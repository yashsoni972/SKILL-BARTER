const express = require('express');
const router = express.Router();
const creditController = require('../controllers/creditController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/', authMiddleware, creditController.getCredits);
router.post('/earn', authMiddleware, creditController.earnCredits);
router.post('/spend', authMiddleware, creditController.spendCredits);

module.exports = router;
