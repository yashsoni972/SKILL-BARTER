const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  location: { type: String, default: 'Anand, Gujarat' },
  bio: { type: String, default: '' },
  profileImage: { type: String, default: '' },
  rating: { type: Number, default: 5.0 },
  totalExchanges: { type: Number, default: 0 },
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('User', UserSchema);
