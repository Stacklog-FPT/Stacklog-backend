const router = require('express').Router();
const { createTask } = require('../controllers/ai.controller')
const auth = require('../middleware/auth');

router.post('/', auth, createTask);

module.exports = router;