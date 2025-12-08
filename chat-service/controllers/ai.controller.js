require('dotenv').config();
const { GoogleGenAI } = require("@google/genai");

const ai = new GoogleGenAI({});

async function createTaskContentFromGemini(prompt) {
  try {

    const response = await ai.models.generateContent({
        model: "gemini-2.5-flash",
        contents: prompt,
    });

    // Trả về nội dung task đã được tạo từ AI
    const generatedText = response.candidates[0].content.parts[0].text;
    const generatedJson = JSON.parse(cleanJsonString(generatedText));
    return generatedJson;

  } catch (error) {
    console.error('Error generating task content:', error.response?.data || error.message);
    throw new Error('Failed to generate task content');
  }
}

function cleanJsonString(str) {
  return str
    .replace(/```json/g, "")   // xoá ```json
    .replace(/```/g, "")       // xoá ```
    .trim();
}

// Tạo prompt từ các thuộc tính của task
function generatePromptSuggestTask(taskDetails) {
  return `
        You are a senior product owner generating tasks for a software development project.

            🎯 Project Strategy / Direction:
            - Focus on stability, bug reduction, and performance improvements.
            - Improve user experience and reduce technical debt.
            - Ensure clarity, consistency, and maintainability in all tasks.
            - Every task should be actionable, testable, and aligned with sprint goals.

            📌 Your job:
            Generate a complete JSON object representing a task, using the details below.
            The task description must:
            - Be detailed, clear, and aligned with the project strategy.
            - Include context, goal, acceptance criteria, and expected outcomes.
            - Expand vague inputs into full meaningful content.
            - Return a JSON object ONLY.

        Using the task details below, generate a complete JSON task object.
        Do NOT include explanations, markdown, quotes, or extra text. Return valid JSON only.

        {
        "taskId": "${taskDetails.taskId || ""}",
        "taskTitle": "${taskDetails.taskTitle}",
        "taskDescription": "Generate a detailed task description here.",
        "statusTaskId": "${taskDetails.statusTaskId || ""}",
        "taskPoint": "${taskDetails.taskPoint || ""}",
        "parentTaskId": "${taskDetails.parentTaskId || ""}",
        "taskStartTime": "${taskDetails.taskStartTime}",
        "taskDueDate": "${taskDetails.taskDueDate}",
        "priority": "${taskDetails.priority}"
        }

        Fill missing fields logically.
        `;
}

function generatePromptSuggestArrayTask(sprintGoal, startTime, endTime, amountTask) {
  return `
        You are a senior product owner generating tasks for a software development project.

            🎯 Project Strategy:
            - Focus on system stability, performance optimization, UX improvements, and reducing technical debt.
            - Tasks must be actionable, testable, and aligned with sprint goals.
            - ${sprintGoal}
            - Time to start the project: ${startTime}
            - Time to end the project: ${endTime}
            - Approximate number of tasks: ${amountTask}
            📌 Your Job:
            - Generate a JSON array containing multiple tasks (from 3 to 10 items depending on the complexity of the input).
            - Each task must follow the format:

            {
                "taskId": "auto-generate or use provided",
                "taskTitle": "",
                "taskDescription": "",
                "statusTaskId": "",
                "taskPoint": "",
                "parentTaskId": "",
                "taskStartTime": "",
                "taskDueDate": "",
                "priority": ""
            }
        Do NOT include explanations, markdown, quotes, or extra text. Return valid JSON only.

        Fill missing fields logically.
        `;
}

module.exports.createTask = async (req, res) => {
  try {
    const taskDetails = req.body; // Lấy thông tin task từ request body
    let prompt = '';
    if (!taskDetails && !taskDetails.taskTitle && !taskDetails.taskDescription) {
        prompt = generatePromptSuggestTask(taskDetails);
    } else {
        prompt = generatePromptSuggestArrayTask(req.params.sprintGoal, req.params.startTime, req.params.endTime, req.params.amount);
    }

    const taskDescription = await createTaskContentFromGemini(prompt);
    

    // Trả về danh sách task với nội dung được tạo từ AI
    return res.status(200).json(taskDescription);

  } catch (error) {
    console.error("Error in creating task:", error.message);
    return res.status(500).json({ message: "Internal Server Error", error: error.message });
  }
};
