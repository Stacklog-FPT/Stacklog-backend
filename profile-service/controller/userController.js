const User = require('../models/User');
const { produceMessage } = require('../config/kafka');
const XLSX = require("xlsx");
const fs = require("fs");

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

        return res.status(200).json(users.map(u => ({
            _id: u._id.toString(),
            full_name: u.full_name,
            work_id: u.work_id,
            email: u.email,
            isActive: u.isActive
        })));

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

exports.createListUser = async (req, res) => {
    try {
        const users = req.body;

        // 1. Validate input
        if (!Array.isArray(users) || users.length === 0) {
            return res.status(400).json({ message: "User list is empty or invalid" });
        }

        // 2. Validate từng user
        for (const u of users) {
            if (!u.email || !u.full_name || !u.work_id) {
                return res.status(400).json({
                    message: `Invalid user data: ${JSON.stringify(u)}`
                });
            }
        }

        // 3. Lấy email từ danh sách
        const emails = users.map(u => u.email);

        // 4. Kiểm tra email đã tồn tại trong DB
        const existingUsers = await User.find({ email: { $in: emails } });

        const existingEmails = existingUsers.map(e => e.email);

        // 5. Tách user mới và user đã tồn tại
        const newUsers = users.filter(u => !existingEmails.includes(u.email));
        const oldUsers = existingUsers;

        // 6. Insert user mới vào DB
        let insertedUsers = [];
        if (newUsers.length > 0) {
            insertedUsers = await User.insertMany(newUsers.map(u => ({
                full_name: u.full_name,
                work_id: u.work_id,
                email: u.email,
                isActive: true
            })));
            const receivers = insertedUsers.map(u => {u.email})
            await sendKafkaEvent('notification-service.email.send', {
                subject: "[STACKLOG WELCOME]",
                content: "You just create profile at stacklog!",
                receivers: receivers
            });
        }

        // 7. Trả về danh sách user cuối cùng
        const finalList = [...oldUsers, ...insertedUsers];

        console.log(finalList);

        return res.status(200).json(finalList.map(u => ({
            _id: u._id.toString(),
            full_name: u.full_name,
            work_id: u.work_id,
            email: u.email,
            isActive: u.isActive
        })));

    } catch (error) {
        console.error("Create List User error:", error);
        return res.status(500).json({ message: "Internal Server Error", error: error.message });
    }
}


exports.importExcel = async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({ message: "Vui lòng upload file Excel." });
        }

        const workbook = XLSX.readFile(req.file.path);
        const sheet = workbook.Sheets[workbook.SheetNames[0]];
        const rows = XLSX.utils.sheet_to_json(sheet);

        const { role } = req.params;
        if (!role) {
            return res.status(400).json({ message: "Thiếu role ?role=" });
        }

        const usersToCreate = [];

        for (const row of rows) {
            // Format yêu cầu theo cột Class, RollNumber, Email, MemberCode, FullName
            const user = {
                class: '',
                rollNumber: row.RollNumber,
                email: row.Email,
                memberCode: row.MemberCode,
                fullName: row.FullName,
                role: role,
            };

            usersToCreate.push(user);
        }

        const createdUsers = await User.insertMany(usersToCreate);

        // Xóa file sau khi import
        fs.unlinkSync(req.file.path);

        return res.status(200).json({
            message: "Import Excel thành công!",
            total: createdUsers.length,
            data: createdUsers,
        });
    } catch (err) {
        console.error("ImportExcel Error:", err);
        return res.status(500).json({ message: "Lỗi import Excel", error: err });
    }
}


exports.exportExcel = async (req, res) => {
    try {
        const { role } = req.params;
        if (!role) {
            return res.status(400).json({ message: "Thiếu role ?role=" });
        }

        const users = await User.find({ role, isDeleted: false }).lean();

        if (!users.length) {
            return res.status(404).json({ message: "Không có user nào với role này." });
        }

        const data = users.map(u => ({
            rollNumber: u.work_id,
            email: u.email,
            memberCode: u.work_id,
            fullName: u.full_name,
        }));

        const worksheet = XLSX.utils.json_to_sheet(data);
        const workbook = XLSX.utils.book_new();

        XLSX.utils.book_append_sheet(workbook, worksheet, "Users");

        const fileName = `users_${role}_${Date.now()}.xlsx`;
        const filePath = `./exports/${fileName}`;

        // Tạo folder nếu chưa tồn tại
        if (!fs.existsSync("./exports")) fs.mkdirSync("./exports");

        XLSX.writeFile(workbook, filePath);

        res.download(filePath, fileName, (err) => {
            if (err) {
                console.error("Error downloading file:", err);
            }
            // Xóa file sau khi gửi xong
            fs.unlinkSync(filePath);
        });
    } catch (err) {
        console.error("ExportExcel Error:", err);
        return res.status(500).json({ message: "Lỗi export Excel", error: err });
    }
};

