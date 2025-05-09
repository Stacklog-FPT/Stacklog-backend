package com.stacklog.class_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.class_service.model.entities.GroupStudent;
import com.stacklog.class_service.model.service.GroupsStudentService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping(path = "/groupstudent")
public class GroupStudentRestController {
    
    @Autowired GroupsStudentService groupsStudentService;

    @GetMapping("")
    public ResponseEntity<List<GroupStudent>> getGroupStudentByUserId(@RequestHeader("Authorization") String token) {
        List<GroupStudent> groupStudents = groupsStudentService.getAllByUserId(token);
        return ResponseEntity.ok().body(groupStudents);
    }

    @PostMapping("")
    public ResponseEntity<GroupStudent> saveGroupStudent(@RequestHeader("Authorization") String token, @RequestBody GroupStudent groupStudent) {
        GroupStudent newGroupStudent = groupsStudentService.save(groupStudent, token);
        
        return ResponseEntity.ok().body(newGroupStudent);
    }

    @DeleteMapping("/{groupStudentId}")
    public ResponseEntity<GroupStudent> deleteGroupStudent(@PathVariable(name = "groupStudentId") String groupStudentId) {
        return ResponseEntity.ok().body(null);
    }    
    

}
