const axios = require('axios');

// Tạo 1 instance để call đến http://profile-service:2001/user/:userId để lấy thông tin user
const profileServiceApi = axios.create({
    baseURL: process.env.PROFILE_SERVICE_URL || 'http://profileservice:2001',
    timeout: 5000,
});

// Hàm lấy thông tin user từ profile-service
const getUserInfo = async (userId) => {
    try {
        const response = await profileServiceApi.get(`/user/${userId}`, {
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching user info from profile-service:', error);
        throw error;
    }
};

module.exports = {
    getUserInfo,
};
