const express = require('express');
const router = express.Router();
const helpRequestController = require('../controllers/helpRequestController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/', helpRequestController.getHelpRequests);
router.post('/', authMiddleware, helpRequestController.createHelpRequest);
router.put('/:id/close', authMiddleware, helpRequestController.closeHelpRequest);

module.exports = router;
