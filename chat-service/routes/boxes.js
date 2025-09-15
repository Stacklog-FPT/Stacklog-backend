const router = require('express').Router();
const ctrl = require('../controllers/boxes.controller');

router.post('/', ctrl.create);
router.post('/:boxId/members', ctrl.addMembers);
router.get('/', ctrl.listByUser);

module.exports = router;
