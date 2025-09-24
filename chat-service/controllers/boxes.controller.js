const asyncHandler = require('../middleware/asyncHandler');
const { createBox, addMembers, listBoxesByUser, deleteBox, searchBoxesByUserIds } = require('../models/box');
const { deleteByBoxId } = require('../models/message');
const { ioEmit } = require('../config/socket');

exports.create = asyncHandler(async (req, res) => {
  const { name, avatar, memberIds = [], type } = req.body || {};
  const creatorId = req.user.id;  // <-- lấy từ token
  if (!memberIds.length) return res.status(400).json({ message: 'memberIds required' });

  const box = await searchBoxesByUserIds(memberIds);

  if (box) return res.status(200).json(box);

  box = await createBox({ name, avatar, creatorId, memberIds, type });
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

exports.delete = asyncHandler(async (req, res) => {
  try {
    const { boxId } = req.params;
    if (!boxId) {
      return res.status(400).json({ success: false, error: "boxId is required" });
    }

    await deleteByBoxId(boxId);

    const result = await deleteBox(boxId);


    // Emit socket để client update UI
    ioEmit("box:deleted", { boxId, ...result }, `box:${boxId}`);

    res.json({ success: true, message: "Box deleted", result });
  } catch (err) {
    // Ghi log chi tiết
    console.error(`[BoxController] Delete boxId=${req.params.boxId} error:`, err);

    res.status(500).json({
      success: false,
      error: err.message || "Internal Server Error",
    });
  }
});

