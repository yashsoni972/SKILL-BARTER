const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const User = require('./models/User');
const Skill = require('./models/Skill');
const ExchangeRequest = require('./models/ExchangeRequest');
require('dotenv').config();

const MONGO_URI = process.env.MONGO_URI || 'mongodb://localhost:27017/skillbarter';

async function seed() {
  try {
    await mongoose.connect(MONGO_URI);
    console.log('Connected to MongoDB for seeding...');

    await User.deleteMany({});
    await Skill.deleteMany({});
    await ExchangeRequest.deleteMany({});

    const passwordHash = await bcrypt.hash('password123', 10);

    const user1 = await User.create({
      name: 'Yash Soni',
      email: 'yash@example.com',
      password: passwordHash,
      location: 'Anand, Gujarat',
      bio: 'Passionate about web development, design and learning new technologies.',
      rating: 4.7,
      totalExchanges: 3
    });

    const user2 = await User.create({
      name: 'Riya Sharma',
      email: 'riya@example.com',
      password: passwordHash,
      location: 'Ahmedabad',
      bio: 'UI/UX Designer & Graphic Artist.',
      rating: 4.8,
      totalExchanges: 5
    });

    const user3 = await User.create({
      name: 'Aman Patel',
      email: 'aman@example.com',
      password: passwordHash,
      location: 'Vadodara',
      bio: 'Python developer and data analyst enthusiast.',
      rating: 4.5,
      totalExchanges: 2
    });

    const user4 = await User.create({
      name: 'Neha Patel',
      email: 'neha@example.com',
      password: passwordHash,
      location: 'Vadodara',
      bio: 'Graphic designer & illustrator.',
      rating: 4.9,
      totalExchanges: 7
    });

    // Seed Skills
    await Skill.insertMany([
      { userId: user1._id, skillName: 'HTML & CSS', category: 'Programming', type: 'offer', level: 'Advanced' },
      { userId: user1._id, skillName: 'JavaScript', category: 'Programming', type: 'offer', level: 'Intermediate' },
      { userId: user1._id, skillName: 'Python', category: 'Programming', type: 'offer', level: 'Intermediate' },
      { userId: user1._id, skillName: 'Graphic Design', category: 'Design', type: 'want', level: 'Beginner' },
      { userId: user1._id, skillName: 'UI/UX', category: 'Design', type: 'want', level: 'Beginner' },

      { userId: user2._id, skillName: 'Graphic Design', category: 'Design', type: 'offer', level: 'Advanced' },
      { userId: user2._id, skillName: 'UI/UX', category: 'Design', type: 'offer', level: 'Advanced' },
      { userId: user2._id, skillName: 'HTML & CSS', category: 'Programming', type: 'want', level: 'Beginner' },
      { userId: user2._id, skillName: 'JavaScript', category: 'Programming', type: 'want', level: 'Beginner' },

      { userId: user3._id, skillName: 'Python', category: 'Programming', type: 'offer', level: 'Advanced' },
      { userId: user3._id, skillName: 'Data Analysis', category: 'Programming', type: 'offer', level: 'Intermediate' },
      { userId: user3._id, skillName: 'HTML & CSS', category: 'Programming', type: 'want', level: 'Beginner' },

      { userId: user4._id, skillName: 'Graphic Design', category: 'Design', type: 'offer', level: 'Advanced' },
      { userId: user4._id, skillName: 'Illustration', category: 'Design', type: 'offer', level: 'Intermediate' },
      { userId: user4._id, skillName: 'Python', category: 'Programming', type: 'want', level: 'Beginner' }
    ]);

    // Seed Exchange Request
    await ExchangeRequest.create({
      senderId: user2._id,
      receiverId: user1._id,
      offeredSkill: 'Graphic Design',
      requestedSkill: 'JavaScript',
      message: 'Hi Yash! I am interested in exchanging skills. I can teach you Graphic Design in exchange for JavaScript.',
      status: 'pending'
    });

    console.log('Database seeded successfully!');
    process.exit(0);
  } catch (err) {
    console.error('Seeding error:', err);
    process.exit(1);
  }
}

seed();
