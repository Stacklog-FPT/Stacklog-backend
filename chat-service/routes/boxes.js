const router = require('express').Router();
const ctrl = require('../controllers/boxes.controller');
const auth = require('../middleware/auth');

router.post('/', auth, ctrl.create);
router.post('/:boxId/members', auth, ctrl.addMembers);
router.get('/', auth, ctrl.listByUser);

module.exports = router;
