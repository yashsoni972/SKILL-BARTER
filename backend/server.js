const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config();

const app = express();

app.use(cors());
app.use(express.json());

// Routes
app.use('/api/auth', require('./routes/authRoutes'));
app.use('/api/users', require('./routes/userRoutes'));
app.use('/api/skills', require('./routes/skillRoutes'));
app.use('/api/requests', require('./routes/requestRoutes'));
app.use('/api/messages', require('./routes/messageRoutes'));
app.use('/api/sessions', require('./routes/sessionRoutes'));
app.use('/api/reviews', require('./routes/reviewRoutes'));
app.use('/api/notifications', require('./routes/notificationRoutes'));

app.get('/', (req, res) => {
  res.json({ message: 'Welcome to Skill Barter REST API Server. Exchange Skills, Not Money!' });
});

app.get('/api/health', (req, res) => {
  res.json({ status: 'OK', message: 'Skill Barter API Server is running!' });
});

const PORT = process.env.PORT || 5001;
const MONGO_URI = process.env.MONGO_URI || 'mongodb://localhost:27017/skillbarter';

mongoose.connect(MONGO_URI)
  .then(() => {
    console.log('MongoDB Connected Successfully');
    app.listen(PORT, () => console.log(`Skill Barter Server running on port ${PORT}`));
  })
  .catch(err => {
    console.log('MongoDB Connection Error:', err.message);
    app.listen(PORT, () => console.log(`Skill Barter Server running (without MongoDB) on port ${PORT}`));
  });
