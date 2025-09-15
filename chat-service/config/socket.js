// config/socket.js
const { Server } = require('socket.io');
let io = null;

function attachSocket(server) {
  io = new Server(server, { cors: { origin: process.env.CORS_ORIGIN || '*' } });

  io.on('connection', (socket) => {
    const userId = socket.handshake.query?.userId;
    if (userId) socket.join(`user:${userId}`);

    socket.on('room:join', (boxId) => socket.join(`box:${boxId}`));
    socket.on('room:leave', (boxId) => socket.leave(`box:${boxId}`));
  });
}

function ioEmit(event, payload, room) {
  if (!io) return;
  if (room) io.to(room).emit(event, payload);
  else io.emit(event, payload);
}

module.exports = { attachSocket, ioEmit };
