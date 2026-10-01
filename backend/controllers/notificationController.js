const Notification = require('../models/Notification');

exports.getNotifications = async (req, res) => {
  try {
    const notifications = await Notification.find({ userId: req.user.userId })
      .sort({ createdAt: -1 })
      .limit(100);
    res.json(notifications);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

/**
 * Drives the red dot on the bell. A separate call keeps the badge cheap so the
 * home screen does not have to download the whole notification list.
 */
exports.getUnreadCount = async (req, res) => {
  try {
    const count = await Notification.countDocuments({ userId: req.user.userId, isRead: false });
    res.json({ count });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.markAllRead = async (req, res) => {
  try {
    await Notification.updateMany(
      { userId: req.user.userId, isRead: false },
      { isRead: true }
    );
    res.json({ message: 'All notifications marked as read' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};