package com.stacklog.score_service.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreExcelDTO {
  private String className;
  private String work_id;
  private String email;
  private String memberCode;
  private String fullname;
  private Map<String, Double> listScores;
}
