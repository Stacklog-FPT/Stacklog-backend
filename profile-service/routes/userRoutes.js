const express = require('express');
const { createUser, updateUser, deleteUser, findByEmail, findByGroupId, findByRole } = require('../controller/userController');
const { validateUser } = require('../middleware/validateUserMiddleware');

const router = express.Router();

// Create User (with validation middleware)
router.post('/', createUser);

// Update User
router.put('/:id', updateUser);

// Soft Delete User
router.delete('/:id', deleteUser);

// find by email
router.get('/:email', findByEmail);

// list lecture
router.get('/lecturer', findByRole);

// list student
router.get('/student', findByRole);

// find by groupId
router.get("/group/:groupId", findByGroupId);

module.exports = router;
