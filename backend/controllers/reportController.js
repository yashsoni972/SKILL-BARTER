const Report = require('../models/Report');

exports.createReport = async (req, res) => {
  try {
    const { reportedUserId, reason, details } = req.body;
    const newReport = new Report({
      reporterId: req.user.userId,
      reportedUserId,
      reason,
      details: details || ''
    });
    await newReport.save();
    res.status(201).json(newReport);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getReports = async (req, res) => {
  try {
    const reports = await Report.find()
      .populate('reporterId reportedUserId', 'name email location')
      .sort({ createdAt: -1 });
    res.json(reports);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
