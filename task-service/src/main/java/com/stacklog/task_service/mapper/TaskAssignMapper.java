package com.stacklog.task_service.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stacklog.task_service.model.entities.TaskAssign;
import com.stacklog.task_service.model.service.TaskAssignService;
import com.stacklog.task_service.model.service.TaskService;

@Component
public class TaskAssignMapper {
    @Autowired
    TaskService taskService;

    @Autowired
    TaskAssignService taskAssignService;

    public List<TaskAssign> generateAssigns(String taskId, String[] assignIds) {
        List<TaskAssign> taskAssigns = new ArrayList<>();

        Set<String> newAssignSet = new HashSet<>(Arrays.asList(assignIds));

        Map<String, TaskAssign> taskAssignMap = taskAssignService.getByTaskId(taskId).stream()
                .collect(Collectors.toMap(TaskAssign::getAssignTo, ta -> ta));

        for (String userId : newAssignSet) {
            if (taskAssignMap.containsKey(userId)) {
                // ✅ Đã tồn tại → giữ lại
                taskAssigns.add(taskAssignMap.get(userId));
            } else {
                // 🆕 Chưa tồn tại → tạo mới
                TaskAssign newAssign = new TaskAssign();
                newAssign.setTaskAssignId(UUID.randomUUID().toString());
                newAssign.setAssignTo(userId);
                newAssign.setTask(taskService.getById(taskId));
                taskAssignService.save(newAssign);
                taskAssigns.add(newAssign);
            }
        }

        List<String> keepUserIds = Arrays.asList(assignIds);

        taskAssigns.stream()
                .filter(assign -> !keepUserIds.contains(assign.getAssignTo()))
                .collect(Collectors.toList()).stream().forEach(ta -> {
                    taskAssignService.remove(ta.getTaskAssignId());
                });
        return taskAssigns;
    }

}
