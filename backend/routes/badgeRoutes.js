const express = require('express');
const router = express.Router();
const badgeController = require('../controllers/badgeController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/', authMiddleware, badgeController.getBadges);

module.exports = router;
