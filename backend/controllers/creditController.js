const SkillCredit = require('../models/SkillCredit');
const CreditTransaction = require('../models/CreditTransaction');

exports.getCredits = async (req, res) => {
  try {
    let credit = await SkillCredit.findOne({ userId: req.user.userId });
    if (!credit) {
      credit = new SkillCredit({ userId: req.user.userId, balance: 40, earnedTotal: 40, spentTotal: 0 });
      await credit.save();
    }
    const history = await CreditTransaction.find({ userId: req.user.userId }).sort({ createdAt: -1 });
    res.json({
      balance: credit.balance,
      earnedTotal: credit.earnedTotal,
      spentTotal: credit.spentTotal,
      history
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.earnCredits = async (req, res) => {
  try {
    const { amount, description } = req.body;
    const earnAmount = amount || 10;
    let credit = await SkillCredit.findOne({ userId: req.user.userId });
    if (!credit) {
      credit = new SkillCredit({ userId: req.user.userId, balance: 40, earnedTotal: 40, spentTotal: 0 });
    }
    credit.balance += earnAmount;
    credit.earnedTotal += earnAmount;
    await credit.save();

    const transaction = new CreditTransaction({
      userId: req.user.userId,
      amount: earnAmount,
      type: 'earn',
      description: description || 'Taught a skill exchange session (+10 credits/hr)'
    });
    await transaction.save();

    res.json({ balance: credit.balance, transaction });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.spendCredits = async (req, res) => {
  try {
    const { amount, description } = req.body;
    const spendAmount = amount || 10;
    let credit = await SkillCredit.findOne({ userId: req.user.userId });
    if (!credit) {
      credit = new SkillCredit({ userId: req.user.userId, balance: 40, earnedTotal: 40, spentTotal: 0 });
    }
    if (credit.balance < spendAmount) {
      return res.status(400).json({ message: 'Insufficient Skill Credits' });
    }
    credit.balance -= spendAmount;
    credit.spentTotal += spendAmount;
    await credit.save();

    const transaction = new CreditTransaction({
      userId: req.user.userId,
      amount: -spendAmount,
      type: 'spend',
      description: description || 'Learned a skill exchange session (-10 credits/hr)'
    });
    await transaction.save();

    res.json({ balance: credit.balance, transaction });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
