const axios = require('axios');

// Hàm lấy thông tin các nhóm từ API class-service
async function getGroups() {
  try {
    const response = await axios.get('http://class-service:2003/group/class');
    return response.data; // Trả về dữ liệu nhóm
  } catch (error) {
    console.error('Error fetching groups:', error);
    throw error;
  }
}

// Hàm lấy thông tin email của học viên từ API profile-service
async function getUserEmail(userId) {
  try {
    const response = await axios.get(`http://profile-service:2002/user/${userId}`);
    return response.data.user.email; // Trả về email của người dùng
  } catch (error) {
    console.error(`Error fetching user email for ${userId}:`, error);
    throw error;
  }
}

// Hàm lấy tất cả các studentId từ API và trích xuất email của họ
async function getStudentEmails() {
  try {
    // Lấy danh sách nhóm từ class-service
    const groups = await getGroups();
    
    // Lấy danh sách userId của học viên từ các nhóm
    const userIds = [];
    groups.forEach(group => {
      group.groupStudents.forEach(student => {
        userIds.push(student.userId);
      });
    });

    // Lấy email của từng học viên
    const emails = [];
    for (const userId of userIds) {
      const email = await getUserEmail(userId); // Lấy email của từng học viên
      emails.push(email);
    }

    return emails;
  } catch (error) {
    console.error('Error getting student emails:', error);
    throw error;
  }
}

module.exports = {
  getGroups,
  getUserEmail,
  getStudentEmails
};
