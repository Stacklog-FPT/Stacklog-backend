const User = require('../models/User');
const { produceMessage } = require('../config/kafka');

// Create a new user
exports.createUser = async (req, res) => {
    try {
        const user = await User.create(req.body);
        await produceMessage('user.created', user);
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
        await produceMessage('user.updated', user);
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
        await produceMessage('user.deleted', user);
        res.json({ message: 'User deleted successfully' });
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
};

// find user
exports.findByEmail = async (req, res) => {
    try {
        const user = await User.findByEmail(req.params.email);
        if (!user) return res.status(404).json({ error: 'User not found' });
        res.status(200).json({ user });
    } catch (error) {
        res.status(400).json({ error: err.message });
    }
}

exports.findByRole = async (req, res) => {
    const { role } = req.params;
    try {
        const users = await User.find({ role: role.toUpperCase(), isDeleted: false });
        if (users.length === 0) return res.status(404).json({ error: `No ${role} found` });
        res.status(200).json({ users });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
};


exports.findByClassId = async (req, res) => {
    const { classId } = req.params;
    const token = req.headers["authorization"];
    if (!token) {
        return res.status(401).json({ error: "Authorization token is required" });
    }

    try {
        // 1. Gọi nhóm từ class-service
        const response = await fetch(`http://classservice:2003/group/class/${classId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": token,
            }
        });

        // 2. Convert sang JSON
        const groups = await response.json();
        console.log(groups);
        // 3. Mapping ra danh sách userId
        const listUserIds = groups
            .flatMap(group => group.groupStudents || [])
            .map(student => student.userId);

        console.log(listUserIds)
        const users = await User.find({
            _id: { $in: listUserIds },
            isDeleted: false
        });
        console.log(users)
        if (!users || users.length === 0) {
            return res.status(404).json({ error: "No users found" });
        }

        return res.status(200).json(users);

    } catch (error) {
        return res.status(400).json({ error: error.message });
    }
};


exports.findById = async (req, res) => {
    const { userId } = req.params;
    try {
        const user = await User.findOne({ _id: userId, isDeleted: false });
        if (user == null) return res.status(404).json({ error: `No ${userId} found` });
        res.status(200).json({ user });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
}

exports.findByNameOrEmail = async (req, res) => {
    const { searchWord } = req.params;
    try {
        const users = await User.findByEmailOrFullname(searchWord);
        if (users.length == 0) return res.status(404).json({ error: `With keyword ${searchWord} not user found` });
        res.status(200).json(users);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
}
