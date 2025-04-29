const User = require("../models/User");
const jwt = require("jsonwebtoken");
const redisClient = require("../config/redis");
const { sendKafkaEvent } = require("../config/kafka");

const generateToken = (user) => {
    return jwt.sign(
        { id: user._id, username: user.username },
        process.env.JWT_SECRET,
        { expiresIn: "1d" }
    );
};



// Đăng nhập người dùng
const login = async (req, res) => {
    const { email, password } = req.body;

    try {
        const user = await User.findOne({ email });
        if (!user || !(await user.matchPassword(password))) {

            return res.status(401).json({ message: "Invalid credentials" });
        }

        const token = generateToken(user);

        // Lưu token vào Redis với TTL 1 ngày
        await redisClient.setEx(`currentuser`, process.env.SESSION_EXPIRY, token);
        console.log(JSON.stringify(user));

        // Gửi event người dùng đăng nhập vào kafka
        await sendKafkaEvent("auth-service.user.loginned", { email: user.email, role: user.role, timestamp: Date.now() });

        return res.json({
            _id: user._id, username: user.username, email: user.email, token
        });

    } catch (error) {
        console.error("Login Error:", error);
        res.status(500).json({ message: "Server error", error });
    }
};

const logout = async (req, res) => {
    try {
        await redisClient.del(`session:${req.user.id}`);

        sendKafkaEvent("UserLoggedOut", { email: decoded.email, timestamp: Date.now() });

        res.json({ message: "Logged out successfully" });
    } catch (error) {
        res.status(500).json({ message: "Server error", error });
    }
};

// validate token
const validate = async (req, res) => {
    console.log(req.headers["authorization"]?.split(" ")[1]);
    const token = req.headers["authorization"]?.split(" ")[1];

    if (!token) {
        return res.status(401).json({ message: "Unauthorized" });
    }

    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        const userRole = User[decoded.email]?.role || "guest";

        res.setHeader("X-User-Role", userRole);
        res.setHeader("Content-Length", "0"); // Đảm bảo không có body trả về
        return res.sendStatus(200);
    } catch (err) {
        console.log(err);
        return res.status(402).json({ message: "Invalid Token" });
    }
}

module.exports = { logout, login, validate };