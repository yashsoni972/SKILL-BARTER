const mongoose = require('mongoose');

const SkillCreditSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, unique: true },
  balance: { type: Number, default: 40 },
  earnedTotal: { type: Number, default: 40 },
  spentTotal: { type: Number, default: 0 },
  updatedAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('SkillCredit', SkillCreditSchema);
