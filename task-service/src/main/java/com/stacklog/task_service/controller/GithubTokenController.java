package com.stacklog.task_service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.task_service.model.service.GithubTokenService;

@RestController
@RequestMapping("/github")
public class GithubTokenController {

    @Autowired
    private GithubTokenService githubTokenService;

    @PostMapping("/save-token")
    public ResponseEntity<?> saveToken(@RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body) {
        String groupId = body.get("groupId");
        String githubToken = body.get("githubToken");

        githubTokenService.saveToken(groupId, githubToken, token);

        return ResponseEntity.ok("Token saved for group: " + groupId);
    }

    @PostMapping("/setup-repo")
    public ResponseEntity<?> setupRepository(
            @RequestHeader("Authorization") String systemJwtToken,
            @RequestBody Map<String, String> body
    ) {
        String groupId = body.get("groupId");
        String repoName = body.get("repoName");
        String collaborator = body.get("collaborator");
        String callbackUrl = body.get("callbackUrl");
        String secret = body.get("secret");

        // Lấy GitHub PAT từ DB theo group
        String githubToken = githubTokenService.getTokenByGroupId(groupId);

        // 1. Tạo repository
        String createdRepoName = githubTokenService.createRepository(githubToken, repoName);

        // 2. Lấy owner từ token GitHub
        String owner = githubTokenService.getGithubAuthenticatedUser(githubToken);

        // 3. Thêm collaborator
        githubTokenService.addCollaborator(githubToken, owner, createdRepoName, collaborator);

        // 4. Tạo webhook
        githubTokenService.createWebhook(githubToken, owner, createdRepoName, callbackUrl, secret);

        return ResponseEntity.ok("Repository setup completed: " + createdRepoName);
    }

}
