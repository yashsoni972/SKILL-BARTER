const express = require('express');
const router = express.Router();
const messageController = require('../controllers/messageController');
const authMiddleware = require('../middleware/authMiddleware');

const conversationController = require('../controllers/conversationController');

// Must be declared before '/:userId' so it is not parsed as a user id.
router.get('/conversations', authMiddleware, conversationController.getConversations);

router.post('/', authMiddleware, messageController.sendMessage);
router.get('/:userId', authMiddleware, messageController.getMessages);

module.exports = router;
