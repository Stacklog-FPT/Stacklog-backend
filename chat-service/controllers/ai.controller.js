const { axios } = require('axios');
require('dotenv').config();

const GEMINI_API_KEY = process.env.GEMINI_API_KEY;

async function createTaskContentFromGemini(taskDetails) {
  try {
    const prompt = generatePromptSuggestTask(taskDetails);

    const response = await axios.post(`https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=${GEMINI_API_KEY}`, {
      model: 'gemini-2.5-flash',
      content: [
        {
          role: "user",
          parts: [{ text: prompt }]
        }
      ],
      config: {
        temperature: 0.7,
        maxOutputTokens: 200,
      }
    });

    // Trả về nội dung task đã được tạo từ AI
    const generatedText = response.data.candidates[0].content.parts[0].text;
    return generatedText.trim();

  } catch (error) {
    console.error('Error generating task content:', error.response?.data || error.message);
    throw new Error('Failed to generate task content');
  }
}

// Tạo prompt từ các thuộc tính của task
function generatePromptSuggestTask(taskDetails) {
  return `
    Create a task description based on the following details:

    Task Title: ${taskDetails.taskTitle}
    Task Description: ${taskDetails.taskDescription}
    Task Start Time: ${taskDetails.taskStartTime}
    Task Due Date: ${taskDetails.taskDueDate}
    Task Priority: ${taskDetails.priority}
    Users Assigned: ${taskDetails.listUserAssign.join(", ")}
    SubTasks: ${taskDetails.subTasks.map(sub => sub).join(", ")}
    Reviews: ${taskDetails.reviews.map(review => review.reviewContent).join(", ")}
    CheckLists: ${taskDetails.checkLists.map(list => list.checkListName).join(", ")}

    Generate a clear and concise task description, including the task's priority, users assigned, and any associated details.
    `;
}

module.exports.createTask = async (req, res) => {
  try {
    const taskDetails = req.body; // Lấy thông tin task từ request body

    // Validate input
    if (!taskDetails || !taskDetails.taskTitle || !taskDetails.taskDescription) {
      return res.status(400).json({ message: "Task title and description are required" });
    }

    // Gọi Gemini API để tạo nội dung cho task
    const taskDescription = await createTaskContentFromGemini(taskDetails);

    // Trả về danh sách task với nội dung được tạo từ AI
    return res.status(200).json({
      taskId: taskDetails.taskId || generateTaskId(),
      taskTitle: taskDetails.taskTitle,
      taskDescription: taskDescription,
      statusTaskId: taskDetails.statusTaskId,
      taskPoint: taskDetails.taskPoint,
      parentTaskId: taskDetails.parentTaskId,
      taskStartTime: taskDetails.taskStartTime,
      taskDueDate: taskDetails.taskDueDate,
      priority: taskDetails.priority,
      listUserAssign: taskDetails.listUserAssign,
      subTasks: taskDetails.subTasks,
      reviews: taskDetails.reviews,
      checkLists: taskDetails.checkLists
    });

  } catch (error) {
    console.error("Error in creating task:", error.message);
    return res.status(500).json({ message: "Internal Server Error", error: error.message });
  }
};