const asyncHandler = require('../middleware/asyncHandler');
const { redis } = require('../config/db');
const { ioEmit } = require('../config/socket');
const { publish } = require('../config/kafka');
const {
  insertMessage, listMessages, recallMessage,
  softDeleteMessage, hardDeleteMessage
} = require('../models/message');
const { BoxChat } = require('../models/box');

const TOPIC_MESSAGE_CREATED = process.env.TOPIC_MESSAGE_CREATED || 'chat-service.message.created';
const unreadKey = (boxId, userId) => `unread:${boxId}:${userId}`;
const lastKey   = (boxId) => `lastmsg:${boxId}`;

exports.send = asyncHandler(async (req, res) => {
  const { boxId } = req.params;
  const { senderId, content, attachment, mentionUserIds = [] } = req.body || {};
  if (!senderId) return res.status(400).json({ message: 'senderId required' });

  const msgId = await insertMessage({ boxId, senderId, content, attachment });

  // update last message cache
  await redis.set(lastKey(boxId), JSON.stringify({ chat_message_id: msgId, preview: content, at: Date.now() }), { EX: 3600 });

  // unread++ cho mọi member trừ sender
  const box = await BoxChat.findById(boxId).select({ members: 1 }).lean();
  for (const m of box?.members || []) {
    if (m.user === senderId) continue;
    await redis.incr(unreadKey(boxId, m.user));
  }

  const payload = { chat_message_id: msgId, box_chat_id: boxId, sender_id: senderId, content, attachment, state: 'SENT' };
  ioEmit('message:new', payload, `box:${boxId}`);
  for (const uid of new Set(mentionUserIds)) {
    ioEmit('notify:mention', { box_chat_id: boxId, chat_message_id: msgId }, `user:${uid}`);
  }

  await publish(TOPIC_MESSAGE_CREATED, payload);
  res.status(201).json(payload);
});

exports.list = asyncHandler(async (req, res) => {
  const { boxId } = req.params;
  const { limit, beforeMessageId } = req.query;
  const msgs = await listMessages(boxId, { limit, beforeMessageId });
  res.json(msgs);
});

exports.recall = asyncHandler(async (req, res) => {
  const { messageId } = req.params;
  const { operatorId } = req.body || {};
  await recallMessage(messageId, operatorId);
  ioEmit('message:recalled', { chat_message_id: messageId }, undefined); // client nên đang ở room box
  res.status(204).end();
});

exports.remove = asyncHandler(async (req, res) => {
  const { messageId } = req.params;
  const { operatorId } = req.body || {};
  const hard = String(req.query.hard || '0') === '1';
  if (hard) await hardDeleteMessage(messageId);
  else await softDeleteMessage(messageId, operatorId);
  ioEmit('message:deleted', { chat_message_id: messageId, hard }, undefined);
  res.status(204).end();
});

exports.readReset = asyncHandler(async (req, res) => {
  const { boxId } = req.params;
  const { userId } = req.body || {};
  await redis.del(unreadKey(boxId, userId));
  res.status(204).end();
});
