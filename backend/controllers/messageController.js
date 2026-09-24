const Message = require('../models/Message');

exports.sendMessage = async (req, res) => {
  try {
    const { receiverId, message } = req.body;
    const newMessage = new Message({
      senderId: req.user.userId,
      receiverId,
      message
    });
    await newMessage.save();
    res.status(201).json(newMessage);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getMessages = async (req, res) => {
  try {
    const partnerId = req.params.userId;
    const userId = req.user.userId;

    const messages = await Message.find({
      $or: [
        { senderId: userId, receiverId: partnerId },
        { senderId: partnerId, receiverId: userId }
      ]
    }).sort({ createdAt: 1 });

    res.json(messages);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
