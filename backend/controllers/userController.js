const User = require('../models/User');
const Skill = require('../models/Skill');
const ExchangeRequest = require('../models/ExchangeRequest');
const Session = require('../models/Session');

const PROGRESS_TARGET = 4; // sessions needed per skill to count as "mastered"

// Shapes a user for API output. Both `_id` and `id` are emitted because the
// Android client has to work against older and newer backend revisions.
function toApiUser(u, offered = [], wanted = []) {
  const id = u._id;
  return {
    _id: id,
    id,
    name: u.name,
    email: u.email,
    location: u.location,
    bio: u.bio,
    profileImage: u.profileImage,
    rating: u.rating,
    totalExchanges: u.totalExchanges,
    offeredSkills: offered,
    wantedSkills: wanted
  };
}

// Real learning/teaching numbers, derived from actual completed sessions.
exports.getProgress = async (req, res) => {
  try {
    const me = req.user.userId;

    const taught = await Session.find({ hostUserId: me, status: 'completed' });
    const learned = await Session.find({ partnerUserId: me, status: 'completed' });

    const sumHours = (list) =>
      list.reduce((total, s) => total + (s.durationHours || 0), 0);

    // Group learned sessions per skill so we can show real per-skill progress.
    const bySkill = {};
    for (const s of learned) {
      const key = s.skill || 'General';
      bySkill[key] = (bySkill[key] || 0) + 1;
    }

    // Include wanted skills that have no sessions yet, so the list reflects
    // what the user is trying to learn rather than only what finished.
    const wantedSkills = await Skill.find({ userId: me, type: 'want' });
    const skillNames = new Set(wantedSkills.map((s) => s.skillName));
    Object.keys(bySkill).forEach((n) => skillNames.add(n));

    const skills = Array.from(skillNames).map((name) => {
      const completed = bySkill[name] || 0;
      const percent = Math.min(100, Math.round((completed / PROGRESS_TARGET) * 100));
      return { name, completed, target: PROGRESS_TARGET, percent };
    });
    skills.sort((a, b) => b.percent - a.percent);

    const offered = await Skill.find({ userId: me, type: 'offer' });
    const wanted = await Skill.find({ userId: me, type: 'want' });

    const completedRequests = await ExchangeRequest.countDocuments({
      $or: [{ senderId: me }, { receiverId: me }],
      status: 'completed'
    });

    res.json({
      taughtHours: sumHours(taught),
      learnedHours: sumHours(learned),
      sessionsTaught: taught.length,
      sessionsLearned: learned.length,
      skills,
      completedExchanges: completedRequests,
      offeredSkills: offered.map((s) => s.skillName),
      wantedSkills: wanted.map((s) => s.skillName)
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getProfile = async (req, res) => {
  try {
    const user = await User.findById(req.user.userId).select('-password');
    if (!user) return res.status(404).json({ message: 'User not found' });

    const offeredSkills = await Skill.find({ userId: req.user.userId, type: 'offer' });
    const wantedSkills = await Skill.find({ userId: req.user.userId, type: 'want' });

    res.json({
      user,
      offeredSkills: offeredSkills.map(s => s.skillName),
      wantedSkills: wantedSkills.map(s => s.skillName)
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.updateProfile = async (req, res) => {
  try {
    const { name, location, bio } = req.body;
    const user = await User.findByIdAndUpdate(
      req.user.userId,
      { name, location, bio },
      { new: true }
    ).select('-password');
    res.json(user);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getStats = async (req, res) => {
  try {
    const totalUsers = await User.countDocuments();
    const activeExchanges = await ExchangeRequest.countDocuments({ status: 'accepted' });
    const completedExchanges = await ExchangeRequest.countDocuments({ status: 'completed' });

    res.json({
      totalUsers,
      activeExchanges,
      completedExchanges
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.searchUsers = async (req, res) => {
  try {
    const query = req.query.query || '';
    const users = await User.find({
      _id: { $ne: req.user ? req.user.userId : null },
      $or: [
        { name: { $regex: query, $options: 'i' } },
        { location: { $regex: query, $options: 'i' } }
      ]
    }).select('-password');

    const results = await Promise.all(users.map(async (u) => {
      const offered = await Skill.find({ userId: u._id, type: 'offer' });
      const wanted = await Skill.find({ userId: u._id, type: 'want' });
      return toApiUser(u, offered.map(s => s.skillName), wanted.map(s => s.skillName));
    }));

    res.json(results);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getRecommendedPartners = async (req, res) => {
  try {
    const currentUserId = req.user ? req.user.userId : null;
    let users = [];

    if (currentUserId) {
      users = await User.find({ _id: { $ne: currentUserId } }).select('-password');
    } else {
      users = await User.find().limit(10).select('-password');
    }

    const results = await Promise.all(users.map(async (u) => {
      const offered = await Skill.find({ userId: u._id, type: 'offer' });
      const wanted = await Skill.find({ userId: u._id, type: 'want' });
      return toApiUser(u, offered.map(s => s.skillName), wanted.map(s => s.skillName));
    }));

    res.json(results);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
