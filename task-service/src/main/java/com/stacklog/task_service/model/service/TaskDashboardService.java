package com.stacklog.task_service.model.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.stacklog.task_service.payload.ResponseOverall.AssigneeStatistic;
import com.stacklog.task_service.payload.ResponseOverall.CompletionTrend;
import com.stacklog.task_service.payload.ResponseOverall.SectionStatistic;
import com.stacklog.task_service.payload.ResponseOverall.UserOverview;

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
        List<TaskAssign> assigns = taskAssignRepository.findAllByGroupId(groupId);

        double totalTasks = allTasks.size();

        // ====== 2️⃣ Tính phần trăm theo trạng thái ======
        Map<String, Double> completionRate = new LinkedHashMap<>();

        // Gom nhóm task theo statusTaskId (đảm bảo chính xác hơn so với name)
        Map<String, Long> statusCountMap = allTasks.stream()
                .filter(t -> t.getStatusTask() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getStatusTask().getStatusTaskId(),
                        Collectors.counting()));

        // Duyệt qua danh sách statusTask trong cùng group để đảm bảo thứ tự và đồng bộ
        // màu
        for (StatusTask status : statusTasks) {
            String statusId = status.getStatusTaskId();
            String name = status.getStatusTaskName();
            long count = statusCountMap.getOrDefault(statusId, 0L);
            completionRate.put(name, percent(count, totalTasks));
        }

        // Nếu tồn tại task chưa gán statusTaskId (null)
        long noStatus = allTasks.stream()
                .filter(t -> t.getStatusTask() == null)
                .count();
        if (noStatus > 0) {
            completionRate.put("Unassigned", percent(noStatus, totalTasks));
        }

        // ====== 3️⃣ Thống kê theo section ======
        // (Giả định task có chứa keyword phân loại, ví dụ title chứa “Hiring”,
        // “Interviews”)
        Map<String, Long> sectionMap = allTasks.stream()
                .collect(Collectors.groupingBy(t -> detectSection(t.getTaskTitle()),
                        Collectors.counting()));

        List<SectionStatistic> sectionStats = sectionMap.entrySet().stream()
                .map(e -> SectionStatistic.builder()
                        .sectionName(e.getKey())
                        .incompleteCount(e.getValue())
                        .build())
                .toList();

        // ====== 4️⃣ Thống kê theo người phụ trách (assignee) ======
        // Tính trung bình mỗi tháng số task & task hoàn thành
        List<AssigneeStatistic> assigneeStats = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            final int monthIdx = i;
            String month = LocalDate.of(2025, monthIdx, 1).getMonth().name().substring(0, 3);
            long totalMonth = assigns.stream().filter(a -> a.getCreatedAt().getMonthValue() == monthIdx)
                    .count();
            long completedMonth = assigns.stream()
                    .filter(a -> a.getCreatedAt().getMonthValue() == monthIdx &&
                            a.getTask() != null &&
                            a.getTask().getStatusTask() != null &&
                            a.getTask().getStatusTask().getStatusTaskName()
                                    .equalsIgnoreCase("Completed"))
                    .count();

            assigneeStats.add(
                    AssigneeStatistic.builder()
                            .month(month)
                            .totalTask(totalMonth)
                            .completedTask(completedMonth)
                            .build());
        }

        // ====== 5️⃣ Biểu đồ xu hướng hoàn thành theo thời gian ======
        List<CompletionTrend> trends = new ArrayList<>();
        for (int i = 1; i <= 24; i++) { // giả lập 24 ngày
            double value = 3.5 + Math.random() * 1.0; // tạo dữ liệu mô phỏng
            trends.add(
                    CompletionTrend.builder()
                            .date("12/" + i)
                            .completedRate(value)
                            .build());
        }

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

        // ====== 7️⃣ Build ResponseOverall ======
        return ResponseOverall.builder()
                .totalTask(totalTasks)
                .taskCompletionRate(completionRate)
                .sectionStatistics(sectionStats)
                .assigneeStatistics(assigneeStats)
                .completionTrends(trends)
                .userOverviews(userOverviews)
                .build();
    }

    private static Double percent(long part, double total) {
        return total == 0 ? 0 : Math.round((part / total) * 10000.0) / 100.0;
    }

    private static String detectSection(String title) {
        if (title == null)
            return "Other";
        String lower = title.toLowerCase();
        if (lower.contains("hire"))
            return "Hiring";
        if (lower.contains("interview"))
            return "Interviews";
        if (lower.contains("shortlist"))
            return "Shortlisting";
        if (lower.contains("contract"))
            return "Contracts";
        return "Next task";
    }

}
