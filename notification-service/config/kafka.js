const { Kafka } = require("kafkajs");
require("dotenv").config();
const { sendEmail } = require("./email")

const { createNotification } = require("../controllers/notification.controller");

const KAFKA_BROKER = process.env.KAFKA_BROKER || "localhost:9092";
const CLIENT_ID = process.env.KAFKA_CLIENT_ID || "notification-service";
const GROUP_ID = process.env.KAFKA_GROUP_ID || "notification-group";

const kafka = new Kafka({ clientId: CLIENT_ID, brokers: [KAFKA_BROKER] });
const producer = kafka.producer();
const consumer = kafka.consumer({
  groupId: GROUP_ID,
  sessionTimeout: 45000,
  heartbeatInterval: 3000,
  rebalanceTimeout: 60000,
});

// --- Helpers ---
const pad2 = (n) => (n < 10 ? `0${n}` : `${n}`);

/** Convert array from Java LocalDateTime -> JS Date (UTC) */
function fromJavaTimeArray(arr) {
  if (!Array.isArray(arr) || arr.length < 3) return null;
  const [y, M, d, h = 0, m = 0, s = 0, ns = 0] = arr;
  const ms = Math.floor((typeof ns === "number" ? ns : 0) / 1e6);
  // JS month is 0-based; dùng UTC để ổn định
  return new Date(Date.UTC(y, (M - 1), d, h, m, s, ms));
}

/** Format to 'YYYY-MM-DD HH:mm' (UTC). Đổi theo TZ nếu bạn muốn. */
function toReadable(arrOrDate) {
  const d = Array.isArray(arrOrDate) ? fromJavaTimeArray(arrOrDate) :
    (arrOrDate instanceof Date ? arrOrDate : new Date(arrOrDate));
  if (!d || isNaN(d.getTime())) return "";
  return `${d.getUTCFullYear()}-${pad2(d.getUTCMonth() + 1)}-${pad2(d.getUTCDate())} `
    + `${pad2(d.getUTCHours())}:${pad2(d.getUTCMinutes())}`;
}

/** Extract memberIds từ payload.assigns */
function getMemberIdsFromAssigns(payload) {
  if (Array.isArray(payload?.assigns)) {
    return payload.assigns
      .map(a => a?.assignTo)
      .filter(Boolean);
  }
  return [];
}

function getMemberIdsFromGroupsStudent(groupsStudents = []) {
  if (Array.isArray(groupsStudents)) {
    return groupsStudents.map(gs => gs?.userId).filter(Boolean);
  }
}

