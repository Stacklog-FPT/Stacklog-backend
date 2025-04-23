package com.stacklog.task_service.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.CheckItem;
import com.stacklog.task_service.model.entities.CheckList;
import com.stacklog.task_service.model.entities.StatusTask;
import com.stacklog.task_service.model.entities.Task;
import com.stacklog.task_service.model.entities.TaskAssign;

@Service
public class TaskCoordinatorService {

    @Autowired
    TaskService taskService;
    @Autowired
    CheckItemService checkItemService;
    @Autowired
    CheckListService checkListService;
    @Autowired
    TaskAssignService taskAssignService;
    @Autowired
    StatusTaskService statusTaskService;

    // Task
    public Task saveTask(Task task) {
        return taskService.save(task);
    }

    public Task deleteTask(Long taskId) {
        return taskService.remove(taskId);
    }

    public List<Task> getTaskByGroupId(String groupId) {
        return taskService.getByGroupId(groupId);
    }

    // StatusTask
    public StatusTask saveStatusTask(StatusTask statusTask) {
        return statusTaskService.save(statusTask);
    }

    public StatusTask deleteStatusTask(Long statusId) {
        return statusTaskService.remove(statusId);
    }

    // TaskAssign
    public TaskAssign saveTaskAssign(TaskAssign assign) {
        return taskAssignService.save(assign);
    }

    public TaskAssign deleteTaskAssign(Long assignId) {
        return taskAssignService.remove(assignId);
    }

    // CheckList
    public CheckList saveCheckList(CheckList checkList) {
        return checkListService.save(checkList);
    }

    public CheckList deleteCheckList(Long checkListId) {
        return checkListService.remove(checkListId);
    }

    // CheckItem
    public CheckItem saveCheckItem(CheckItem checkItem) {
        return checkItemService.save(checkItem);
    }

    public CheckItem deleteCheckItem(Long checkItemId) {
        return checkItemService.remove(checkItemId);
    }

}
