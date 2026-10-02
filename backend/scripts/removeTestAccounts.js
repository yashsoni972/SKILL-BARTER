/**
 * Removes the throwaway accounts created while testing so that only genuinely
 * registered members remain.
 *
 * Run it on the server, because the machine used for development cannot reach
 * MongoDB Atlas directly:
 *
 *   Render dashboard -> your service -> Shell
 *   node scripts/removeTestAccounts.js --dry-run     # preview, changes nothing
 *   node scripts/removeTestAccounts.js               # delete test accounts only
 *   node scripts/removeTestAccounts.js --include-seed  # also delete seed accounts
 *
 * The demo accounts created by seed.js are NOT removed by default, because the
 * account owner may well be signed in as one of them right now and deleting it
 * would lose their real data. Pass --include-seed once you are sure you are
 * using your own registered account.
 *
 * Every collection that references a user is cleaned up too, otherwise deleted
 * members would leave orphaned requests, messages and sessions behind.
 */
require('dotenv').config();
const mongoose = require('mongoose');

const MONGO_URI = process.env.MONGO_URI || 'mongodb://localhost:27017/skillbarter';
const DRY_RUN = process.argv.includes('--dry-run');
const INCLUDE_SEED = process.argv.includes('--include-seed');

// Demo accounts created by seed.js when the database was first populated.
// yash@example.com is listed first because it is the one most likely to belong
// to the account owner.
const SEED_EMAILS = [
  'yash@example.com',
  'riya@example.com',
  'aman@example.com',
  'neha@example.com'
];

/**
 * Addresses produced by the automated end-to-end checks. Every pattern is
 * anchored so it can only match an address this project generated, never a real
 * member: an unanchored pattern such as /^raj/i would also delete
 * rajesh.kumar@gmail.com.
 */
const TEST_EMAIL_PATTERNS = [
  /^e2ea\./i,
  /^e2eb\./i,
  /^e2e\./i,
  /^shape\d*\./i,
  /^shape2?\./i,
  /^zfa\./i,
  /^zfb\./i,
  /^fa\./i,
  /^fb\./i,
  /^diagnostic/i,
  /^dummy/i,
  /^test\d*@/i,
  /@t\.com$/i
];

async function run() {
  await mongoose.connect(MONGO_URI);
  console.log('Connected to MongoDB.');

  const User = mongoose.model('User', new mongoose.Schema({}, { strict: false }), 'User');

  const all = await User.find({}).lean();
  console.log(`Total accounts currently in the database: ${all.length}\n`);

  const isSeed = u => SEED_EMAILS.includes(String(u.email || '').toLowerCase());
  const isTest = u => TEST_EMAIL_PATTERNS.some(re => re.test(String(u.email || '')));

  const doomed = all.filter(u => (INCLUDE_SEED ? isSeed(u) || isTest(u) : isTest(u)));

  const seedOnly = all.filter(u => isSeed(u) && !isTest(u));

  if (seedOnly.length > 0) {
    console.log('Demo accounts left untouched (pass --include-seed to remove these):');
    seedOnly.forEach(u => console.log(`  - ${u.name || '(no name)'}  <${u.email}>`));
    console.log('');
  }

  if (doomed.length === 0) {
    console.log('Nothing to remove - no test accounts found.');
    await mongoose.disconnect();
    return;
  }

  console.log(`The following accounts will be removed:`);
  doomed.forEach(u => console.log(`  - ${u.name || '(no name)'}  <${u.email}>`));
  console.log('');

  if (DRY_RUN) {
    console.log('Dry run only. Nothing was deleted. Re-run without --dry-run to apply.');
    await mongoose.disconnect();
    return;
  }

  const ids = doomed.map(u => u._id);
  const inIds = { $in: ids };
  const either = { $in: ids };

  const results = await Promise.all([
    User.deleteMany({ _id: inIds }),
    mongoose.model('Skill', new mongoose.Schema({}, { strict: false }), 'Skill').deleteMany({ userId: either }),
    mongoose.model('ExchangeRequest', new mongoose.Schema({}, { strict: false }), 'ExchangeRequest')
      .deleteMany({ $or: [{ senderId: either }, { receiverId: either }] }),
    mongoose.model('Message', new mongoose.Schema({}, { strict: false }), 'Message')
      .deleteMany({ $or: [{ senderId: either }, { receiverId: either }] }),
    mongoose.model('Session', new mongoose.Schema({}, { strict: false }), 'Session')
      .deleteMany({ $or: [{ hostUserId: either }, { partnerUserId: either }] }),
    mongoose.model('Notification', new mongoose.Schema({}, { strict: false }), 'Notification').deleteMany({ userId: either }),
    mongoose.model('SkillCredit', new mongoose.Schema({}, { strict: false }), 'SkillCredit').deleteMany({ userId: either }),
    mongoose.model('CreditTransaction', new mongoose.Schema({}, { strict: false }), 'CreditTransaction').deleteMany({ userId: either }),
    mongoose.model('Review', new mongoose.Schema({}, { strict: false }), 'Review')
      .deleteMany({ $or: [{ reviewerId: either }, { reviewedUserId: either }] }),
    mongoose.model('Availability', new mongoose.Schema({}, { strict: false }), 'Availability').deleteMany({ userId: either }),
    mongoose.model('UserBadge', new mongoose.Schema({}, { strict: false }), 'UserBadge').deleteMany({ userId: either }),
    mongoose.model('SkillListing', new mongoose.Schema({}, { strict: false }), 'SkillListing').deleteMany({ userId: either }),
    mongoose.model('HelpRequest', new mongoose.Schema({}, { strict: false }), 'HelpRequest').deleteMany({ userId: either }),
    mongoose.model('Report', new mongoose.Schema({}, { strict: false }), 'Report').deleteMany({ reportedUserId: either })
  ]);

  const labels = [
    'users', 'skills', 'requests', 'messages', 'sessions', 'notifications',
    'creditBalances', 'creditTransactions', 'reviews', 'availability',
    'userBadges', 'listings', 'helpRequests', 'reports'
  ];

  console.log('Deleted:');
  results.forEach((r, i) => console.log(`  ${labels[i]}: ${r.deletedCount}`));

  const remaining = await User.find({}).lean();
  console.log(`\nRemaining accounts: ${remaining.length}`);
  remaining.forEach(u => console.log(`  - ${u.name || '(no name)'}  <${u.email}>`));

  await mongoose.disconnect();
}

run().catch(err => {
  console.error('Cleanup failed:', err.message);
  process.exit(1);
});