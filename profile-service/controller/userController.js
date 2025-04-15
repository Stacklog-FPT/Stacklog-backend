const User = require('../models/User');
const { produceMessage } = require('../config/kafka');

// Create a new user
exports.createUser = async (req, res) => {
    try {
        const user = await User.create(req.body);
        await produceMessage('UserCreated', user);
        res.status(201).json(user);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
};

// Update an existing user
exports.updateUser = async (req, res) => {
    try {
        const user = await User.findByIdAndUpdate(req.params.id, req.body, { new: true });
        if (!user) return res.status(404).json({ error: 'User not found' });
        await produceMessage('UserUpdated', user);
        res.json(user);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
};

// Delete user (soft delete)
exports.deleteUser = async (req, res) => {
    try {
        const user = await User.findByIdAndUpdate(req.params.id, { isDeleted: true }, { new: true });
        if (!user) return res.status(404).json({ error: 'User not found' });
        await produceMessage('UserDeleted', user);
        res.json({ message: 'User deleted successfully' });
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
};

// find user
exports.findById = async (req, res) => {
    try {
        const user = await User.findByIdAndUpdate(req.params.id, { isDeleted: true }, { new: true });
        if (!user) return res.status(404).json({ error: 'User not found' });
        res.status(200).json({ user });
    } catch (error) {
        res.status(400).json({ error: err.message });
    }
}
