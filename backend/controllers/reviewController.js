const Review = require('../models/Review');
const User = require('../models/User');

exports.addReview = async (req, res) => {
  try {
    const { reviewedUserId, rating, comment } = req.body;
    const review = new Review({
      reviewerId: req.user.userId,
      reviewedUserId,
      rating,
      comment
    });
    await review.save();

    // Recalculate average rating
    const reviews = await Review.find({ reviewedUserId });
    const avgRating = reviews.reduce((acc, curr) => acc + curr.rating, 0) / reviews.length;
    await User.findByIdAndUpdate(reviewedUserId, { rating: Number(avgRating.toFixed(1)) });

    res.status(201).json(review);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getUserReviews = async (req, res) => {
  try {
    const reviews = await Review.find({ reviewedUserId: req.params.userId })
      .populate('reviewerId', 'name profileImage');
    res.json(reviews);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
