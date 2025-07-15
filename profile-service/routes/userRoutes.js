const express = require('express');
const { createUser, updateUser, deleteUser, findById, findByGroupId, findLecture, findStudent } = require('../controller/userController');
const { validateUser } = require('../middleware/validateUserMiddleware');

const router = express.Router();

// Create User (with validation middleware)
router.post('/', createUser);

// Update User
router.put('/:id', updateUser);

// Soft Delete User
router.delete('/:id', deleteUser);

// find by id
router.get('/:id', findById);

// list lecture
router.get('/lecture', findLecture);

// list student
router.get('/student', findStudent);

// find by groupId
router.get("/group/:groupId", findByGroupId);

module.exports = router;
