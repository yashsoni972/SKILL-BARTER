const express = require('express');
const router = express.Router();
const availabilityController = require('../controllers/availabilityController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/:userId', availabilityController.getAvailability);
router.post('/', authMiddleware, availabilityController.addAvailability);
router.delete('/:id', authMiddleware, availabilityController.deleteAvailability);

module.exports = router;
