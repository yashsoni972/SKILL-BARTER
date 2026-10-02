const mongoose = require('mongoose');
const { GridFSBucket } = require('mongodb');
const jwt = require('jsonwebtoken');
const crypto = require('crypto');
const path = require('path');
const multer = require('multer');
const Attachment = require('../models/Attachment');

const BUCKET_NAME = 'attachments';
const MAX_BYTES = 10 * 1024 * 1024; // 10 MB

// Materials people actually swap while teaching a skill: documents, spreadsheets
// and reference images. Anything executable is refused.
const ALLOWED = {
  'application/pdf': '.pdf',
  'application/msword': '.doc',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document': '.docx',
  'application/vnd.ms-excel': '.xls',
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': '.xlsx',
  'image/jpeg': '.jpg',
  'image/jpg': '.jpg',
  'image/png': '.png'
};

// Memory storage keeps the request simple; the size cap makes this safe.
const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: MAX_BYTES, files: 1 }
});

let bucket;

function getBucket() {
  if (!bucket) {
    bucket = new GridFSBucket(mongoose.connection.db, { bucketName: BUCKET_NAME });
  }
  return bucket;
}

/** Stores a buffer in GridFS and resolves with the new file id. */
function storeInGridFS(buffer, storedName, mimeType) {
  return new Promise((resolve, reject) => {
    const stream = getBucket().openUploadStream(storedName, { contentType: mimeType });
    stream.on('error', reject);
    stream.on('finish', () => resolve(stream.id));
    stream.end(buffer);
  });
}

/**
 * A shared file is usually opened by Google Docs, a browser or a PDF viewer, and
 * those apps cannot send our Authorization header. So the app hands them a short
 * lived, attachment-scoped token in the query string instead. It cannot be
 * reused for any other file, and expires on its own.
 */
const TOKEN_TTL_SECONDS = 15 * 60;

function signDownloadToken(attachmentId, userId) {
  return jwt.sign(
    { purpose: 'attachment-download', attachmentId: String(attachmentId), userId: String(userId) },
    process.env.JWT_SECRET || 'skill_barter_super_secret_jwt_key_2026_xyz',
    { expiresIn: TOKEN_TTL_SECONDS }
  );
}

function verifyDownloadToken(token, attachmentId) {
  try {
    const decoded = jwt.verify(
      token,
      process.env.JWT_SECRET || 'skill_barter_super_secret_jwt_key_2026_xyz'
    );
    return decoded.purpose === 'attachment-download' &&
      String(decoded.attachmentId) === String(attachmentId);
  } catch (err) {
    return false;
  }
}

/** Lets a download proceed on either a valid signed token or a normal JWT. */
exports.attachmentAccess = function (req, res, next) {
  const token = req.query.t;
  if (token && verifyDownloadToken(token, req.params.id)) return next();
  return require('../middleware/authMiddleware')(req, res, next);
}

/** Absolute URL an external viewer can actually open, with a fresh token. */
exports.buildDownloadUrl = function (req, attachment, userId) {
  const token = signDownloadToken(attachment._id, userId);
  return `${req.protocol}://${req.get('host')}/api/attachments/${attachment._id}?t=${encodeURIComponent(token)}`;
};

/**
 * Hands out a fresh signed link on demand, so tapping a file hours later still
 * works even though the link embedded in the chat has expired.
 */
