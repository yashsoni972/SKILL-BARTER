const express = require('express');
const router = express.Router();
const sessionController = require('../controllers/sessionController');
const authMiddleware = require('../middleware/authMiddleware');

router.post('/', authMiddleware, sessionController.createSession);
router.get('/', authMiddleware, sessionController.getSessions);

module.exports = router;
