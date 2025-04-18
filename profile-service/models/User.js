const mongoose = require('mongoose');
const { v4: uuidv4 } = require('uuid');

const UserSchema = new mongoose.Schema({
    user_id: {
        type: String,
        default: () => uuidv4(), // Tự sinh khi tạo mới
        unique: true
    },
    full_name: { type: String, required: true },
    work_id: { type: String, required: true, unique: true },
    avatar_link: { type: String },
    email: { type: String, required: true },
    description: { type: String },
    last_login: { type: Date, default: Date.now },
    isActive: { type: Boolean, default: true },
    isDeleted: { type: Boolean, default: false },
    personal_score: { type: Number, default: 0 }
}, { timestamps: true });

module.exports = mongoose.model('User', UserSchema);
