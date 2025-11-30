package com.stacklog.task_service.model.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@FeignClient(name = "class-service", url = "http://scoreservice:2008", path = "")
public interface ScoreServiceClient {
  @GetMapping("/group/{groupId}")
  List<ScoreItem> getScoreItemsByGroupId(
      @RequestHeader("Authorization") String token,
      @PathVariable("groupId") String groupId,
      @RequestParam("category") String categoryName);
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class ScoreItem {
  private String scoreItemId;
  private String scoreItemName;
  private Double scoreItemValue = 0.00;
  private Boolean isVisualize = false;
  private String userId;
  private String groupId;
}