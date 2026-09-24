const mongoose = require('mongoose');

const SkillSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  skillName: { type: String, required: true },
  category: { type: String, default: 'General' },
  type: { type: String, enum: ['offer', 'want'], required: true },
  level: { type: String, enum: ['Beginner', 'Intermediate', 'Advanced'], default: 'Intermediate' },
  description: { type: String, default: '' }
});

module.exports = mongoose.model('Skill', SkillSchema);
