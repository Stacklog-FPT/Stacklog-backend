package com.stacklog.score_service.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@FeignClient(name = "task-service", url = "http://taskservice:2002", path = "")
public interface TaskServiceClient {

    @GetMapping("/task/overall?groupId={groupId}")
    ResponseOverall getOverallTask(@RequestHeader("Authorization") String token,
            @RequestParam(name = "groupId") String groupId);

}


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ResponseOverall {

    private String groupId;

    // === Tổng quan toàn hệ thống ===
    private Double totalTask; // tổng số task (vd: 66.577)
    private List<StatusTaskRate> taskCompletionRate; // Tỉ lệ phần trăm các status của task
    private Map<String, Double> memberContribution; // % đóng góp của từng thành viên
    private Double groupAverageScore; // Điểm trung bình của nhóm
    private List<UpcomingDeadline> upcomingDeadlines; // Các task có deadline sắp tới

    // === Tổng hợp cá nhân cho dashboard trên cùng ===
    private List<UserOverview> userOverviews;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusTaskRate {
        private String statusTaskId;
        private String statusTaskName;
        private String colorCode;
        private Double taskCompletionRate; 
        
    }


    @Data
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpcomingDeadline {
        private String day; // ngày deadline, ví dụ "2025-11-12"
        private int totalTask; // tổng số task có deadline trong ngày này
        private int taskCompleted;
    }

}