exports.mintLink = async (req, res) => {
  try {
    const attachment = await Attachment.findById(req.params.id);
    if (!attachment) {
      return res.status(404).json({ message: 'That file is no longer available.' });
    }

    const me = req.user.userId;
    const allowed = String(attachment.uploadedBy) === String(me) ||
      String(attachment.sharedWith) === String(me);
    if (!allowed) {
      return res.status(403).json({ message: 'This file was not shared with you.' });
    }

    res.json({ url: exports.buildDownloadUrl(req, attachment, me) });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

/**
 * Resolves the conversation partner from the exchange when a requestId is given,
 * otherwise from sharedWith. Sharing with yourself is rejected because there is
 * no conversation to show the file in.
 */
async function resolveTarget(req) {
  const me = req.user.userId;
  const { requestId, sharedWith } = req.body;

  if (requestId) {
    const ExchangeRequest = mongoose.model('ExchangeRequest');
    const request = await ExchangeRequest.findById(requestId);
    if (request) {
      const isSender = String(request.senderId) === String(me);
      if (!isSender && String(request.receiverId) !== String(me)) {
        return { error: 'This exchange does not belong to you', code: 403 };
      }
      return { partnerId: isSender ? request.receiverId : request.senderId, requestId };
    }
  }

  if (sharedWith && String(sharedWith) !== String(me)) {
    return { partnerId: sharedWith, requestId: null };
  }

  return { error: 'A valid requestId or sharedWith user is required', code: 400 };
}

exports.uploadAttachment = [
  upload.single('file'),
  async (req, res) => {
    try {
      if (!req.file) {
        return res.status(400).json({ message: 'No file was received.' });
      }

      const extension = path.extname(req.file.originalname || '').toLowerCase();
      const mimeType = (req.file.mimetype || '').toLowerCase();
      const allowedByMime = ALLOWED[mimeType];
      const allowedByExtension = Object.values(ALLOWED).includes(extension);

      // Some Android pickers report an empty or generic mimetype, so an
      // acceptable extension is accepted as well. Both unknown means refuse.
      if (!allowedByMime && !allowedByExtension) {
        return res.status(400).json({
          message: 'Only PDF, Word (doc/docx), Excel (xls/xlsx), JPG and PNG files are supported.'
        });
      }

      const target = await resolveTarget(req);
      if (target.error) {
        return res.status(target.code).json({ message: target.error });
      }

      const finalType = allowedByMime || mimeType;
      const storedName = `${Date.now()}-${crypto.randomBytes(8).toString('hex')}${extension || ''}`;
      const fileId = await storeInGridFS(req.file.buffer, storedName, finalType);

      const attachment = await Attachment.create({
        fileId,
        filename: req.file.originalname || storedName,
        storedName,
        mimeType: finalType,
        size: req.file.size || req.file.buffer.length,
        uploadedBy: req.user.userId,
        sharedWith: target.partnerId,
        requestId: target.requestId
      });

      res.status(201).json({
        id: attachment._id,
        filename: attachment.filename,
        mimeType: attachment.mimeType,
        size: attachment.size,
        downloadUrl: buildDownloadUrl(req, attachment, req.user.userId)
      });
    } catch (err) {
      // Multer raises this when the file is over the cap.
      if (err && err.code === 'LIMIT_FILE_SIZE') {
        return res.status(400).json({ message: 'That file is larger than the 10 MB limit.' });
      }
      res.status(500).json({ message: err.message });
    }
  }
];

/** Streams a shared file back so the app can open it with a normal viewer. */
exports.downloadAttachment = async (req, res) => {
  try {
    const attachment = await Attachment.findById(req.params.id);
    if (!attachment) {
      return res.status(404).json({ message: 'That file is no longer available.' });
    }

// When a signed download token was used it was already scoped to this file
    // and only issued to a participant, so there is nothing left to check.
    if (req.user) {
      const me = req.user.userId;
      const allowed = String(attachment.uploadedBy) === String(me) ||
        String(attachment.sharedWith) === String(me);
      if (!allowed) {
        return res.status(403).json({ message: 'This file was not shared with you.' });
      }
    }

    res.setHeader('Content-Type', attachment.mimeType);
    res.setHeader('Content-Length', attachment.size);
    // Inline so a PDF opens in the viewer instead of being downloaded blind.
    res.setHeader('Content-Disposition',
      `inline; filename="${encodeURIComponent(attachment.filename)}"`);

    getBucket().openDownloadStream(attachment.fileId)
      .on('error', () => {
        if (!res.headersSent) res.status(404).json({ message: 'File data is missing.' });
      })
      .pipe(res);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

/** Files shared in one conversation, newest first. */
exports.listAttachments = async (req, res) => {
  try {
    const me = req.user.userId;
    const partnerId = req.params.userId;

    const attachments = await Attachment.find({
      $or: [
        { uploadedBy: me, sharedWith: partnerId },
        { uploadedBy: partnerId, sharedWith: me }
      ]
    })
      .sort({ createdAt: -1 })
      .limit(100);

    res.json(attachments.map(a => ({
      id: a._id,
      filename: a.filename,
      mimeType: a.mimeType,
      size: a.size,
      uploadedBy: a.uploadedBy,
      mine: String(a.uploadedBy) === String(me),
      createdAt: a.createdAt,
      downloadUrl: buildDownloadUrl(req, a, me)
    })));
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};