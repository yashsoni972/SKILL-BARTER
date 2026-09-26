const express = require('express');
const router = express.Router();
const marketplaceController = require('../controllers/marketplaceController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/', marketplaceController.getListings);
router.post('/listings', authMiddleware, marketplaceController.createListing);
router.delete('/listings/:id', authMiddleware, marketplaceController.deleteListing);

module.exports = router;
