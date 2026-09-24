const jwt = require('jsonwebtoken');

module.exports = function (req, res, next) {
  const tokenHeader = req.header('Authorization');
  if (!tokenHeader) {
    return res.status(401).json({ message: 'No token, authorization denied' });
  }

  const token = tokenHeader.startsWith('Bearer ') ? tokenHeader.split(' ')[1] : tokenHeader;

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET || 'skill_barter_super_secret_jwt_key_2026_xyz');
    req.user = decoded;
    next();
  } catch (err) {
    res.status(401).json({ message: 'Token is invalid or expired' });
  }
};
