const express = require('express');
const router = express.Router();
const requestController = require('../controllers/requestController');
const authMiddleware = require('../middleware/authMiddleware');

router.post('/', authMiddleware, requestController.sendRequest);
router.get('/incoming', authMiddleware, requestController.getIncomingRequests);
router.get('/outgoing', authMiddleware, requestController.getOutgoingRequests);
// "exchanges" must be declared before "/:id/..." style routes so it is never
// parsed as an id.
router.get('/exchanges', authMiddleware, requestController.getMyExchanges);
router.get('/with/:userId', authMiddleware, requestController.getRelationship);
router.put('/:id/accept', authMiddleware, requestController.acceptRequest);
router.put('/:id/reject', authMiddleware, requestController.rejectRequest);
router.put('/:id/complete', authMiddleware, requestController.completeRequest);

module.exports = router;
