// config/kafka.js
const { Kafka, logLevel } = require('kafkajs');
const { autoCreateBoxFromGroupEvent } = require('../models/box');
const { ioEmit } = require('./socket');

const kafka = new Kafka({
  clientId: process.env.KAFKA_CLIENT_ID || 'chat-service',
  brokers: (process.env.KAFKA_BROKERS || 'localhost:9092').split(','),
  logLevel: logLevel.NOTHING
});

const producer = kafka.producer();
const consumer = kafka.consumer({ groupId: process.env.KAFKA_GROUP_ID || 'chat-service-group' });

async function startKafka() {
  await producer.connect();
  await consumer.connect();

  const topicGroupCreated = process.env.TOPIC_GROUP_CREATED || 'class-service.groupsses.created';
  await consumer.subscribe({ topic: topicGroupCreated, fromBeginning: false });

  await consumer.run({
    eachMessage: async ({ topic, message }) => {
      if (topic !== topicGroupCreated) return;
      try {
        const payload = JSON.parse(message.value.toString());
        const box = await autoCreateBoxFromGroupEvent(payload);
        for (const uid of payload.memberIds || []) {
          ioEmit('box:created', { box_chat_id: box.box_chat_id, name_box: box.name_box }, `user:${uid}`);
        }
      } catch (e) {
        console.error('Kafka consume error:', e);
      }
    }
  });
}

async function publish(topic, value) {
  await producer.send({ topic, messages: [{ value: JSON.stringify(value) }] });
}

module.exports = { startKafka, publish };
