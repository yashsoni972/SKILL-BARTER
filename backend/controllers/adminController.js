const User = require('../models/User');
const ExchangeRequest = require('../models/ExchangeRequest');
const SkillListing = require('../models/SkillListing');
const HelpRequest = require('../models/HelpRequest');
const Report = require('../models/Report');

exports.getOverview = async (req, res) => {
  try {
    const totalUsers = await User.countDocuments();
    const activeExchanges = await ExchangeRequest.countDocuments({ status: 'accepted' });
    const completedExchanges = await ExchangeRequest.countDocuments({ status: 'completed' });
    const marketplaceListings = await SkillListing.countDocuments();
    const helpRequests = await HelpRequest.countDocuments();
    const pendingReports = await Report.countDocuments({ status: 'pending' });

    res.json({
      totalUsers,
      activeExchanges,
      completedExchanges,
      marketplaceListings,
      helpRequests,
      pendingReports
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
