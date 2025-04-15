const express = require('express');
const { createUser, updateUser, deleteUser, findById } = require('../controller/userController');
const { validateUser } = require('../middleware/validateUserMiddleware');

const router = express.Router();

// Create User (with validation middleware)
router.post('/', createUser);

// Update User
router.put('/:id', updateUser);

// Soft Delete User
router.delete('/:id', deleteUser);

// find by id
router.get('/:id', findById)

module.exports = router;
