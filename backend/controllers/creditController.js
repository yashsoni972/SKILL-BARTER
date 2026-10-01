const SkillCredit = require('../models/SkillCredit');
const CreditTransaction = require('../models/CreditTransaction');
const Notification = require('../models/Notification');

// The whole economy of the app in two numbers: teaching earns, learning costs.
const CREDITS_PER_HOUR = 10;

// New members get this once so they can start learning before they have taught.
const WELCOME_CREDITS = 40;

// Credits can never go negative. A learner who runs out still gets their
// session, they just stop accruing a debt.
const MIN_BALANCE = 0;

async function getOrCreate(userId) {
  let credit = await SkillCredit.findOne({ userId });
  if (!credit) {
    credit = await SkillCredit.create({
      userId,
      balance: 0,
      earnedTotal: 0,
      spentTotal: 0
    });
  }
  return credit;
}

/**
 * Moves `amount` credits in or out of one user's wallet and writes the matching
 * transaction row. Returns the new balance. Shared by the session-completion
 * flow and by the manual earn/spend endpoints so the ledger can only ever be
 * written in one way.
 */
async function applyChange(userId, amount, description) {
  const credit = await getOrCreate(userId);
  const isEarn = amount >= 0;

  let balance = credit.balance + amount;
  if (balance < MIN_BALANCE) balance = MIN_BALANCE;

  const applied = balance - credit.balance; // never let the clamp hide a real debit
  credit.balance = balance;
  if (isEarn) credit.earnedTotal += Math.abs(applied);
  else credit.spentTotal += Math.abs(applied);
  credit.updatedAt = new Date();
  await credit.save();

  await CreditTransaction.create({
    userId,
    amount: applied,
    type: isEarn ? 'earn' : 'spend',
    description
  });

  return balance;
}

/**
 * Signs a new member up with their welcome credits. Called from register so the
 * balance exists before the first request, instead of appearing out of nowhere
 * the first time the Credits screen is opened.
 */
async function grantWelcomeCredits(userId) {
  await applyChange(userId, WELCOME_CREDITS, `Welcome gift for joining Skill Barter`);
}

async function notifyBalance(userId, balance, amount) {
  try {
    const gained = amount >= 0;
    await Notification.create({
      userId,
      title: gained ? 'Credits Earned' : 'Credits Used',
      message: gained
        ? `You earned ${amount} credits for teaching. New balance: ${balance}.`
        : `You used ${Math.abs(amount)} credits for learning. New balance: ${balance}.`,
      type: 'credits'
    });
  } catch (err) {
    console.error('credit notification failed:', err.message);
  }
}

exports.getCredits = async (req, res) => {
  try {
    const credit = await getOrCreate(req.user.userId);
    const history = await CreditTransaction.find({ userId: req.user.userId })
      .sort({ createdAt: -1 })
      .limit(50);
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
    const amount = Math.abs(Number(req.body.amount) || CREDITS_PER_HOUR);
    const balance = await applyChange(
      req.user.userId,
      amount,
      req.body.description || `Taught a skill exchange session (+${amount} credits)`
    );
    await notifyBalance(req.user.userId, balance, amount);
    res.json({ balance });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.spendCredits = async (req, res) => {
  try {
    const amount = Math.abs(Number(req.body.amount) || CREDITS_PER_HOUR);
    const balance = await applyChange(
      req.user.userId,
      -amount,
      req.body.description || `Learned a skill exchange session (-${amount} credits)`
    );
    await notifyBalance(req.user.userId, balance, -amount);
    res.json({ balance });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.CREDITS_PER_HOUR = CREDITS_PER_HOUR;
exports.applyChange = applyChange;
exports.grantWelcomeCredits = grantWelcomeCredits;