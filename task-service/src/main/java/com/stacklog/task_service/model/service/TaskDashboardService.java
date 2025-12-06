package com.stacklog.task_service.model.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.StatusTask;
import com.stacklog.task_service.model.entities.Task;
// import com.stacklog.task_service.model.entities.TaskAssign;
import com.stacklog.task_service.model.entities.Task.Priority;
import com.stacklog.task_service.model.repo.StatusTaskRepo;
import com.stacklog.task_service.model.repo.TaskRepo;
import com.stacklog.task_service.payload.ResponseOverall;
import com.stacklog.task_service.payload.ResponseOverall.UserOverview;
import com.stacklog.task_service.payload.ResponseOverall.StatusTaskRate;
import com.stacklog.task_service.payload.ResponseOverall.UpcomingDeadline;

@Service
public class TaskDashboardService {

        @Autowired
        private TaskRepo taskRepository;

        @Autowired
        private StatusTaskRepo statusTaskRepository;

        @Autowired
        ScoreServiceClient scoreServiceClient;

        public ResponseOverall getOverallStatistics(String groupId, String token) {

                // ====== 1️⃣ Lấy dữ liệu gốc ======
                List<Task> allTasks = taskRepository.findByGroupId(groupId);
                List<StatusTask> statusTasks = statusTaskRepository.findAllByGroupId(groupId);
                double totalTasks = allTasks.size();

                // ====== 2️⃣ Tính % task theo trạng thái ======
                Map<String, Long> statusCountMap = allTasks.stream()
                                .filter(t -> t.getStatusTask() != null)
                                .collect(Collectors.groupingBy(
                                                t -> t.getStatusTask().getStatusTaskId(),
                                                Collectors.counting()));

                List<StatusTaskRate> statusTaskRates = statusTasks.stream()
                                .map(status -> {
                                        String statusId = status.getStatusTaskId();
                                        String name = status.getStatusTaskName();
                                        String color = status.getStatusTaskColor();
                                        long count = statusCountMap.getOrDefault(statusId, 0L);
                                        double taskCompletionRate = percent(count, totalTasks);
                                        return new StatusTaskRate(statusId, name, color, taskCompletionRate);
                                })
                                .collect(Collectors.toList());

                // Nếu có task chưa gán trạng thái
                long noStatus = allTasks.stream()
                                .filter(t -> t.getStatusTask() == null)
                                .count();
                if (noStatus > 0) {
                        statusTaskRates.add(new StatusTaskRate("Unassigned", "Unassigned", "#FF0000",
                                        percent(noStatus, totalTasks)));
                }

                // ====== 3️⃣ Tính % đóng góp theo độ khó task ======

                Map<String, Double> completedByMember = allTasks.stream()
                                .filter(t -> t.getStatusTask() != null
                                                && t.getStatusTask().getStatusTaskName().toLowerCase()
                                                                .contains("complete"))

                                .flatMap(t -> {
                                        if (t.getAssigns() == null)
                                                return Stream.<AbstractMap.SimpleEntry<String, Double>>empty();
                                        return t.getAssigns().stream()
                                                        .map(a -> new AbstractMap.SimpleEntry<String, Double>(
                                                                        a.getAssignTo(),
                                                                        convertPriority(t.getPriority())));
                                })

                                .filter(e -> e.getKey() != null && !e.getKey().isBlank())

                                .collect(Collectors.groupingBy(
                                                AbstractMap.SimpleEntry::getKey,
                                                Collectors.summingDouble(AbstractMap.SimpleEntry::getValue)));

                // Tổng độ khó của tất cả thành viên
                double totalDifficulty = completedByMember.values().stream()
                                .mapToDouble(Double::doubleValue)
                                .sum();

                // Tính %
                Map<String, Double> memberContribution = new LinkedHashMap<>();
                for (var e : completedByMember.entrySet()) {
                        memberContribution.put(e.getKey(), percent(e.getValue(), totalDifficulty));
                }

                // ====== 4️⃣ Điểm trung bình theo nhóm ======
                List<ScoreItem> getAllScoreItems = scoreServiceClient.getScoreItemsByGroupId(token, groupId,
                                "Assignment");
                double totalScore = 0.0;
                for (ScoreItem scoreItem : getAllScoreItems) {
                        totalScore += scoreItem.getScoreItemValue(); // Cộng điểm của mỗi ScoreItem vào tổng
                }
                double groupAverageScore = getAllScoreItems.isEmpty() ? 0.0 : totalScore / getAllScoreItems.size();

                // ====== 5️⃣ Upcoming Deadline (7 ngày tới) ======
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime nextWeek = now.plusDays(7);

                List<Task> upcomingTasks = allTasks.stream()
                                .filter(t -> t.getTaskDueDate() != null)
                                .filter(t -> !t.getTaskDueDate().isBefore(now) && !t.getTaskDueDate().isAfter(nextWeek))
                                .toList();

                Map<LocalDateTime, Long> deadlineCountMap = upcomingTasks.stream()
                                .collect(Collectors.groupingBy(Task::getTaskDueDate, Collectors.counting()));

                List<UpcomingDeadline> upcoming = deadlineCountMap.entrySet().stream()
                                .map(e -> {
                                        // Lấy số lượng task đã hoàn thành cho mỗi ngày
                                        int taskCompleted = (int) upcomingTasks.stream()
                                                        .filter(t -> t.getTaskDueDate().equals(e.getKey()) &&
                                                                        t.getStatusTask() != null &&
                                                                        t.getStatusTask().getStatusTaskName()
                                                                                        .equalsIgnoreCase("Completed"))
                                                        .count();
                                        return new UpcomingDeadline(e.getKey().toString(), e.getValue().intValue(),
                                                        taskCompleted);
                                })
                                .sorted(Comparator.comparing(UpcomingDeadline::getDay))
                                .toList();

                // ====== 6️⃣ Thống kê người dùng cụ thể ======
                Map<String, List<Task>> tasksByUser = allTasks.stream()
                                .flatMap(task -> task.getAssigns().stream()
                                                .map(a -> Map.entry(a.getAssignTo(), task)))
                                .collect(Collectors.groupingBy(
                                                Map.Entry::getKey,
                                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

                List<UserOverview> userOverviews = tasksByUser.entrySet().stream()
                                .map(entry -> {
                                        String userId = entry.getKey();
                                        List<Task> userTasks = entry.getValue();

                                        int total = userTasks.size();

                                        int remaining = (int) userTasks.stream()
                                                        .filter(t -> t.getStatusTask() == null ||
                                                                        !"Completed".equalsIgnoreCase(t.getStatusTask()
                                                                                        .getStatusTaskName()))
                                                        .count();

                                        double completion = (total == 0) ? 0 : ((total - remaining) * 100.0 / total);

                                        return UserOverview.builder()
                                                        .userId(userId)
                                                        .totalTask(total)
                                                        .remainingTask(remaining)
                                                        .completionPercent(completion)
                                                        .colorCode(completion >= 80 ? "#00A36C" : "#FF7F50")
                                                        .build();
                                })
                                .toList();

                // ====== 7️⃣ Trả về kết quả tổng hợp ======
                return ResponseOverall.builder()
                                .groupId(groupId)
                                .totalTask(totalTasks)
                                .taskCompletionRate(statusTaskRates)
                                .memberContribution(memberContribution)
                                .groupAverageScore(groupAverageScore)
                                .upcomingDeadlines(upcoming)
                                .userOverviews(userOverviews)
                                .build();
        }

        private static Double percent(double part, double total) {
                return total == 0 ? 0 : Math.round((part / total) * 10000.0) / 100.0;
        }

        private Double convertPriority(Priority p) {
                if (p == null)
                        return 1.00; // Mặc định LOW
                switch (p.name()) {
                        case "HIGH":
                                return 3.00;
                        case "MEDIUM":
                                return 2.00;
                        case "LOW":
                                return 1.00;
                        default:
                                return 1.00; // fallback
                }
        }
}
