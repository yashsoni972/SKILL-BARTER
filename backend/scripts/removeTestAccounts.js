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
const { GridFSBucket } = require('mongodb');

const MONGO_URI = process.env.MONGO_URI || 'mongodb://localhost:27017/skillbarter';
const DRY_RUN = process.argv.includes('--dry-run');
const INCLUDE_SEED = process.argv.includes('--include-seed');
const GRIDFS_BUCKET = 'attachments';

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
  /^diagtest/i,
  /^dummy/i,
  /^test\d*@/i,
  /@t\.com$/i
];

async function run() {
  await mongoose.connect(MONGO_URI);
  console.log('Connected to MongoDB.');

  // Mongoose pluralises and lower-cases model names only when the collection is
  // NOT passed explicitly. Passing 'User' created a phantom empty "User"
  // collection, so this script used to report 0 accounts and delete nothing.
  // These names are the real collections in MongoDB.
  const coll = name => mongoose.connection.db.collection(name);

  const all = await coll('users').find({}).toArray();
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

  const ids = doomed.map(u => u._id);
  const inIds = { $in: ids };
  const either = { $in: ids };

  // Shared files are stored in GridFS, so the bytes have to be removed too.
  // Deleting only the metadata rows would leave the real content behind in
  // attachments.files and attachments.chunks forever.
  const bucket = new GridFSBucket(mongoose.connection.db, { bucketName: GRIDFS_BUCKET });
  const sharedFiles = await coll('attachments')
    .find({ $or: [{ uploadedBy: either }, { sharedWith: either }] })
    .toArray();
  const fileBytes = sharedFiles.reduce((total, a) => total + (a.size || 0), 0);

  if (sharedFiles.length > 0) {
    console.log(`Shared files to remove: ${sharedFiles.length} (${(fileBytes / 1024).toFixed(1)} KB in GridFS)`);
    sharedFiles.forEach(a => console.log(`  - ${a.filename}  uploaded by ${a.uploadedBy}`));
    console.log('');
  }

  if (DRY_RUN) {
    console.log('Dry run only. Nothing was deleted. Re-run without --dry-run to apply.');
    await mongoose.disconnect();
    return;
  }

  for (const a of sharedFiles) {
    await new Promise((resolve, reject) => {
      bucket.delete(a.fileId, err => (err ? reject(err) : resolve()));
    });
  }

  const results = await Promise.all([
    coll('users').deleteMany({ _id: inIds }),
    coll('skills').deleteMany({ userId: either }),
    coll('exchangerequests').deleteMany({ $or: [{ senderId: either }, { receiverId: either }] }),
    coll('messages').deleteMany({ $or: [{ senderId: either }, { receiverId: either }] }),
    coll('sessions').deleteMany({ $or: [{ hostUserId: either }, { partnerUserId: either }] }),
    coll('notifications').deleteMany({ userId: either }),
    coll('skillcredits').deleteMany({ userId: either }),
    coll('credittransactions').deleteMany({ userId: either }),
    coll('reviews').deleteMany({ $or: [{ reviewerId: either }, { reviewedUserId: either }] }),
    coll('availabilities').deleteMany({ userId: either }),
    coll('userbadges').deleteMany({ userId: either }),
    coll('skilllistings').deleteMany({ userId: either }),
    coll('helprequests').deleteMany({ userId: either }),
    coll('attachments').deleteMany({ _id: { $in: sharedFiles.map(a => a._id) } })
  ]);

  const labels = [
    'users', 'skills', 'requests', 'messages', 'sessions', 'notifications',
    'creditBalances', 'creditTransactions', 'reviews', 'availability',
    'userBadges', 'listings', 'helpRequests', 'sharedFileMetadata'
  ];

  console.log('Deleted:');
  results.forEach((r, i) => console.log(`  ${labels[i]}: ${r.deletedCount}`));
  console.log(`  sharedFileBytes (GridFS): ${fileBytes}`);

  const remaining = await coll('users').find({}).toArray();
  console.log(`\nRemaining accounts: ${remaining.length}`);
  remaining.forEach(u => console.log(`  - ${u.name || '(no name)'}  <${u.email}>`));

  await mongoose.disconnect();
}

run().catch(err => {
  console.error('Cleanup failed:', err.message);
  process.exit(1);
});