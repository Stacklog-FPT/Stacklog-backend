package com.stacklog.score_service.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseOverall {
    private String groupId;
    private Double totalTask;
    private Map<String, Double> memberContribution;
    private Double groupAverageScore;

}
