package com.stacklog.score_service.model.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.stacklog.score_service.dto.ResponseOverall;

@FeignClient(name = "task-service", url = "http://taskservice:2002", path = "")
public interface TaskServiceClient {
    @GetMapping("/task/overall?groupId={groupId}")
    ResponseOverall getOverallTask(@RequestHeader("Authorization") String token,
            @RequestParam(name = "groupId") String groupId);

}


