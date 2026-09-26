const mongoose = require('mongoose');

const AvailabilitySchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  day: { type: String, required: true }, // e.g. "Monday", "Wednesday", "Saturday"
  startTime: { type: String, required: true }, // e.g. "18:00" or "6:00 PM"
  endTime: { type: String, required: true }, // e.g. "21:00" or "9:00 PM"
  mode: { type: String, enum: ['Online', 'Offline'], default: 'Online' }
});

module.exports = mongoose.model('Availability', AvailabilitySchema);
