// models/box.js
const { Schema, model } = require('mongoose');
const { v4: uuidv4 } = require('uuid');

const MemberSchema = new Schema({
  user:   { type: String, required: true, index: true }, // user_id
  isAdmin:{ type: Boolean, default: false },
  isMute: { type: Boolean, default: false },
  joinedAt: { type: Date, default: Date.now },
  addedBy:  { type: String, default: null }
}, { _id: false });

const BoxChatSchema = new Schema({
  _id:       { type: String, required: true },          // box_chat_id (uuid hoặc groupId từ Kafka)
  name_box:  { type: String },
  ava_box:   { type: String },
  created_by:{ type: String },
  members:   { type: [MemberSchema], default: [] }
}, { timestamps: { createdAt: 'created_at', updatedAt: 'update_at' }, versionKey: false });

BoxChatSchema.virtual('box_chat_id').get(function(){ return this._id; });
BoxChatSchema.index({ 'members.user': 1 });
BoxChatSchema.index({ update_at: -1 });

const BoxChat = model('BoxChat', BoxChatSchema);

// Services trên model
async function createBox({ name, avatar, creatorId, memberIds }) {
  const id = uuidv4();
  const members = Array.from(new Set(memberIds || [])).map(uid => ({
    user: uid, isAdmin: uid === creatorId, addedBy: creatorId
  }));
  const doc = await BoxChat.create({ _id: id, name_box: name, ava_box: avatar, created_by: creatorId, members });
  return doc.toObject();
}

async function addMembers(boxId, operatorId, memberIds = []) {
  // chỉ push nếu chưa tồn tại (filter + $push)
  const bulk = Array.from(new Set(memberIds)).map(uid => ({
    updateOne: {
      filter: { _id: boxId, 'members.user': { $ne: uid } },
      update: { $push: { members: { user: uid, isAdmin: false, isMute: false, addedBy: operatorId } } }
    }
  }));
  if (bulk.length) await BoxChat.bulkWrite(bulk);
}

async function listBoxesByUser(userId) {
  return BoxChat.find({ 'members.user': userId })
    .sort({ update_at: -1 })
    .select({ _id: 1, name_box: 1, ava_box: 1, update_at: 1 })
    .lean();
}

/** Kafka handler: payload = { groupId, name, avatar, memberIds[], createdBy } */
async function autoCreateBoxFromGroupEvent({ groupId, name, avatar, memberIds = [], createdBy }) {
  await BoxChat.updateOne(
    { _id: groupId },
    { $setOnInsert: { name_box: name, ava_box: avatar, created_by: createdBy, members: [] } },
    { upsert: true }
  );

  // Thêm member (chống trùng)
  const bulk = Array.from(new Set(memberIds)).map(uid => ({
    updateOne: {
      filter: { _id: groupId, 'members.user': { $ne: uid } },
      update: { $push: { members: { user: uid, isAdmin: false, isMute: false, addedBy: createdBy } } }
    }
  }));
  if (bulk.length) await BoxChat.bulkWrite(bulk);

  return BoxChat.findById(groupId).lean();
}

module.exports = { BoxChat, createBox, addMembers, listBoxesByUser, autoCreateBoxFromGroupEvent };
