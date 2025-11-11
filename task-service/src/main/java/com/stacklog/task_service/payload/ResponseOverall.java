package com.stacklog.task_service.payload;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseOverall {

    private String groupId;

    // === Tổng quan toàn hệ thống ===
    private Double totalTask; // tổng số task (vd: 66.577)
    private Map<String, Double> taskCompletionRate; // Tỉ lệ phần trăm các status của task
    private Map<String, Double> memberContribution; // % đóng góp của từng thành viên
    private Double groupAverageScore; // Điểm trung bình của nhóm
    private List<UpcomingDeadline> upcomingDeadlines; // Các task có deadline sắp tới

    // === Tổng hợp cá nhân cho dashboard trên cùng ===
    private List<UserOverview> userOverviews;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionStatistic {
        private String sectionName;
        private Long incompleteCount; // số task chưa xong
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssigneeStatistic {
        private String month; // vd: "Jan", "Feb"
        private Long totalTask;
        private Long completedTask;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompletionTrend {
        private String date; // vd: "12/01"
        private Double completedRate; // tỉ lệ hoàn thành theo thời gian
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserOverview {
        private String userId; // vd: "Nhat.Truong"
        private int totalTask; // tổng số task (10)
        private int remainingTask; // số task còn lại
        private double completionPercent; // 80%
        private String colorCode; // vd: #FF9F43 (cho biểu đồ ring)
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpcomingDeadline {
        private String day; // ngày deadline, ví dụ "2025-11-12"
        private int totalTask; // tổng số task có deadline trong ngày này
    }

}
