package com.stacklog.task_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.entities.CheckList;
import com.stacklog.task_service.model.service.CheckListService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping(path = {"/checklist"})
public class RestChecklistController {
    
    @Autowired
    CheckListService checkListService;

    @GetMapping("/{taskId}")
    public ResponseEntity<List<CheckList>> getCheckListByTaskId(@PathVariable(name = "taskId") String taskId) {
        List<CheckList> checkLists = checkListService.getCheckListsByIdTask(Long.parseLong(taskId));
        return ResponseEntity.ok().body(checkLists);
    }

    @PostMapping("")
    public ResponseEntity<CheckList> saveCheckList(@RequestBody CheckList checkList) {
        checkListService.save(checkList);
        return ResponseEntity.ok(checkList);
    }
    
    @DeleteMapping("/{checkListId}")
    public ResponseEntity<CheckList> deleteCheckList(@PathVariable(name = "checkListId") String checkListId) {
        CheckList checkList = checkListService.remove(Long.parseLong(checkListId));
        return ResponseEntity.ok().body(checkList);
    }
    

}
