const Message = require('../models/Message');
const ExchangeRequest = require('../models/ExchangeRequest');

const USER_FIELDS = 'name email location rating profileImage bio totalExchanges';

// Returns one entry per distinct partner for the given user, containing the most
// recent message exchanged with them. Drives the Chats tab so it lists real
// conversations instead of every registered user.
exports.getConversations = async (req, res) => {
  try {
    const me = req.user.userId;

    const requests = await ExchangeRequest.find({
      $or: [{ senderId: me }, { receiverId: me }],
      status: { $in: ['accepted', 'completed'] }
    })
      .populate('senderId', USER_FIELDS)
      .populate('receiverId', USER_FIELDS)
      .sort({ updatedAt: -1, createdAt: -1 });

    const conversations = [];

    // A pair can have several request rows (rejected then retried, completed then
    // started again). Only the newest one per partner may produce a chat entry,
    // otherwise the same person is listed more than once.
    const seenPartners = new Set();

    for (const r of requests) {
      const partner = r.senderId && r.senderId._id && String(r.senderId._id) === String(me)
        ? r.receiverId
        : r.senderId;

      if (!partner || !partner._id) continue;

      const partnerKey = String(partner._id);
      if (seenPartners.has(partnerKey)) continue;
      seenPartners.add(partnerKey);

      const msgs = await Message.find({
        $or: [
          { senderId: me, receiverId: partner._id },
          { senderId: partner._id, receiverId: me }
        ]
      }).sort({ createdAt: -1 }).limit(1);

      const last = msgs[0];

      conversations.push({
        partner: {
          _id: partner._id,
          id: partner._id,
          name: partner.name,
          email: partner.email,
          location: partner.location,
          profileImage: partner.profileImage,
          bio: partner.bio,
          rating: partner.rating,
          totalExchanges: partner.totalExchanges
        },
        requestId: r._id,
        status: r.status,
        lastMessage: last ? last.message : '',
        lastMessageAt: last ? last.createdAt : null,
        lastMessageMine: last ? String(last.senderId) === String(me) : false,
        unreadCount: await Message.countDocuments({
          senderId: partner._id,
          receiverId: me,
          read: false
        })
      });
    }

    // Newest conversation first.
    conversations.sort((a, b) => {
      const at = a.lastMessageAt ? new Date(a.lastMessageAt).getTime() : 0;
      const bt = b.lastMessageAt ? new Date(b.lastMessageAt).getTime() : 0;
      return bt - at;
    });

    res.json(conversations);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
