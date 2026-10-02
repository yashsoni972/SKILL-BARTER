const ExchangeRequest = require('../models/ExchangeRequest');
const User = require('../models/User');
const Notification = require('../models/Notification');
const Session = require('../models/Session');

const USER_FIELDS = 'name email location rating profileImage bio totalExchanges';

// Statuses that mean "this pair already has a live exchange going on".
const ACTIVE_STATUSES = ['pending', 'accepted'];

/**
 * One place to create in-app notifications. Every event in the exchange
 * lifecycle calls this so the bell always reflects real activity instead of
 * the static list that used to live in the layout.
 */
async function notify(userId, title, message, type) {
  try {
    await Notification.create({ userId, title, message, type });
  } catch (err) {
    // A failed notification must never fail the action that triggered it.
    console.error('notification failed:', err.message);
  }
}

// Attaches `partner` (the *other* user, fully populated) to every request so
// the app never has to guess which side of the request is "the other person".
// `direction` is 'incoming' when the request was sent *to* the caller.
function withPartner(requests, direction) {
  return requests.map((r) => {
    const isIncoming = direction === 'incoming';
    const other = isIncoming ? r.senderId : r.receiverId;
    const raw = r.toObject();
    raw.partner = other && other._id ? other.toObject() : null;
    raw.direction = isIncoming ? 'incoming' : 'outgoing';
    return raw;
  });
}

/**
 * A member pair shares ONE exchange, but the database keeps one row per attempt
 * (a rejected request may be sent again later). Returning every row makes the
 * same person appear several times in the requests list and in the chat list, so
 * only the newest attempt per partner is kept.
 *
 * Callers must pass the rows newest-first, which every query in this controller
 * does with `sort({ createdAt: -1 })`.
 */
function dedupeByPartner(requests, me) {
  const seen = new Set();
  const kept = [];

  for (const r of requests) {
    const sender = r.senderId && r.senderId._id ? r.senderId._id : r.senderId;
    const receiver = r.receiverId && r.receiverId._id ? r.receiverId._id : r.receiverId;
    const other = String(sender) === String(me) ? receiver : sender;
    const key = String(other);

    if (!key || key === 'undefined' || key === 'null' || seen.has(key)) continue;
    seen.add(key);
    kept.push(r);
  }

  return kept;
}

