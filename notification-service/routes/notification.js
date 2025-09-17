const router = require("express").Router();
const { listAll, listByUser } = require("../controllers/notification.controller");
const auth = require("../middleware/auth"); // middleware verify JWT

// phải login mới xem
router.get("/", auth, listByUser);

// chỉ admin mới xem được toàn bộ
router.get("/all", auth, listAll);

module.exports = router;
