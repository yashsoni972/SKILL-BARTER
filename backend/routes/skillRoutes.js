const express = require('express');
const router = express.Router();
const skillController = require('../controllers/skillController');
const authMiddleware = require('../middleware/authMiddleware');

router.post('/', authMiddleware, skillController.addSkill);
router.post('/batch', authMiddleware, skillController.updateSkills);
router.delete('/:id', authMiddleware, skillController.deleteSkill);

module.exports = router;
