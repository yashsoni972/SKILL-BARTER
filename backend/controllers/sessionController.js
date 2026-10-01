const Session = require('../models/Session');
const ExchangeRequest = require('../models/ExchangeRequest');
const User = require('../models/User');
const Notification = require('../models/Notification');
const creditService = require('./creditController');

// Every exchange happens over Google Meet, so the medium is never a choice.
const MEDIUM = 'Online (Google Meet)';

/**
 * Records a finished session so the progress dashboard has real numbers, and
 * settles the credit economy for both sides: the teacher earns, the learner
 * spends. Credits are only moved once per request so a double tap on "Mark as
 * Completed" cannot mint credits.
 */
exports.completeSession = async (req, res) => {
  try {
    const { requestId, durationHours, skill, iTaught } = req.body;
    const me = req.user.userId;

    const hours = Number(durationHours);
    if (!Number.isFinite(hours) || hours <= 0) {
      return res.status(400).json({ message: 'durationHours must be a positive number' });
    }

    if (!requestId) {
      return res.status(400).json({ message: 'requestId is required' });
    }

    const request = await ExchangeRequest.findById(requestId);
    if (!request) {
      return res.status(404).json({ message: 'Exchange request not found' });
    }

    const iAmSender = String(request.senderId) === String(me);
    if (!iAmSender && String(request.receiverId) !== String(me)) {
      return res.status(403).json({ message: 'This exchange does not belong to you' });
    }

    const alreadyDone = await Session.findOne({ requestId: request._id, status: 'completed' });
    if (alreadyDone) {
      return res.status(409).json({ message: 'This exchange session was already completed.' });
    }

    const partnerUserId = iAmSender ? request.receiverId : request.senderId;

    request.status = 'completed';
    await request.save();

    // "I taught" means I hosted; otherwise I was the learner.
    const session = await Session.create({
      requestId: request._id,
      hostUserId: iTaught ? me : partnerUserId,
      partnerUserId: iTaught ? partnerUserId : me,
      skill: skill || 'Skill exchange',
      date: new Date().toISOString().slice(0, 10),
      time: new Date().toTimeString().slice(0, 5),
      location: MEDIUM,
      durationHours: hours,
      status: 'completed'
    });

    const meUser = await User.findById(me);
    const partnerUser = await User.findById(partnerUserId);

    // Each side's balance moves by the opposite amount: whoever taught earns,
    // whoever learned spends, for the same number of hours.
    const points = creditService.CREDITS_PER_HOUR * hours;
    const myChange = iTaught ? points : -points;
    const partnerChange = iTaught ? -points : points;

    const myBalance = await creditService.applyChange(
      me,
      myChange,
      `${iTaught ? 'Taught' : 'Learned'} "${session.skill}" (${hours}h)`
    );
    await creditService.applyChange(
      partnerUserId,
      partnerChange,
      `${iTaught ? 'Learned' : 'Taught'} "${session.skill}" (${hours}h)`
    );

    await Promise.all([User.updateOne({ _id: me }, { $inc: { totalExchanges: 1 } })]);
    await Promise.all([User.updateOne({ _id: partnerUserId }, { $inc: { totalExchanges: 1 } })]);

    try {
      await Notification.create({
        userId: partnerUserId,
        title: 'Exchange Completed',
        message: `${meUser ? meUser.name : 'Your partner'} completed "${session.skill}" over Google Meet. Leave a review!`,
        type: 'session'
      });
      await Notification.create({
        userId: me,
        title: iTaught ? 'Credits Earned' : 'Credits Used',
        message: iTaught
          ? `You earned ${points} credits for teaching. Balance: ${myBalance}.`
          : `You used ${points} credits for learning. Balance: ${myBalance}.`,
        type: 'credits'
      });
    } catch (err) {
      console.error('session notification failed:', err.message);
    }

    res.status(201).json({
      session,
      credits: { balance: myBalance, change: myChange },
      partnerName: partnerUser ? partnerUser.name : null
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

/**
 * Schedules a session for an accepted exchange. The date, time and medium are
 * real because they are what the exchange details screen displays.
 */
exports.createSession = async (req, res) => {
  try {
    const me = req.user.userId;
    const { requestId, partnerUserId, skill, date, time, notes } = req.body;

    if (!skill || !date || !time) {
      return res.status(400).json({ message: 'skill, date and time are required' });
    }

    let partnerId = partnerUserId;
    if (requestId) {
      const request = await ExchangeRequest.findById(requestId);
      if (!request) {
        return res.status(404).json({ message: 'Exchange request not found' });
      }
      const iAmSender = String(request.senderId) === String(me);
      if (!iAmSender && String(request.receiverId) !== String(me)) {
        return res.status(403).json({ message: 'This exchange does not belong to you' });
      }
      partnerId = iAmSender ? request.receiverId : request.senderId;
    }

    if (!partnerId) {
      return res.status(400).json({ message: 'partnerUserId is required' });
    }

    const session = await Session.create({
      requestId,
      hostUserId: me,
      partnerUserId: partnerId,
      skill,
      date,
      time,
      location: MEDIUM,
      notes: notes || '',
      durationHours: 1,
      status: 'scheduled'
    });

    const meUser = await User.findById(me);
    try {
      await Notification.create({
        userId: partnerId,
        title: 'Session Scheduled',
        message: `${meUser ? meUser.name : 'Your partner'} scheduled "${skill}" on ${date} at ${time} over Google Meet.`,
        type: 'session'
      });
    } catch (err) {
      console.error('session notification failed:', err.message);
    }

    res.status(201).json(session);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getSessions = async (req, res) => {
  try {
    const userId = req.user.userId;
    const sessions = await Session.find({
      $or: [{ hostUserId: userId }, { partnerUserId: userId }]
    })
      .populate('hostUserId partnerUserId', 'name email location profileImage rating')
      .sort({ date: -1, createdAt: -1 });
    res.json(sessions);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.MEDIUM = MEDIUM;