const mongoose = require('mongoose');

const SessionSchema = new mongoose.Schema({
  requestId: { type: mongoose.Schema.Types.ObjectId, ref: 'ExchangeRequest' },
  hostUserId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  partnerUserId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  skill: { type: String, required: true },
  date: { type: String, required: true },
  time: { type: String, required: true },
  location: { type: String, default: 'Online (Google Meet)' },
  notes: { type: String, default: '' },
  status: { type: String, enum: ['scheduled', 'completed', 'cancelled'], default: 'scheduled' }
});

module.exports = mongoose.model('Session', SessionSchema);
