const mongoose = require('mongoose');

const BadgeSchema = new mongoose.Schema({
  title: { type: String, required: true },
  icon: { type: String, required: true }, // e.g. "🥇", "🔥", "🧑‍🏫", "⭐", "⚡"
  description: { type: String, required: true },
  category: { type: String, default: 'Achievement' }
});

module.exports = mongoose.model('Badge', BadgeSchema);
