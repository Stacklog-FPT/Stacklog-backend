package com.stacklog.task_service.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.task_service.model.entities.GithubToken;
// import com.stacklog.task_service.model.entities.StatusTask;
// import com.stacklog.task_service.model.entities.Task;
// import com.stacklog.task_service.model.entities.Task.Priority;
import com.stacklog.task_service.model.repo.GithubTokenRepo;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;

@Service
public class GithubTokenService {

    @Autowired
    private GithubFeignClient githubFeignClient;

    // @Autowired
    // private TaskService taskService;

    // @Autowired
    // private StatusTaskService statusTaskService;

    @Autowired
    private KafkaProducer<GitMsg> kafkaProducer;

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

    public void createWebhook(String token, String owner, String repo, String callbackUrl, String secret,
            String groupId) {

        Map<String, Object> body = Map.of(
                "name", "web",
                "active", true,
                "events", List.of("pull_request"),
                "config", Map.of(
                        "url", callbackUrl,
                        "content_type", "json",
                        "secret", secret));

        JsonNode response = githubFeignClient.createWebhook(
                "Bearer " + token,
                owner,
                repo,
                body);

        if (response == null || !response.has("id")) {
            throw new RuntimeException("Failed to create webhook for repository: " + repo);
        }

        GithubToken githubToken = githubTokenRepo.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("No GitHub token found for groupId: " + groupId));

        if (!repo.equals(githubToken.getGithubRepo())) {
            githubToken.setOwner(owner);
            githubToken.setGithubRepo(repo);
            githubTokenRepo.save(githubToken);
        }
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

    public void handlePullRequestWebhook(JsonNode payload) {
        String action = payload.get("action").asText();
        JsonNode pr = payload.get("pull_request");
        String title = pr.get("title").asText();
        boolean merged = pr.get("merged").asBoolean();
        String prUrl = pr.get("html_url").asText();

        // Thông tin về repository
        JsonNode repo = payload.get("repository");
        String owner = repo.get("owner").get("login").asText();
        String repoName = repo.get("name").asText();

        // Thông tin về người tạo PR
        JsonNode user = pr.get("user");
        String prCreator = user.get("login").asText();

        // send notification
        GithubToken githubToken = getGithubTokenByReponameAndOwner(owner, repoName);
        GitMsg gitMsg = new GitMsg();
        gitMsg.setGroupId(githubToken.getGroupId());
        String content;
        if (action.equals("opened") || action.equals("reopened")) {
            content = "A new pull request has been created: " + title + ". PR URL: " + prUrl + ". by: " + prCreator;
        } else if (merged) {
            content = "The pull request has been merged: " + title + ". PR URL: " + prUrl + ". by: " + prCreator;
        } else {
            content = "A pull request action occurred: " + action + " for PR: " + title + ". PR URL: " + prUrl
                    + ". by: " + prCreator;
        }

        gitMsg.setContent(content);
        kafkaProducer.sendMessage(gitMsg, "task-service.github");

    }

    // private void updateOrCreateTask(String title, String repoName, String owner,
    // boolean merged) {

    // GithubToken githubToken = getGithubTokenByReponameAndOwner(owner, repoName);
    // Task existingTaskOpt = taskService.findByTaskTitleAndGroupId(title,
    // githubToken.getGroupId())
    // .orElse(new Task());
    // LocalDateTime now = CommonFunction.getCurrentTime();
    // StatusTask statusTask = null;
    // if (!merged) {
    // statusTask =
    // statusTaskService.getByGroupIdNStatusTaskName(githubToken.getGroupId(),
    // "review");
    // } else {
    // statusTask =
    // statusTaskService.getByGroupIdNStatusTaskName(githubToken.getGroupId(),
    // "completed");
    // }
    // if (existingTaskOpt.getTaskTitle() == null) {
    // existingTaskOpt.setTaskTitle(title);
    // existingTaskOpt.setTaskDescription("Create task by pr at git repository");
    // existingTaskOpt.setTaskStartTime(now);
    // existingTaskOpt.setTaskDueDate(now);
    // existingTaskOpt.setPriority(Priority.LOW);
    // existingTaskOpt.setCreatedAt(now);
    // existingTaskOpt.setUpdateAt(now);
    // existingTaskOpt.setGroupId(githubToken.getGroupId());
    // existingTaskOpt.setTaskId(UUID.randomUUID().toString());
    // } else {
    // existingTaskOpt.setUpdateAt(now);
    // }

    // existingTaskOpt.setStatusTask(statusTask);
    // taskService.save(existingTaskOpt);
    // }

    private GithubToken getGithubTokenByReponameAndOwner(String owner, String repoName) {
        return githubTokenRepo.findByOwnerAndGithubRepo(owner, repoName).orElseThrow();
    }

}

@Getter
@Setter
class GitMsg {
    private String groupId;
    private String content;
}
