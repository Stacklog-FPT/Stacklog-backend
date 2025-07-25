package com.stacklog.task_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.entities.Review;
import com.stacklog.task_service.model.service.ReviewService;

@RestController
@RequestMapping(value = {"/review", "/review/"})
public class ReviewRestController {
    
    @Autowired
    ReviewService reviewService;

    @GetMapping("")
    public ResponseEntity<List<Review>> getTasksByUserId(@RequestHeader("Authorization") String token) {
        List<Review> lists = reviewService.getAllByUserId(token);
        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<Review>> getTasksByGroupId(@RequestHeader("Authorization") String token, @PathVariable("taskId") String taskId) {
        List<Review> lists = reviewService.getAllByTaskId(token, taskId);
        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }
    
    @PostMapping("")
    public ResponseEntity<Review> saveTask(@RequestHeader("Authorization") String token, @RequestBody Review e) {
        Review review = reviewService.save(e, token);
        if (review == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(review);
    }
    
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteTask(@RequestHeader("Authorization") String token, @PathVariable("reviewId") String reviewId) {
        Review review = reviewService.delete(reviewId, token);
        if (review == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body("Delete success");
    }

}
