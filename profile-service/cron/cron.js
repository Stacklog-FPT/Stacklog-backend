const cron = require("node-cron");
const User = require('../models/User');

function buildUserMap(list) {
    const map = {};

    list.forEach(u => {
        if (u.userId && u.email) {
            map[u._id] = u.email;
        }
    });

    return map;
}

// CRONJOB chạy mỗi 1 giờ
cron.schedule("0 * * * *", async () => {
    console.log("⏳ Running cronjob: Sending user map to task service...");

    try {
        // 1. Lấy toàn bộ user
        const users = await User.find({ role: role.toUpperCase(), isDeleted: false });;

        // 2. Chuyển thành map<userId, email>
        const userMap = buildUserMap(users);

        console.log("User map:", userMap);

        // 3. Gửi POST sang task-service
        const res = await axios.post(
            "http://taskservice:2002/auto-check-deadline",
            { userIdMapToEmail: userMap },
            { timeout: 5000 }
        );

        console.log("✔ Task-service response:", res.data);

    } catch (err) {
        console.error("❌ Cronjob failed:", err.message);
    }
});

console.log("🚀 Cronjob started. Running every hour...");
