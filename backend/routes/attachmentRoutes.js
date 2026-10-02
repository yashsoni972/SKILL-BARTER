const express = require('express');
const router = express.Router();
const attachmentController = require('../controllers/attachmentController');
const authMiddleware = require('../middleware/authMiddleware');

router.post('/', authMiddleware, attachmentController.uploadAttachment);
router.get('/with/:userId', authMiddleware, attachmentController.listAttachments);
router.get('/:id/link', authMiddleware, attachmentController.mintLink);

// Download accepts a normal JWT or a short lived signed link token, because the
// file is usually opened by an external viewer that cannot send our header.
router.get('/:id', attachmentController.attachmentAccess, attachmentController.downloadAttachment);

module.exports = router;