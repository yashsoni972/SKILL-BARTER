const Availability = require('../models/Availability');

exports.getAvailability = async (req, res) => {
  try {
    const userId = req.params.userId || req.user.userId;
    const slots = await Availability.find({ userId });
    res.json(slots);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.addAvailability = async (req, res) => {
  try {
    const { day, startTime, endTime, mode } = req.body;
    const newSlot = new Availability({
      userId: req.user.userId,
      day,
      startTime,
      endTime,
      mode: mode || 'Online'
    });
    await newSlot.save();
    res.status(201).json(newSlot);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.deleteAvailability = async (req, res) => {
  try {
    await Availability.findOneAndDelete({ _id: req.params.id, userId: req.user.userId });
    res.json({ message: 'Slot removed' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
