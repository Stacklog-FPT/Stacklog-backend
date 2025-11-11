package com.stacklog.task_service.model.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.StatusTask;
import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.entities.TaskAssign;
import com.stacklog.task_service.model.repo.StatusTaskRepo;
import com.stacklog.task_service.model.repo.TaskAssignRepo;
import com.stacklog.task_service.model.repo.TaskRepo;
import com.stacklog.task_service.payload.ResponseOverall;
import com.stacklog.task_service.payload.ResponseOverall.UserOverview;
import com.stacklog.task_service.payload.ResponseOverall.UpcomingDeadline;

@Service
public class TaskDashboardService {

    @Autowired
    private TaskRepo taskRepository;

    @Autowired
    private StatusTaskRepo statusTaskRepository;

    @Autowired
    private TaskAssignRepo taskAssignRepository;

    public ResponseOverall getOverallStatistics(String groupId) {

        // ====== 1️⃣ Lấy dữ liệu gốc ======
        List<Task> allTasks = taskRepository.findByGroupId(groupId);
        List<StatusTask> statusTasks = statusTaskRepository.findAllByGroupId(groupId);
        List<TaskAssign> assigns = taskAssignRepository.findAllByTaskGroupId(groupId);
        double totalTasks = allTasks.size();

        // ====== 2️⃣ Tính % task theo trạng thái ======
        Map<String, Double> statusPercentMap = new LinkedHashMap<>();
        Map<String, Long> statusCountMap = allTasks.stream()
                .filter(t -> t.getStatusTask() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getStatusTask().getStatusTaskId(),
                        Collectors.counting()));

        for (StatusTask status : statusTasks) {
            String statusId = status.getStatusTaskId();
            String name = status.getStatusTaskName();
            long count = statusCountMap.getOrDefault(statusId, 0L);
            statusPercentMap.put(name, percent(count, totalTasks));
        }

        // Nếu có task chưa gán trạng thái
        long noStatus = allTasks.stream()
                .filter(t -> t.getStatusTask() == null)
                .count();
        if (noStatus > 0) {
            statusPercentMap.put("Unassigned", percent(noStatus, totalTasks));
        }

        // ====== 3️⃣ Tính % đóng góp task của từng thành viên ======
        Map<String, Long> taskByMember = assigns.stream()
                .collect(Collectors.groupingBy(TaskAssign::getAssignTo, Collectors.counting()));

        Map<String, Double> memberContribution = new LinkedHashMap<>();
        for (Map.Entry<String, Long> e : taskByMember.entrySet()) {
            memberContribution.put(e.getKey(), percent(e.getValue(), assigns.size()));
        }

        // ====== 4️⃣ Điểm trung bình theo nhóm ======
        // (Tính trung bình completion rate của tất cả user)
        double groupAverageScore = allTasks.stream()
                .filter(t -> t.getStatusTask() != null)
                .mapToDouble(t -> t.getStatusTask().getStatusTaskName().equalsIgnoreCase("Completed") ? 1 : 0)
                .average()
                .orElse(0.0) * 100.0;

        // ====== 5️⃣ Upcoming Deadline (7 ngày tới) ======
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);

        List<Task> upcomingTasks = allTasks.stream()
                .filter(t -> t.getTaskDueDate() != null)
                .filter(t -> !t.getTaskDueDate().isBefore(now) && !t.getTaskDueDate().isAfter(nextWeek))
                .toList();

        // Gom nhóm theo ngày
        Map<LocalDateTime, Long> deadlineCountMap = upcomingTasks.stream()
                .collect(Collectors.groupingBy(Task::getTaskDueDate, Collectors.counting()));

        List<UpcomingDeadline> upcoming = deadlineCountMap.entrySet().stream()
                .map(e -> UpcomingDeadline.builder()
                        .day(e.getKey().toString())
                        .totalTask(e.getValue().intValue())
                        .build())
                .sorted(Comparator.comparing(UpcomingDeadline::getDay))
                .toList();

        // ====== 6️⃣ Thống kê người dùng cụ thể ======
        Map<String, List<TaskAssign>> assignByUser = assigns.stream()
                .collect(Collectors.groupingBy(TaskAssign::getAssignTo));

        List<UserOverview> userOverviews = assignByUser.entrySet().stream().map(entry -> {
            String user = entry.getKey();
            List<Task> userTasks = entry.getValue().stream()
                    .map(TaskAssign::getTask)
                    .filter(Objects::nonNull)
                    .toList();

            int total = userTasks.size();
            int remaining = (int) userTasks.stream()
                    .filter(t -> t.getStatusTask() == null ||
                            !t.getStatusTask().getStatusTaskName()
                                    .equalsIgnoreCase("Completed"))
                    .count();

            double completion = total == 0 ? 0 : ((total - remaining) * 100.0 / total);

            return UserOverview.builder()
                    .userId(user)
                    .totalTask(total)
                    .remainingTask(remaining)
                    .completionPercent(completion)
                    .colorCode(completion >= 80 ? "#00A36C" : "#FF7F50")
                    .build();
        }).toList();

        // ====== 7️⃣ Trả về kết quả tổng hợp ======
        return ResponseOverall.builder()
                .totalTask(totalTasks)
                .taskCompletionRate(statusPercentMap)
                .memberContribution(memberContribution)
                .groupAverageScore(groupAverageScore)
                .upcomingDeadlines(upcoming)
                .userOverviews(userOverviews)
                .build();
    }

    private static Double percent(long part, double total) {
        return total == 0 ? 0 : Math.round((part / total) * 10000.0) / 100.0;
    }
}
