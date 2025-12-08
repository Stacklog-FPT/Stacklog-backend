const router = require('express').Router();
const { createTask } = require('../controllers/ai.controller')
const auth = require('../middleware/auth');

router.post('/:sprintGoal/:startTime/:endTime/:amount', auth, createTask);

module.exports = router;
