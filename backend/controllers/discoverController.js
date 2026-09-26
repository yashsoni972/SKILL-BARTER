const User = require('../models/User');
const Skill = require('../models/Skill');

exports.discoverUsers = async (req, res) => {
  try {
    const currentUserId = req.user ? req.user.userId : null;
    const { skill, category, location } = req.query;

    let filter = {};
    if (currentUserId) {
      filter._id = { $ne: currentUserId };
    }
    if (location) {
      filter.location = { $regex: location, $options: 'i' };
    }

    const candidateUsers = await User.find(filter).select('-password');

    let myOffers = [];
    let myWants = [];
    if (currentUserId) {
      const myOfferDocs = await Skill.find({ userId: currentUserId, type: 'offer' });
      const myWantDocs = await Skill.find({ userId: currentUserId, type: 'want' });
      myOffers = myOfferDocs.map(s => s.skillName.toLowerCase());
      myWants = myWantDocs.map(s => s.skillName.toLowerCase());
    }

    const results = await Promise.all(candidateUsers.map(async (user) => {
      const offerDocs = await Skill.find({ userId: user._id, type: 'offer' });
      const wantDocs = await Skill.find({ userId: user._id, type: 'want' });

      const offers = offerDocs.map(s => ({ skillName: s.skillName, level: s.level || 'Intermediate' }));
      const wants = wantDocs.map(s => ({ skillName: s.skillName, level: s.level || 'Beginner' }));

      // Match % Calculation
      let matchScore = 50; // Base score
      let mutualOffersMatch = false;
      let mutualWantsMatch = false;

      const partnerOfferNames = offers.map(o => o.skillName.toLowerCase());
      const partnerWantNames = wants.map(w => w.skillName.toLowerCase());

      if (myWants.length > 0 && partnerOfferNames.length > 0) {
        for (let want of myWants) {
          if (partnerOfferNames.some(o => o.includes(want) || want.includes(o))) {
            mutualOffersMatch = true;
            break;
          }
        }
      }

      if (myOffers.length > 0 && partnerWantNames.length > 0) {
        for (let offer of myOffers) {
          if (partnerWantNames.some(w => w.includes(offer) || offer.includes(w))) {
            mutualWantsMatch = true;
            break;
          }
        }
      }

      if (mutualOffersMatch) matchScore += 25;
      if (mutualWantsMatch) matchScore += 25;
      if (user.rating && user.rating >= 4.7) matchScore += 5;

      const finalMatchPercentage = Math.min(100, matchScore);

      return {
        _id: user._id,
        name: user.name,
        email: user.email,
        location: user.location,
        bio: user.bio,
        rating: user.rating || 5.0,
        profileImage: user.profileImage || '',
        offers,
        wants,
        matchPercentage: finalMatchPercentage
      };
    }));

    // Filter by skill or category query if specified
    let filteredResults = results;
    if (skill) {
      const q = skill.toLowerCase();
      filteredResults = filteredResults.filter(u =>
        u.offers.some(o => o.skillName.toLowerCase().includes(q)) ||
        u.wants.some(w => w.skillName.toLowerCase().includes(q)) ||
        u.name.toLowerCase().includes(q)
      );
    }

    if (category && category.toLowerCase() !== 'all') {
      const cat = category.toLowerCase();
      filteredResults = filteredResults.filter(u =>
        u.offers.some(o => o.skillName.toLowerCase().includes(cat)) ||
        u.wants.some(w => w.skillName.toLowerCase().includes(cat))
      );
    }

    // Sort by highest match percentage descending
    filteredResults.sort((a, b) => b.matchPercentage - a.matchPercentage);

    res.json(filteredResults);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
