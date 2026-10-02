const mongoose = require('mongoose');

const SessionSchema = new mongoose.Schema({
  requestId: { type: mongoose.Schema.Types.ObjectId, ref: 'ExchangeRequest' },
  hostUserId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  partnerUserId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  skill: { type: String, required: true },
  date: { type: String, required: true },
  time: { type: String, required: true },
  // The agreed day name and the start/end times, so both members see the same
  // "Wednesday, 10:00 - 11:00" without either of them having to do the maths.
  dayOfWeek: { type: String, default: '' },
  startTime: { type: String, default: '' },
  endTime: { type: String, default: '' },
  // Google Meet is the only delivery medium. When the host has not pasted an
  // existing room the app falls back to meet.google.com/new, which creates a real
  // room on demand instead of faking a link that would never open.
  meetLink: { type: String, default: '' },
  location: { type: String, default: 'Online (Google Meet)' },
  notes: { type: String, default: '' },
  durationHours: { type: Number, default: 1 },
  status: { type: String, enum: ['scheduled', 'completed', 'cancelled'], default: 'scheduled' }
});

module.exports = mongoose.model('Session', SessionSchema);
