const mongoose = require('mongoose');

const HelpRequestSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  skillName: { type: String, required: true },
  title: { type: String, required: true },
  description: { type: String, required: true },
  level: { type: String, enum: ['Beginner', 'Intermediate', 'Advanced'], default: 'Beginner' },
  mode: { type: String, enum: ['Online', 'Offline'], default: 'Online' },
  status: { type: String, enum: ['open', 'closed'], default: 'open' },
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('HelpRequest', HelpRequestSchema);
