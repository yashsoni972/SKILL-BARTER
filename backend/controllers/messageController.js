const Message = require('../models/Message');
const Attachment = require('../models/Attachment');

exports.sendMessage = async (req, res) => {
  try {
    const { receiverId, message, attachmentId } = req.body;
    const text = typeof message === 'string' ? message.trim() : '';

    // A message needs at least one of a caption or a file, otherwise it is an
    // empty bubble the recipient would see.
    let resolvedAttachment = null;
    if (attachmentId) {
      resolvedAttachment = await Attachment.findById(attachmentId);
      if (!resolvedAttachment) {
        return res.status(400).json({ message: 'That attachment does not exist.' });
      }
      if (String(resolvedAttachment.uploadedBy) !== String(req.user.userId)) {
        return res.status(403).json({ message: 'You did not upload that file.' });
      }
    }

    if (!text && !resolvedAttachment) {
      return res.status(400).json({ message: 'Write something or attach a file.' });
    }

    const newMessage = new Message({
      senderId: req.user.userId,
      receiverId,
      message: text,
      attachmentId: resolvedAttachment ? resolvedAttachment._id : undefined
    });

    await newMessage.save();
    res.status(201).json(await decorate(newMessage, req));
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

/**
 * Attaches file metadata so the bubble can render a thumbnail or file row.
 * The signed link is minted here with a short life, which is plenty for the
 * chat it is being displayed in; opening a file asks the server for a fresh one.
 */
async function decorate(msg, req) {
  const plain = msg.toObject();
  if (!plain.attachmentId) return plain;

  const attachmentController = require('./attachmentController');
  const attachment = await Attachment.findById(plain.attachmentId);
  if (attachment) {
    plain.attachment = {
      id: String(attachment._id),
      filename: attachment.filename,
      mimeType: attachment.mimeType,
      size: attachment.size,
      downloadUrl: attachmentController.buildDownloadUrl(req, attachment, req.user.userId)
    };
  }
  return plain;
}

exports.getMessages = async (req, res) => {
  try {
    const partnerId = req.params.userId;
    const userId = req.user.userId;

    const messages = await Message.find({
      $or: [
        { senderId: userId, receiverId: partnerId },
        { senderId: partnerId, receiverId: userId }
      ]
    }).sort({ createdAt: 1 });

    res.json(await Promise.all(messages.map(m => decorate(m, req))));
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};