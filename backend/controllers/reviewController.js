const Review = require('../models/Review');
const User = require('../models/User');
const ExchangeRequest = require('../models/ExchangeRequest');
const Notification = require('../models/Notification');

// A review is only meaningful if the two people actually exchanged a skill, so
// the pair must share at least one completed exchange before a rating counts.
async function hasCompletedExchange(a, b) {
  const request = await ExchangeRequest.findOne({
    $or: [
      { senderId: a, receiverId: b },
      { senderId: b, receiverId: a }
    ],
    status: 'completed'
  });
  return Boolean(request);
}

exports.addReview = async (req, res) => {
  try {
    const me = req.user.userId;
    const { reviewedUserId, rating, comment } = req.body;

    if (String(reviewedUserId) === String(me)) {
      return res.status(400).json({ message: 'You cannot review yourself.' });
    }

    const score = Number(rating);
    if (!Number.isFinite(score) || score < 1 || score > 5) {
      return res.status(400).json({ message: 'Rating must be between 1 and 5.' });
    }

    const reviewed = await User.findById(reviewedUserId);
    if (!reviewed) {
      return res.status(404).json({ message: 'That member no longer exists.' });
    }

    if (!(await hasCompletedExchange(me, reviewedUserId))) {
      return res.status(403).json({ message: 'You can only review someone after a completed skill exchange.' });
    }

    // One review per pair: re-rating overwrites instead of skewing the average.
    const review = await Review.findOneAndUpdate(
      { reviewerId: me, reviewedUserId },
      { rating: score, comment: comment || '', createdAt: new Date() },
      { upsert: true, new: true, setDefaultsOnInsert: true }
    );

    const reviews = await Review.find({ reviewedUserId });
    const avgRating = reviews.reduce((acc, curr) => acc + curr.rating, 0) / reviews.length;
    reviewed.rating = Number(avgRating.toFixed(1));
    await reviewed.save();

    const meUser = await User.findById(me);
    try {
      await Notification.create({
        userId: reviewedUserId,
        title: 'New Review',
        message: `${meUser ? meUser.name : 'Someone'} rated you ${score}/5.`,
        type: 'review'
      });
    } catch (err) {
      console.error('review notification failed:', err.message);
    }

    res.status(201).json(review);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getUserReviews = async (req, res) => {
  try {
    const reviews = await Review.find({ reviewedUserId: req.params.userId })
      .populate('reviewerId', 'name profileImage')
      .sort({ createdAt: -1 });
    res.json(reviews);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

/**
 * Which partners the caller has already reviewed. Read in one call so the
 * "My Exchanges" list can label each row "Rate" or "Rated" without N+1 calls.
 */
exports.getMyGivenReviews = async (req, res) => {
  try {
    const reviews = await Review.find({ reviewerId: req.user.userId }).select('reviewedUserId rating');
    res.json(reviews);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};