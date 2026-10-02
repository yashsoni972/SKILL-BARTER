const User = require('../models/User');
const Skill = require('../models/Skill');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const creditService = require('./creditController');

/**
 * Builds the user payload sent with register/login.
 *
 * Skills live in their own collection, not on the User document, so they have to
 * be looked up separately. They must be included here because the app stores this
 * object as the signed-in user and every skill-driven screen (profile, discover,
 * send request) reads its lists from it.
 */
async function buildUserPayload(user) {
  const skills = await Skill.find({ userId: user._id }).lean();

  return {
    _id: user._id,
    id: user._id,
    name: user.name,
    email: user.email,
    location: user.location,
    bio: user.bio,
    rating: user.rating,
    totalExchanges: user.totalExchanges,
    offeredSkills: skills.filter(s => s.type === 'offer').map(s => s.skillName),
    wantedSkills: skills.filter(s => s.type === 'want').map(s => s.skillName)
  };
}

exports.register = async (req, res) => {
  try {
    const { name, email, password, location, bio } = req.body;
    let user = await User.findOne({ email });
    if (user) {
      return res.status(400).json({ message: 'User already exists with this email' });
    }

    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    user = new User({
      name,
      email,
      password: hashedPassword,
      location: location || 'Anand, Gujarat',
      bio: bio || 'Passionate about web development, design and learning new technologies.'
    });

    await user.save();

    // Every new member starts with credits so they can learn before they have
    // taught anything.
    try {
      await creditService.grantWelcomeCredits(user._id);
    } catch (err) {
      console.error('welcome credits failed:', err.message);
    }

    const token = jwt.sign(
      { userId: user._id, email: user.email },
      process.env.JWT_SECRET || 'skill_barter_super_secret_jwt_key_2026_xyz',
      { expiresIn: '30d' }
    );

    res.status(201).json({
      token,
      user: await buildUserPayload(user)
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.login = async (req, res) => {
  try {
    const { email, password } = req.body;
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(400).json({ message: 'Invalid credentials' });
    }

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(400).json({ message: 'Invalid credentials' });
    }

    const token = jwt.sign(
      { userId: user._id, email: user.email },
      process.env.JWT_SECRET || 'skill_barter_super_secret_jwt_key_2026_xyz',
      { expiresIn: '30d' }
    );

    res.json({
      token,
      user: await buildUserPayload(user)
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
