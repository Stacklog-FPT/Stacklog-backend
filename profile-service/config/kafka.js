const { Kafka } = require('kafkajs');

const kafka = new Kafka({
clientId: process.env.KAFKA_CLIENT_ID,
    brokers: [process.env.KAFKA_BROKER]
});

const producer = kafka.producer();

const produceMessage = async (topic, message) => {
    await producer.connect();
    await producer.send({
        topic,
        messages: [{ value: JSON.stringify(message) }],
    });
    console.log(`Message sent to ${topic}:`, message);
};

module.exports = { produceMessage };
