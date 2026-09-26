const mongoose = require('mongoose');

const SkillListingSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  skillName: { type: String, required: true },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  level: { type: String, enum: ['Beginner', 'Intermediate', 'Advanced'], default: 'Intermediate' },
  mode: { type: String, enum: ['Online', 'Offline'], default: 'Online' },
  hourlyCredits: { type: Number, default: 10 },
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('SkillListing', SkillListingSchema);
