const asyncHandler = require('../middleware/asyncHandler');
const { createBox, addMembers, listBoxesByUser } = require('../models/box');
const { ioEmit } = require('../config/socket');

exports.create = asyncHandler(async (req, res) => {
  const { name, avatar, memberIds = [] } = req.body || {};
  const creatorId = req.user.id;  // <-- lấy từ token
  if (!memberIds.length) return res.status(400).json({ message: 'memberIds required' });

  const box = await createBox({ name, avatar, creatorId, memberIds });
  for (const uid of new Set(memberIds)) {
    ioEmit('box:created', { box_chat_id: box.box_chat_id, name_box: box.name_box }, `user:${uid}`);
  }
  res.status(201).json(box);
});

exports.addMembers = asyncHandler(async (req, res) => {
  const { boxId } = req.params;
  const operatorId = req.user.id;  // <-- lấy từ token
  const { memberIds = [] } = req.body || {};
  await addMembers(boxId, operatorId, memberIds);
  for (const uid of new Set(memberIds)) {
    ioEmit('box:member_added', { box_chat_id: boxId, user_id: uid }, `user:${uid}`);
  }
  res.status(204).end();
});

exports.listByUser = asyncHandler(async (req, res) => {
  const userId = req.user.id;   // <-- lấy từ token
  const boxes = await listBoxesByUser(userId);
  res.json(boxes);
});

