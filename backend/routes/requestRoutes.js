const express = require('express');
const router = express.Router();
const requestController = require('../controllers/requestController');
const authMiddleware = require('../middleware/authMiddleware');

router.post('/', authMiddleware, requestController.sendRequest);
router.get('/incoming', authMiddleware, requestController.getIncomingRequests);
router.get('/outgoing', authMiddleware, requestController.getOutgoingRequests);
router.put('/:id/accept', authMiddleware, requestController.acceptRequest);
router.put('/:id/reject', authMiddleware, requestController.rejectRequest);
router.put('/:id/complete', authMiddleware, requestController.completeRequest);

module.exports = router;
