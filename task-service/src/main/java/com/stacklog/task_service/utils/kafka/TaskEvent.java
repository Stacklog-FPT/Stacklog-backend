package com.stacklog.task_service.utils.kafka;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {
    private Long taskId;
    private String eventType;
    private String createdBy;
    private LocalDateTime createdAt;
}
