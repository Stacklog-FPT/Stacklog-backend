package com.stacklog.task_service.model.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "github-api", url = "${github.api.url:https://api.github.com}")
public interface GithubFeignClient {

    @PostMapping("/user/repos")
    JsonNode createRepository(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody Map<String, Object> body);

    @PutMapping("/repos/{owner}/{repo}/collaborators/{collaborator}")
    JsonNode addCollaborator(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String collaborator);

    @PostMapping("/repos/{owner}/{repo}/hooks")
    JsonNode createWebhook(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestBody Map<String, Object> body);

    @GetMapping("/user")
    JsonNode getCurrentUser(@RequestHeader("Authorization") String bearerToken);
}
