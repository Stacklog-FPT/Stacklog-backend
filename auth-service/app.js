const express = require("express");
const dotenv = require("dotenv");
const cors = require("cors");
const helmet = require("helmet");
const morgan = require("morgan");
const connectDB = require("./config/db");
const { initProducer, initConsumer } = require("./config/kafka");

dotenv.config();
connectDB();

const app = express();
app.use(express.json());
app.use(cors());
app.use(helmet());
app.use(morgan("dev"));

(async () => {
    await initProducer();
    await initConsumer();
})();

// Routes
app.use("/auth", require("./routes/authRoutes"));

app.get("/", (req, res) => {
    res.send("Auth Service is Running...");
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));

// module.exports = app;