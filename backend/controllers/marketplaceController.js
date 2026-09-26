const SkillListing = require('../models/SkillListing');

exports.getListings = async (req, res) => {
  try {
    const listings = await SkillListing.find()
      .populate('userId', 'name email location rating profileImage')
      .sort({ createdAt: -1 });
    res.json(listings);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.createListing = async (req, res) => {
  try {
    const { skillName, title, description, level, mode, hourlyCredits } = req.body;
    const newListing = new SkillListing({
      userId: req.user.userId,
      skillName,
      title,
      description: description || '',
      level: level || 'Intermediate',
      mode: mode || 'Online',
      hourlyCredits: hourlyCredits || 10
    });
    await newListing.save();
    res.status(201).json(newListing);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.deleteListing = async (req, res) => {
  try {
    await SkillListing.findOneAndDelete({ _id: req.params.id, userId: req.user.userId });
    res.json({ message: 'Listing removed' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
