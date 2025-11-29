package com.stacklog.score_service.model.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@FeignClient(name = "task-service", url = "http://taskservice:2002", path = "")
public interface TaskServiceClient {

    @GetMapping("/task/overall?groupId={groupId}")
    ResponseOverall getOverallTask(@RequestHeader("Authorization") String token,
            @RequestParam(name = "groupId") String groupId);

}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class ResponseOverall {
    private String groupId;
    private Double totalTask;
    private Map<String, Double> memberContribution;
    private Double groupAverageScore;

}