exports.sendRequest = async (req, res) => {
  try {
    const me = req.user.userId;
    const { receiverId, offeredSkill, requestedSkill, message } = req.body;

    // Both skill names are required by the schema. Validating here turns a
    // missing field into a clear 400 instead of a Mongoose 500.
    if (!offeredSkill || !requestedSkill) {
      return res.status(400).json({ message: 'Both an offered skill and a requested skill are required.' });
    }

    if (String(receiverId) === String(me)) {
      return res.status(400).json({ message: 'You cannot send an exchange request to yourself.' });
    }

    const receiver = await User.findById(receiverId);
    if (!receiver) {
      return res.status(404).json({ message: 'That member no longer exists.' });
    }

    // The core rule of the app: a pair of members shares ONE exchange. While a
    // request is pending or already accepted, no new request may be created,
    // because the two of them are already busy talking to each other.
    const existing = await ExchangeRequest.findOne({
      $or: [
        { senderId: me, receiverId },
        { senderId: receiverId, receiverId: me }
      ],
      status: { $in: ACTIVE_STATUSES }
    });

    if (existing) {
      const iAmSender = String(existing.senderId) === String(me);
      return res.status(409).json({
        message: iAmSender
          ? `You already sent a request to ${receiver.name}.`
          : `${receiver.name} already sent you a request. Open it in Requests.`,
        status: existing.status,
        existingRequestId: existing._id,
        // Tells the app to open the chat instead of the send-request form.
        openChat: existing.status === 'accepted'
      });
    }

    const newRequest = new ExchangeRequest({
      senderId: me,
      receiverId,
      offeredSkill,
      requestedSkill,
      message: message || 'Hi! I would like to exchange skills with you.'
    });
    await newRequest.save();

    const sender = await User.findById(me);
    await notify(
      receiverId,
      'New Skill Request',
      `${sender ? sender.name : 'Someone'} wants to exchange "${requestedSkill || 'a skill'}" for "${offeredSkill || 'a skill'}".`,
      'request'
    );

    res.status(201).json(newRequest);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getIncomingRequests = async (req, res) => {
  try {
    const requests = await ExchangeRequest.find({ receiverId: req.user.userId })
      .populate('senderId', USER_FIELDS)
      .populate('receiverId', USER_FIELDS)
      .sort({ createdAt: -1 });
    res.json(withPartner(dedupeByPartner(requests, req.user.userId), 'incoming'));
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getOutgoingRequests = async (req, res) => {
  try {
    const requests = await ExchangeRequest.find({ senderId: req.user.userId })
      .populate('senderId', USER_FIELDS)
      .populate('receiverId', USER_FIELDS)
      .sort({ createdAt: -1 });
    res.json(withPartner(dedupeByPartner(requests, req.user.userId), 'outgoing'));
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

/**
 * Every exchange the caller has ever taken part in, with the partner, the skill
 * that was actually taught, the session date and whether the caller has already
 * reviewed the partner. This is what the "My Exchanges" screen renders, so it
 * has to be readable in one call rather than stitched together on the device.
 */
exports.getMyExchanges = async (req, res) => {
  try {
    const me = req.user.userId;

    const requests = await ExchangeRequest.find({
      $or: [{ senderId: me }, { receiverId: me }],
      status: { $in: ['accepted', 'completed'] }
    })
      .populate('senderId', USER_FIELDS)
      .populate('receiverId', USER_FIELDS)
      .sort({ createdAt: -1 });

    const exchanges = [];

    for (const r of dedupeByPartner(requests, me)) {
      const iAmSender = String(r.senderId && r.senderId._id) === String(me);
      const partner = iAmSender ? r.receiverId : r.senderId;
      if (!partner || !partner._id) continue;

      const session = await Session.findOne({ requestId: r._id }).sort({ createdAt: -1 });

      // What the caller taught is the skill *they* offered; what they learned is
      // the skill the partner offered.
      const taught = iAmSender ? r.offeredSkill : r.requestedSkill;
      const learned = iAmSender ? r.requestedSkill : r.offeredSkill;

      exchanges.push({
        requestId: r._id,
        status: r.status,
        direction: iAmSender ? 'outgoing' : 'incoming',
        partner: {
          _id: partner._id,
          id: partner._id,
          name: partner.name,
          location: partner.location,
          profileImage: partner.profileImage,
          rating: partner.rating,
          totalExchanges: partner.totalExchanges
        },
        taughtSkill: taught || '',
        learnedSkill: learned || '',
        sessionId: session ? session._id : null,
        sessionDate: session ? session.date : null,
        sessionTime: session ? session.time : null,
        sessionLocation: session ? session.location : 'Online (Google Meet)',
        durationHours: session ? session.durationHours : null,
        createdAt: r.createdAt,
        // Lets the app show "Rate" instead of "Rated" for partners already reviewed.
        canRate: r.status === 'completed'
      });
    }

    exchanges.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    res.json(exchanges);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

/**
 * Tells the app whether the caller already has a live exchange with one member,
 * so the "Send Request" button can become "Chat" instead of letting the user fill
 * in a form that the server will only reject.
 */
exports.getRelationship = async (req, res) => {
  try {
    const me = req.user.userId;
    const otherId = req.params.userId;

    if (String(otherId) === String(me)) {
      return res.status(400).json({ message: 'This is your own profile.' });
    }

    const existing = await ExchangeRequest.findOne({
      $or: [
        { senderId: me, receiverId: otherId },
        { senderId: otherId, receiverId: me }
      ]
    }).sort({ createdAt: -1 });

    if (!existing) {
      return res.json({ status: 'none', requestId: null, canChat: false, canRequest: true });
    }

    const live = existing.status === 'pending' || existing.status === 'accepted';

    res.json({
      status: existing.status,
      requestId: existing._id,
      // Accepted (or already completed) means there is a conversation to open.
      canChat: existing.status === 'accepted' || existing.status === 'completed',
      // A rejected or completed pair may start a fresh exchange.
      canRequest: !live || existing.status === 'completed',
      direction: String(existing.senderId) === String(me) ? 'outgoing' : 'incoming'
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.acceptRequest = async (req, res) => {
  try {
    const request = await ExchangeRequest.findById(req.params.id);
    if (!request) {
      return res.status(404).json({ message: 'Exchange request not found' });
    }
    if (String(request.receiverId) !== String(req.user.userId)) {
      return res.status(403).json({ message: 'Only the person receiving a request can accept it.' });
    }

    request.status = 'accepted';
    await request.save();

    const me = await User.findById(req.user.userId);
    await notify(
      request.senderId,
      'Request Accepted',
      `${me ? me.name : 'Your partner'} accepted your skill exchange. Start chatting to plan the session.`,
      'request'
    );

    res.json(request);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.rejectRequest = async (req, res) => {
  try {
    const request = await ExchangeRequest.findById(req.params.id);
    if (!request) {
      return res.status(404).json({ message: 'Exchange request not found' });
    }
    if (String(request.receiverId) !== String(req.user.userId)) {
      return res.status(403).json({ message: 'Only the person receiving a request can reject it.' });
    }

    request.status = 'rejected';
    await request.save();

    const me = await User.findById(req.user.userId);
    await notify(
      request.senderId,
      'Request Declined',
      `${me ? me.name : 'Your partner'} declined the skill exchange request.`,
      'request'
    );

    res.json(request);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.completeRequest = async (req, res) => {
  try {
    const request = await ExchangeRequest.findById(req.params.id);
    if (!request) {
      return res.status(404).json({ message: 'Exchange request not found' });
    }
    const me = req.user.userId;
    const isParticipant =
      String(request.senderId) === String(me) || String(request.receiverId) === String(me);
    if (!isParticipant) {
      return res.status(403).json({ message: 'This exchange does not belong to you' });
    }

    request.status = 'completed';
    await request.save();

    const partnerId = String(request.senderId) === String(me) ? request.receiverId : request.senderId;
    const meUser = await User.findById(me);
    await notify(
      partnerId,
      'Exchange Completed',
      `${meUser ? meUser.name : 'Your partner'} marked the skill exchange as completed. Leave a review!`,
      'session'
    );

    res.json(request);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.notify = notify;