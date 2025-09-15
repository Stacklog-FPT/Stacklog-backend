const router = require('express').Router();
const ctrl = require('../controllers/messages.controller');

router.post('/:boxId', ctrl.send);
router.get('/:boxId', ctrl.list);
router.put('/recall/:messageId', ctrl.recall);
router.delete('/:messageId', ctrl.remove);
router.post('/read/:boxId', ctrl.readReset);

module.exports = router;
