const Skill = require('../models/Skill');

exports.addSkill = async (req, res) => {
  try {
    const { skillName, category, type, level, description } = req.body;
    const newSkill = new Skill({
      userId: req.user.userId,
      skillName,
      category: category || 'General',
      type,
      level: level || 'Intermediate',
      description: description || ''
    });
    await newSkill.save();
    res.status(201).json(newSkill);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.updateSkills = async (req, res) => {
  try {
    const { offeredSkills, wantedSkills } = req.body;
    const userId = req.user.userId;

    await Skill.deleteMany({ userId });

    const skillsToInsert = [];
    if (offeredSkills && Array.isArray(offeredSkills)) {
      offeredSkills.forEach(s => {
        skillsToInsert.push({ userId, skillName: s, type: 'offer' });
      });
    }
    if (wantedSkills && Array.isArray(wantedSkills)) {
      wantedSkills.forEach(s => {
        skillsToInsert.push({ userId, skillName: s, type: 'want' });
      });
    }

    if (skillsToInsert.length > 0) {
      await Skill.insertMany(skillsToInsert);
    }

    res.json({ message: 'Skills updated successfully' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.deleteSkill = async (req, res) => {
  try {
    await Skill.findOneAndDelete({ _id: req.params.id, userId: req.user.userId });
    res.json({ message: 'Skill removed' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
