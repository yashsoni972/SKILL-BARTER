const Badge = require('../models/Badge');
const UserBadge = require('../models/UserBadge');

exports.getBadges = async (req, res) => {
  try {
    const defaultBadges = [
      { id: 'b1', title: 'First Exchange', icon: '🥇', description: 'Completed your first skill exchange session' },
      { id: 'b2', title: '7 Day Streak', icon: '🔥', description: 'Active skill barterer for 7 consecutive days' },
      { id: 'b3', title: 'Top Mentor', icon: '🧑‍🏫', description: 'Taught over 10 hours of skill sessions' },
      { id: 'b4', title: '5-Star Mentor', icon: '⭐', description: 'Maintained a perfect 5-star rating' },
      { id: 'b5', title: 'Fast Responder', icon: '⚡', description: 'Responds to exchange requests in under 15 minutes' }
    ];
    res.json(defaultBadges);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
