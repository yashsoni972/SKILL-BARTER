const ExchangeRequest = require('../models/ExchangeRequest');
const User = require('../models/User');

exports.sendRequest = async (req, res) => {
  try {
    const { receiverId, offeredSkill, requestedSkill, message } = req.body;
    const newRequest = new ExchangeRequest({
      senderId: req.user.userId,
      receiverId,
      offeredSkill,
      requestedSkill,
      message: message || 'Hi! I would like to exchange skills with you.'
    });
    await newRequest.save();
    res.status(201).json(newRequest);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getIncomingRequests = async (req, res) => {
  try {
    const requests = await ExchangeRequest.find({ receiverId: req.user.userId, status: 'pending' })
      .populate('senderId', 'name email location rating profileImage');
    res.json(requests);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.getOutgoingRequests = async (req, res) => {
  try {
    const requests = await ExchangeRequest.find({ senderId: req.user.userId })
      .populate('receiverId', 'name email location rating profileImage');
    res.json(requests);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.acceptRequest = async (req, res) => {
  try {
    const request = await ExchangeRequest.findByIdAndUpdate(
      req.params.id,
      { status: 'accepted' },
      { new: true }
    );
    res.json(request);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.rejectRequest = async (req, res) => {
  try {
    const request = await ExchangeRequest.findByIdAndUpdate(
      req.params.id,
      { status: 'rejected' },
      { new: true }
    );
    res.json(request);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.completeRequest = async (req, res) => {
  try {
    const request = await ExchangeRequest.findByIdAndUpdate(
      req.params.id,
      { status: 'completed' },
      { new: true }
    );
    res.json(request);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
