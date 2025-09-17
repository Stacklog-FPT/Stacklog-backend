const {
    getAllNotifications,
    getNotificationsByUser,
} = require("../services/notification");

const { RedisService } = require("../config/redis");
const { ioEmit } = require("../config/socket");

// Tạo RedisService cho entity = notification
// idExtractor = cách lấy id từ notification
const redisService = new RedisService({
    jwtDecoder: require("../utils/jwtDecoder"), // bạn cần có jwtDecoder riêng
    idExtractor: (e) => e._id?.toString(),
    entityName: "notification",
    ttlMinutes: 600,
});

/**
 * GET /api/notifications/all (admin)
 */
async function listAll(req, res) {
    try {
        if (req.user?.role !== "admin") {
            return res.status(403).json({ success: false, error: "Forbidden" });
        }

        const notifications = await getAllNotifications();
        res.json({ success: true, data: notifications });
    } catch (err) {
        res.status(500).json({ success: false, error: err.message });
    }
}

/**
 * GET /api/notifications (của user hiện tại từ token)
 */
async function listByUser(req, res) {
    try {
        const userId = req.user?.id;
        if (!userId) {
            return res.status(401).json({ success: false, error: "Unauthorized" });
        }

        const notifications = await getNotificationsByUser(userId);

        // --- Lưu vào Redis cache ---
        const token = req.headers["authorization"];
        for (const n of notifications) {
            await redisService.saveToRedis(n, token, "notification-service");
        }

        // --- Gửi realtime qua socket ---
        ioEmit("notification:list", notifications, `user:${userId}`);

        res.json({ success: true, data: notifications });
    } catch (err) {
        console.error("listByUser error", err);
        res.status(500).json({ success: false, error: err.message });
    }
}

module.exports = {
    listAll,
    listByUser,
};
