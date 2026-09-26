const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const http = require('http');
require('dotenv').config();

const app = express();
const server = http.createServer(app);

// Socket.IO Setup
const { Server } = require('socket.io');
const io = new Server(server, {
  cors: {
    origin: '*',
    methods: ['GET', 'POST']
  }
});

io.on('connection', (socket) => {
  console.log('⚡ Client Connected to Socket.IO:', socket.id);

  socket.on('join_room', (userId) => {
    socket.join(userId);
    console.log(`User ${userId} joined socket room`);
  });

  socket.on('send_message', (data) => {
    // Relays real-time message to recipient's socket room
    if (data && data.receiverId) {
      io.to(data.receiverId).emit('receive_message', data);
    }
  });

  socket.on('typing', (data) => {
    if (data && data.receiverId) {
      io.to(data.receiverId).emit('user_typing', data);
    }
  });

  socket.on('disconnect', () => {
    console.log('⚡ Client Disconnected:', socket.id);
  });
});

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
app.use('/api/discover', require('./routes/discoverRoutes'));
app.use('/api/marketplace', require('./routes/marketplaceRoutes'));
app.use('/api/help-requests', require('./routes/helpRequestRoutes'));
app.use('/api/availability', require('./routes/availabilityRoutes'));
app.use('/api/credits', require('./routes/creditRoutes'));
app.use('/api/badges', require('./routes/badgeRoutes'));

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
    server.listen(PORT, () => console.log(`Skill Barter Server running with Socket.IO on port ${PORT}`));
  })
  .catch(err => {
    console.log('MongoDB Connection Error:', err.message);
    server.listen(PORT, () => console.log(`Skill Barter Server running on port ${PORT}`));
  });
