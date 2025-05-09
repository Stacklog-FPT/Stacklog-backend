package com.stacklog.class_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.class_service.model.entities.Groupss;
import com.stacklog.class_service.model.service.GroupService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping(path = "/group")
public class GroupRestController {
    
    @Autowired GroupService groupService;

    @GetMapping("")
    public ResponseEntity<List<Groupss>> getGroupByUserId(@RequestHeader("Authorization") String token) {
        List<Groupss> groupsses = groupService.getAllByUserId(token);
        return ResponseEntity.ok().body(groupsses);
    }
    
    @PostMapping("")
    public ResponseEntity<Groupss> saveGroupss(@RequestBody Groupss groupss, @RequestHeader("Authorization") String token) {
        Groupss newGroupss = groupService.save(groupss, token);
        
        return ResponseEntity.ok().body(newGroupss);
    }

    @DeleteMapping("/{groupsId}")
    public ResponseEntity<Groupss> deleteGroupss(@RequestHeader("Authorization") String token, @PathVariable(name = "groupsId") String groupsId) {
        return ResponseEntity.ok().body(null);
    }
    

}
