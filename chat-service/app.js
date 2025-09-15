const express = require("express");
const dotenv = require("dotenv");
const cors = require("cors");
const helmet = require("helmet");
const morgan = require("morgan");
const connectDB = require("./config/db");
const { startKafka, publish } = require("./config/kafka");

const boxesRoutes    = require('./routes/boxes');
const messagesRoutes = require('./routes/messages');

dotenv.config();
connectDB();

const app = express();
app.use(express.json());
app.use(cors({
    origin: [
        'http://localhost:5173',
        'https://stacklog.io.vn',
        'https://www.stacklog.io.vn',
        'https://*.vercel.app'
    ],
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization'],
    credentials: true
}));
app.use(helmet());
app.use(morgan("dev"));

(async () => {
    await startKafka();
    await publish();
})();

// Routes
app.use('/api/boxes', boxesRoutes);
app.use('/api/messages', messagesRoutes);

app.get("/", (req, res) => {
    res.send("Chat Service is Running...");
});

// const PORT = process.env.PORT || 5000;
// app.listen(PORT, () => console.log(`Server running on port ${PORT}`));

module.exports = app;