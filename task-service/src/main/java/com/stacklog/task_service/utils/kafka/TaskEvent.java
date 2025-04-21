package com.stacklog.task_service.utils.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEvent {
    private String message;
    private String status;
    private String task;
}
