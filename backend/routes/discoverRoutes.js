const express = require('express');
const router = express.Router();
const discoverController = require('../controllers/discoverController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/users', (req, res, next) => {
  // Optional auth middleware so non-authenticated discovery works as well
  const token = req.header('Authorization');
  if (token) {
    authMiddleware(req, res, () => discoverController.discoverUsers(req, res));
  } else {
    discoverController.discoverUsers(req, res);
  }
});

module.exports = router;
