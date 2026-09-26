const mongoose = require('mongoose');

const CreditTransactionSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  amount: { type: Number, required: true }, // e.g. +10 or -10
  type: { type: String, enum: ['earn', 'spend'], required: true },
  description: { type: String, required: true },
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('CreditTransaction', CreditTransactionSchema);
