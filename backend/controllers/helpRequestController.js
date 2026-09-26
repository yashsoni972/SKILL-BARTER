const HelpRequest = require('../models/HelpRequest');

exports.getHelpRequests = async (req, res) => {
  try {
    const requests = await HelpRequest.find({ status: 'open' })
      .populate('userId', 'name email location rating profileImage')
      .sort({ createdAt: -1 });
    res.json(requests);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.createHelpRequest = async (req, res) => {
  try {
    const { skillName, title, description, level, mode } = req.body;
    const newRequest = new HelpRequest({
      userId: req.user.userId,
      skillName,
      title,
      description,
      level: level || 'Beginner',
      mode: mode || 'Online'
    });
    await newRequest.save();
    res.status(201).json(newRequest);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.closeHelpRequest = async (req, res) => {
  try {
    const request = await HelpRequest.findOneAndUpdate(
      { _id: req.params.id, userId: req.user.userId },
      { status: 'closed' },
      { new: true }
    );
    res.json(request);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
