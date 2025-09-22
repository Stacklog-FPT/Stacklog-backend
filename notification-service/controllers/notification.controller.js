const {
    Notification,
    getAllNotifications,
    getNotificationsByUser,
} = require("../model/Notification");

const { redisService } = require("../config/redis");
const { ioEmitNotification } = require('../config/socket');

/**
 * GET /api/notifications (tất cả hệ thống - chỉ admin mới nên dùng)
 */
async function listAll(req, res) {
    try {
        // Có thể kiểm tra role nếu cần
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
 * GET /api/notifications/me (lấy theo user từ token)
 */
async function listByUser(req, res) {
    try {
        const userId = req.user?.id;
        if (!userId) {
            return res.status(401).json({ success: false, error: "Unauthorized" });
        }

        const notifications = await getNotificationsByUser(userId);
        res.json({ success: true, data: notifications });
    } catch (err) {
        res.status(500).json({ success: false, error: err.message });
    }
}

async function createNotification(userIds, content, type = "system", meta = {}) {
    if (!Array.isArray(userIds) || userIds.length === 0) {
        throw new Error("userIds required");
    }

    // 1. Lưu Mongo
    const notification = await Notification.create({
        content,
        type,
        receivers: userIds.map((uid) => ({ userId: uid })),
        meta,
    });

    // 2. Lưu Redis + emit socket realtime
    for (const uid of userIds) {
        // await redisService.saveToRedis(notification.toObject(), null, "notification-service");
        ioEmitNotification(notification, `user:${uid}`);
        console.log(`noti:${notification} user:${uid}`);
    }

    return notification;
}

module.exports = {
    listAll,
    listByUser,
    createNotification
};
