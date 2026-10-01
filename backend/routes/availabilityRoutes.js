const express = require('express');
const router = express.Router();
const availabilityController = require('../controllers/availabilityController');
const authMiddleware = require('../middleware/authMiddleware');

// "me" must be declared before the /:userId route or it is swallowed as a userId.
router.get('/me', authMiddleware, availabilityController.getMyAvailability);
router.get('/:userId', availabilityController.getAvailability);
router.post('/', authMiddleware, availabilityController.addAvailability);
router.delete('/:id', authMiddleware, availabilityController.deleteAvailability);

module.exports = router;
