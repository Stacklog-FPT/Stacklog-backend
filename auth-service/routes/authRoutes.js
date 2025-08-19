const express = require("express");
const { logout,login,validate, addOne } = require("../controllers/authController");
const protect = require("../middleware/authMiddleware");

const router = express.Router();

router.post("/login", login);
router.post("/logout", protect, logout);
router.get("/validate", validate);
router.post("/forgot-password", sendMailResetPassword);
router.post("/change-password", changePassword)

module.exports = router;