package com.stacklog.class_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.class_service.model.entities.GroupStudent;
import com.stacklog.class_service.model.service.GroupService;
import com.stacklog.class_service.model.service.GroupsStudentService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping(path = "/groupstudent")
public class GroupStudentRestController {

    @Autowired
    GroupsStudentService groupsStudentService;

    @Autowired
    GroupService groupService;

    @GetMapping("")
    public ResponseEntity<List<GroupStudent>> getGroupStudentByUserId(@RequestHeader("Authorization") String token) {
        List<GroupStudent> groupStudents = groupsStudentService.getAllByUserId(token);
        return ResponseEntity.ok().body(groupStudents);
    }

    @PostMapping("")
    public ResponseEntity<GroupStudent> saveGroupStudent(@RequestHeader("Authorization") String token,
            @RequestBody GroupStudent groupStudent) {
        GroupStudent newGroupStudent = groupsStudentService.save(groupStudent, token);

        return ResponseEntity.ok().body(newGroupStudent);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<GroupStudent> deleteGroupStudent(@RequestHeader("Authorization") String token,
            @PathVariable(name = "groupId") String groupId) {
        groupsStudentService.delete(groupId, token);
        return ResponseEntity.ok().body(null);
    }

    @PutMapping("/leave-group/{groupId}")
    public ResponseEntity<GroupStudent> outGroup(@RequestHeader("Authorization") String token,
            @PathVariable(name = "groupId") String groupId) {
        GroupStudent gs = groupsStudentService.getByGroupId(groupId, token);
        gs.setGroups(groupService.getAllByClassId(token, gs.getGroups().getClasses().getClassesId()).stream()
                .filter(g -> g.getGroupsName().equals("unassigned")).findFirst().get());
        gs = groupsStudentService.save(gs, token);
        return ResponseEntity.ok().body(gs);
    }

}
