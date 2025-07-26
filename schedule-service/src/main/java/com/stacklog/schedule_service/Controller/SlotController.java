package com.stacklog.schedule_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.schedule_service.model.entities.Slot;
import com.stacklog.schedule_service.model.service.SlotService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping(value = {"", "/"})
public class SlotController {
    
    @Autowired
    SlotService slotService;

    @GetMapping("/user")
    public ResponseEntity<List<Slot>> getTasksByUserId(@RequestHeader("Authorization") String token) {
        List<Slot> lists = slotService.getAllByUserId(token);
        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Slot>> getTasksByGroupId(@RequestHeader("Authorization") String token, @PathVariable("groupId") String groupId) {
        List<Slot> lists = slotService.getAllByGroupId(token, groupId);
        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }
    
    @PostMapping("")
    public ResponseEntity<Slot> saveTask(@RequestHeader("Authorization") String token, @RequestBody Slot e) {
        Slot slot = slotService.save(e, token);
        if (slot == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(slot);
    }
    
    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@RequestHeader("Authorization") String token, @PathVariable("slotId") String slotId) {
        Slot slot = slotService.delete(slotId, token);
        if (slot == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body("Delete success");
    }

}
