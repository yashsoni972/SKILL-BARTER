const Session = require('../models/Session');

exports.createSession = async (req, res) => {
  try {
    const { requestId, partnerUserId, skill, date, time, location, notes } = req.body;
    const session = new Session({
      requestId,
      hostUserId: req.user.userId,
      partnerUserId,
      skill,
      date,
      time,
      location: location || 'Online (Google Meet)',
      notes: notes || ''
    });
    await session.save();
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
    }).populate('hostUserId partnerUserId', 'name email location');
    res.json(sessions);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
