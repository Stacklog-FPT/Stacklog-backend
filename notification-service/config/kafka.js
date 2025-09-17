const { Kafka } = require("kafkajs");
require("dotenv").config();

const { createNotification } = require("../controllers/notification.controller");
const Group = require("../models/group");

const KAFKA_BROKER = process.env.KAFKA_BROKER || "localhost:9092";
const CLIENT_ID = process.env.KAFKA_CLIENT_ID || "notification-service";
const GROUP_ID = process.env.KAFKA_GROUP_ID || "notification-group";

const kafka = new Kafka({ clientId: CLIENT_ID, brokers: [KAFKA_BROKER] });
const producer = kafka.producer();
const consumer = kafka.consumer({ groupId: GROUP_ID });

const topicHandlers = {
  // Khi group được tạo
  [process.env.TOPIC_GROUP_CREATED || "class-service.groupsses.created"]: async (payload) => {
    const memberIds = Array.isArray(payload.memberIds) ? payload.memberIds : [];

    await autoCreateBoxFromGroupEvent(payload);

    await createNotification(
      memberIds,
      `Nhóm ${payload.groupName} đã được tạo`,
      "system",
      { groupId: payload.groupId }
    );
  },

  // Khi task mới được tạo
  [process.env.TOPIC_TASK_CREATED || "task-service.task.created"]: async (payload) => {
    const { groupId, taskId, taskName } = payload;

    let memberIds = [];
    if (Array.isArray(payload.memberIds)) {
      memberIds = payload.memberIds;
    } else if (groupId) {
      const group = await Group.findById(groupId).lean();
      memberIds = group?.memberIds || [];
    }

    await createNotification(
      memberIds,
      `Task mới: ${taskName}`,
      "task",
      { groupId, taskId }
    );
  },

  // Khi task đến deadline
  [process.env.TOPIC_TASK_DEADLINE || "task-service.task.deadline"]: async (payload) => {
    const { groupId, taskId, taskName, deadline } = payload;

    let memberIds = [];
    if (Array.isArray(payload.memberIds)) {
      memberIds = payload.memberIds;
    } else if (groupId) {
      const group = await Group.findById(groupId).lean();
      memberIds = group?.memberIds || [];
    }

    await createNotification(
      memberIds,
      `⏰ Task "${taskName}" sắp đến hạn (${deadline})`,
      "task",
      { groupId, taskId, deadline }
    );
  },

  // Khi có review mới cho task
  [process.env.TOPIC_REVIEW_CREATED || "task-service.review.created"]: async (payload) => {
    const { taskId, taskName, reviewerId, reviewContent, ownerId } = payload;

    // Gửi cho chủ task (owner)
    const receivers = [ownerId];

    await createNotification(
      receivers,
      `💬 Task "${taskName}" có review mới: "${reviewContent}"`,
      "task",
      { taskId, reviewerId }
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
