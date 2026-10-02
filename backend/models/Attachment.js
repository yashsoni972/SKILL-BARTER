const mongoose = require('mongoose');

/**
 * Metadata for a file shared in a conversation. The bytes themselves live in
 * GridFS on the same cluster, referenced by fileId.
 *
 * Render's free tier has an ephemeral disk, so writing uploads to the filesystem
 * would lose every shared file on the next deploy. GridFS keeps them in MongoDB,
 * which is already the project's datastore.
 */
const AttachmentSchema = new mongoose.Schema({
  fileId: { type: mongoose.Schema.Types.ObjectId, required: true, index: true },
  filename: { type: String, required: true },
  storedName: { type: String, required: true },
  mimeType: { type: String, required: true },
  size: { type: Number, default: 0 },
  uploadedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  // The person it was shared with, so the conversation can list its files.
  sharedWith: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  requestId: { type: mongoose.Schema.Types.ObjectId, ref: 'ExchangeRequest' },
  createdAt: { type: Date, default: Date.now }
});

// Avoid duplicate-name collisions in GridFS when two people send "notes.pdf".
AttachmentSchema.index({ storedName: 1 }, { unique: true });

module.exports = mongoose.model('Attachment', AttachmentSchema);