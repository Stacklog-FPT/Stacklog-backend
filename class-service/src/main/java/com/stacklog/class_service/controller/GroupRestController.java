package com.stacklog.class_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.class_service.model.entities.Classes;
import com.stacklog.class_service.model.entities.GroupStudent;
import com.stacklog.class_service.model.entities.Groupss;
import com.stacklog.class_service.model.service.ClassService;
import com.stacklog.class_service.model.service.GroupService;
import com.stacklog.class_service.model.service.GroupsStudentService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(path = "/group")
public class GroupRestController {

    @Autowired
    GroupService groupService;

    @Autowired
    GroupsStudentService groupsStudentService;

    @Autowired
    ClassService classService;

    @GetMapping("")
    public ResponseEntity<List<Groupss>> getGroupByUserId(@RequestHeader("Authorization") String token) {
        List<Groupss> groupsses = groupService.getAllByUserId(token);
        return ResponseEntity.ok().body(groupsses);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Groupss>> getGroupByClassId(@RequestHeader("Authorization") String token,
            @PathVariable(name = "classId") String classId) {
        List<Groupss> groupsses = groupService.getAllByClassId(token, classId);
        return ResponseEntity.ok().body(groupsses);
    }

    @PostMapping("")
    public ResponseEntity<Groupss> saveGroupss(@RequestBody GroupDTO groupDTO,
            @RequestHeader("Authorization") String token) {
        Groupss newGroupss = new Groupss();
        newGroupss.setGroupsName(groupDTO.groupsName);
        newGroupss.setGroupsDescriptions(groupDTO.groupsDescriptions);
        newGroupss.setGroupsMaxMember(groupDTO.groupsMaxMember);
        newGroupss.setGroupsAvgScore(0.00);
        newGroupss.setGroupsLeaderId(groupDTO.groupsLeaderId);
        Classes clazz = classService.getById(groupDTO.classId, token);
        if (clazz == null) {
            return ResponseEntity.badRequest().build();
        }
        newGroupss.setClasses(clazz);
        newGroupss = groupService.save(newGroupss, token);
        for (String userId : groupDTO.groupUserUserIds) {
            GroupStudent groupStudent = new GroupStudent();
            groupStudent.setGroups(newGroupss);
            groupStudent.setUserId(userId);
            groupsStudentService.save(groupStudent, token);
        }

        return ResponseEntity.ok().body(newGroupss);
    }

    @DeleteMapping("/{groupsId}")
    public ResponseEntity<Groupss> deleteGroupss(@RequestHeader("Authorization") String token,
            @PathVariable(name = "groupsId") String groupsId) {
        return ResponseEntity.ok().body(null);
    }

}

class GroupDTO {
    String groupsName;
    String groupsDescriptions;
    Integer groupsMaxMember;
    String groupsLeaderId;
    String classId;
    List<String> groupUserUserIds;
}
