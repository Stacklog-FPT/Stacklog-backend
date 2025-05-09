package com.stacklog.class_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.class_service.model.entities.Classes;
import com.stacklog.class_service.model.service.ClassService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping(path = "/class")
public class ClassRestController {
    
    @Autowired ClassService classService;

    @GetMapping("")
    public ResponseEntity<Classes> getClassesByUserId(@RequestHeader("Authorization") String token) {
        List<Classes> classes = classService.getAllByUserId(token);
        return new String();
    }
    

}
