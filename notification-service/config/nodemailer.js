const nodemailer = require('nodemailer');
require("dotenv").config();

// Tạo một transporter với thông tin SMTP của bạn
export async function sendEmail(to, subject, text, attachments = []) {
  // Tạo một transporter với thông tin SMTP của bạn
  let transporter = nodemailer.createTransport({
    service: 'gmail', // Sử dụng Gmail để gửi email (có thể thay đổi cho các dịch vụ khác)
    auth: {
      user: process.env.EMAIL_STACKLOG_USER,
      pass: process.env.EMAIL_STACKLOG_PASSWORD
    }
  });

  // Định nghĩa email bạn muốn gửi
  let mailOptions = {
    from: process.env.EMAIL_STACKLOG_USER,
    to: to,
    subject: subject,
    text: text,
    attachments: attachments
  };

  // Gửi email
  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
      return console.log('Error: ' + error);
    }
    console.log('Email sent: ' + info.response);
  });
}
