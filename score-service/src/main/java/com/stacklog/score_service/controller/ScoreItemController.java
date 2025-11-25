package com.stacklog.score_service.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.score_service.model.entities.ScoreItem;
import com.stacklog.score_service.model.service.ScoreItemService;

import io.jsonwebtoken.lang.Classes;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/")
public class ScoreItemController {

    @Autowired
    private ScoreItemService scoreItemService;

    @GetMapping("/class/{groupId}")
    public ResponseEntity<List<ScoreItem>> getScoreItem(@RequestHeader("Authorization") String token,
            @PathVariable("groupId") String groupId) {
        List<ScoreItem> list = scoreItemService.getAllByGroupId(token, groupId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/save")
    public ResponseEntity<ScoreItem> saveScoreItem(@RequestHeader("Authorization") String token,
            @RequestBody ScoreItem e) {
        ScoreItem newScoreItem = scoreItemService.save(e, token);
        if (newScoreItem == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(newScoreItem);
    }

    @DeleteMapping("/delete/{stId}")
    public ResponseEntity<ScoreItem> deleteScoreItem(@RequestHeader("Authorization") String token,
            @PathVariable("stId") String stId) {
        
        ScoreItem oldScoreItem = scoreItemService.delete(stId, token);
        if (oldScoreItem == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();

    }

    @GetMapping("/export-by-class")
    public ResponseEntity<?> exportByClassesId(@RequestHeader("Authorization") String token,
            @RequestParam("classId") String classId) {
        try {
            File temp = File.createTempFile("classes-export-", ".xlsx");

            scoreItemService.exportByClassId(classId, temp.getAbsolutePath(), token);

            byte[] bytes = Files.readAllBytes(temp.toPath());

            temp.delete();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"classes-" + classId
                                    + ".xlsx\"; filename*=UTF-8''classes.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(bytes);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Export error: " + e.getMessage());

        }
    }
    

}
