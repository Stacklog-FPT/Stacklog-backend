const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
    full_name: { type: String, required: true },
    work_id: { type: String, required: true, unique: true },
    avatar_link: { type: String },
    email: {type: String, required: true},
    description: { type: String },
    last_login: { type: Date, default: Date.now },
    isActive: { type: Boolean, default: true },
    isDeleted: { type: Boolean, default: false },
    personal_score: { type: Number, default: 0 }
}, { timestamps: true });

module.exports = mongoose.model('User', UserSchema);
