package com.stacklog.task_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.task_service.model.entities.GithubToken;
import com.stacklog.task_service.model.repo.GithubTokenRepo;

import jakarta.transaction.Transactional;

@Service
public class GithubTokenService {

    @Autowired
    private GithubFeignClient githubFeignClient;

    @Autowired
    private GithubTokenRepo githubTokenRepo;

    private final RedisService<GithubToken> redisService;

    public GithubTokenService(RedisService<GithubToken> redisService) {
        this.redisService = redisService;
    }

    // ---------------- GitHub API ---------------- //

    public String createRepository(String token, String repoName) {
        JsonNode response = githubFeignClient.createRepository(
                "Bearer " + token,
                Map.of(
                        "name", repoName,
                        "private", true));

        return response.get("name").asText();
    }

    public void addCollaborator(String token, String owner, String repo, String collaborator) {
        githubFeignClient.addCollaborator(
                "Bearer " + token,
                owner,
                repo,
                collaborator);
    }

    public void createWebhook(String token, String owner, String repo, String callbackUrl, String secret) {

        Map<String, Object> body = Map.of(
                "name", "web",
                "active", true,
                "events", List.of("pull_request"),
                "config", Map.of(
                        "url", callbackUrl,
                        "content_type", "json",
                        "secret", secret));

        githubFeignClient.createWebhook(
                "Bearer " + token,
                owner,
                repo,
                body);
    }

    public String getGithubAuthenticatedUser(String githubToken) {
        JsonNode response = githubFeignClient.getCurrentUser("Bearer " + githubToken);
        return response.get("login").asText();
    }

    // ---------------- Token Save Logic ---------------- //

    @Transactional
    public GithubToken saveToken(String groupId, String githubToken, String jwtToken) {

        GithubToken entity = githubTokenRepo.findByGroupId(groupId)
                .orElseGet(() -> {
                    GithubToken newGt = new GithubToken();
                    newGt.setId(UUID.randomUUID().toString());
                    newGt.setCreatedAt(CommonFunction.getCurrentTime());
                    newGt.setCreatedBy(redisService.getCurrentUserId(jwtToken));
                    return newGt;
                });

        LocalDateTime now = CommonFunction.getCurrentTime();

        entity.setGroupId(groupId);
        entity.setAccessToken(githubToken);
        entity.setUpdateAt(now);
        entity.setUpdateBy(redisService.getCurrentUserId(jwtToken));

        return githubTokenRepo.save(entity);
    }

    public String getTokenByGroupId(String groupId) {
        return githubTokenRepo.findByGroupId(groupId).orElseThrow().getAccessToken();
    }

}
