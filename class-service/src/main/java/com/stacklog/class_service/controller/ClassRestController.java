package com.stacklog.class_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.class_service.model.entities.Classes;
import com.stacklog.class_service.model.service.ClassService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(path = "/class")
public class ClassRestController {
    
    @Autowired ClassService classService;

    @GetMapping("")
    public ResponseEntity<List<Classes>> getClassesByUserId(@RequestHeader("Authorization") String token) {
        List<Classes> classes = classService.getAllByUserId(token);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/lecture")
    public ResponseEntity<List<Classes>> getClassesByLectureId(@RequestHeader("Authorization") String token) {
        List<Classes> classes = classService.getAllByLecture(token);
        return ResponseEntity.ok(classes);
    }
    
    
    @PostMapping(path = "")
    public ResponseEntity<Classes> saveClasses(@RequestBody Classes classes, @RequestHeader("Authorization") String token) {
        Classes newClasses = classService.save(classes, token);
        return ResponseEntity.ok(newClasses);
    }

    @DeleteMapping(path = "/{classesId}")
    public ResponseEntity<Classes> deleteClasses(@RequestHeader("Authorization") String token, @PathVariable(name = "classesId") String classesId) {
        classService.delete(classesId, token);
        return ResponseEntity.ok(null);
    }
    

}
