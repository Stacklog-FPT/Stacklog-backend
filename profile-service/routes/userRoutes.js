const express = require('express');
const { createUser, updateUser, deleteUser, findByEmail, findByClassId, findByRole, findById, findByNameOrEmail, createListUser, importExcel, exportExcel } = require('../controller/userController');
const { validateUser } = require('../middleware/validateUserMiddleware');
const multer = require("multer");


const router = express.Router();

// Create User (with validation middleware)
router.post('/save', createUser);

// get user by id
router.get('/:userId', findById);

// Update User
router.put('/update/:id', updateUser);

// Soft Delete User
router.delete('/delete/:id', deleteUser);

// find by email
router.get('/email/:email', findByEmail);

// find by name or email
router.get('/find/:searchWord', findByNameOrEmail);

// list by role
router.get('/role/:role', findByRole);

// find by classId
router.get("/class/:classId", findByClassId);

// add list user
router.post("/addAll", createListUser)

// import excel 
const upload = multer({ dest: "uploads/" });
router.post("/import-excel/:role", upload.single('file'), importExcel)

// export excel 
router.get("/export-excel/:role", exportExcel)

module.exports = router;