// --- Topic handlers ---
const topicHandlers = {

  // Class: import người vào thì thông báo về mail, tạo nhóm, thêm người vào nhóm thì tb hệ thống
  // Tạo topic, reject approve thì tb hệ thống
  // task: assign, comment, delete
  // task deadline trong 1 ngày thì thông báo 1h thì mail hệ thống
  // 

  // Nhóm được tạo (giữ nguyên nếu payload của bạn có memberIds & groupName, groupId)
  ["class-service.groupstudent.updated"]: async (payload) => {
    const userId = payload.userId;
    const groupsName = payload.groupsName || "Nhóm";
    const groupsId = payload.groupsId;
    const action = payload.action;

    const path = `/tasks/${groupsId}`

    let content = `Nhóm ${groupsName} đã được tạo và đã thêm bạn vào`;
    if (action === "KICKED") {
      content = `Bạn vừa bị kicked khỏi nhóm ${groupsName} `
    }

    console.log(payload)

    await createNotification(
      [userId],
      content,
      "system",
      {},
      path
    );
  },

  ["topic-service.projectinformation.updated"]: async (payload) => {
    try {
      const { createdBy, piStatus, piTitle } = payload;
      console.log(payload)

      if (!createdBy || !piStatus || !piTitle) {
        console.warn("❗ Missing required fields in payload:", payload);
        return;
      }

      // Normalize status
      const status = String(piStatus).toUpperCase().trim();

      let content;
      switch (status) {
        case "APPROVED":
          content = `Topic bạn đăng ký với tên "${piTitle}" đã được chấp thuận.`;
          break;
        case "REJECTED":
          content = `Topic bạn đăng ký với tên "${piTitle}" đã bị từ chối.`;
          break;
        default:
          content = `Topic bạn đăng ký với tên "${piTitle}" đã được cập nhật trạng thái: ${status}.`;
          break;
      }

      await createNotification(
        [createdBy],
        content,
        "system",
        {}
      );
      console.log(
        `📨 Notification sent to user ${createdBy} | Status: ${status} | Title: ${piTitle}`
      );

    } catch (error) {
      console.error("🔥 Error handling projectinformation.updated event:", error);
    }
  },

  // Task mới được tạo
  [process.env.TOPIC_TASK_CREATED || "task-service.task.created"]: async (payload) => {
    const { groupId, taskId, taskTitle } = payload;

    const memberIds = getMemberIdsFromAssigns(payload);

    const path = `/tasks/${groupId}`

    console.log(payload);

    if (memberIds.length) {
      await createNotification(
        memberIds,
        `Task mới: ${taskTitle}`,
        "task",
        {},
        path
      );

    }

  },

  // Review mới cho task (payload mang cả mảng reviews)
  [process.env.TOPIC_REVIEW_CREATED || "task-service.review.created"]: async (payload) => {
    const { taskTitle, assigns, groupId } = payload;

    const path = `/tasks/${groupId}`

    if (Array.isArray(assigns) && assigns.length > 0) {
      await Promise.all(
        assigns
          .filter(Boolean) // loại null/undefined
          .map(userId =>
            createNotification(
              [userId],
              `💬 Task "${taskTitle}" có review mới`,
              "task",
              path
            )
          )
      );

    }
  },

  // Task đến deadline (sử dụng taskDueDate từ payload)
  [process.env.TOPIC_TASK_DEADLINE || "task-service.task.deadline-by-date"]: async (payload) => {
    const { groupId, taskId, taskTitle, taskDueDate } = payload;

    const deadlineText = toReadable(taskDueDate); // ví dụ: 2025-09-26 01:35 (UTC)
    const memberIds = getMemberIdsFromAssigns(payload);

    const path = `/tasks/${groupId}`

    if (memberIds.length) {
      await createNotification(
        memberIds,
        `⏰ Task "${taskTitle}" sắp đến hạn (${deadlineText})`,
        "task",
        {},
        path
      );
    }
  },

  [process.env.TOPIC_CHAT_MENTION || "chat-service.message.mention"]: async (payload) => {
    const {
      chat_message_id,
      box_chat_id,
      sender_id,
      content,
      attachment,
      state,
      mentionUserIds = [],
    } = payload;

    console.log(payload);

    const path = `/chatbox/${box_chat_id}`

    if (Array.isArray(mentionUserIds) && mentionUserIds.length > 0) {
      await createNotification(
        mentionUserIds,
        `📢 Bạn được mention trong một tin nhắn: "${content}"`,
        "chat",
        {},
        path
      );
    }
  },

  [process.env.TOPIC_MESSAGE_CREATED || 'chat-service.boxchat.kicked']: async (payload) => {
    const {
      userId,
      box
    } = payload;

    console.log(payload);

    const path = `/chatbox/${box.box_chat_id}`;

    await createNotification(
      userId,
      `📢 Bạn đã bị xóa khỏi nhóm: "${box.name_box}"`,
      "chat",
      {},
      path
    );
  },



  ['notification-service.email.send']: async (payload) => {
    const {
      subject,
      content,
      receivers
    } = payload;

    console.log(payload)

    await Promise.all(
      receivers.map(email =>
        sendEmail("", email, subject, content, [])
      )
    );

  },

};

const initProducer = async () => {
  await producer.connect();
  console.log("✅ Kafka Producer ready");
};

const initConsumer = async () => {
  await consumer.connect();
  console.log("✅ Kafka Consumer ready");

  for (const topic of Object.keys(topicHandlers)) {
    await consumer.subscribe({ topic, fromBeginning: false });
  }

  await consumer.run({
    eachMessage: async ({ topic, message }) => {
      try {
        const payload = JSON.parse(message.value?.toString() || "{}");
        const handler = topicHandlers[topic];
        if (handler) {
          await handler(payload);
        } else {
          console.warn(`⚠️ No handler for topic: ${topic}`);
        }
      } catch (e) {
        console.error("Kafka consume error:", e);
      }
    },
  });
};

const sendKafkaEvent = async (topic, value) => {
  try {
    await producer.send({
      topic,
      messages: [{ value: JSON.stringify(value) }],
    });
    console.log(`✅ Kafka Event Sent: ${topic}`);
  } catch (err) {
    console.error("Kafka Send Error:", err);
  }
};

module.exports = { initProducer, initConsumer, sendKafkaEvent, topicHandlers };
