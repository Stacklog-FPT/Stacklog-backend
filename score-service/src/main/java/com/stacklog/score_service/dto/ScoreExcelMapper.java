package com.stacklog.score_service.dto;

import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import com.stacklog.core_service.utils.excel.ExcelMapper;

@Component
public class ScoreExcelMapper implements ExcelMapper<ScoreExcelDTO> {

  @Override
  public ScoreExcelDTO map(Row row) {
    ScoreExcelDTO dto = new ScoreExcelDTO();
    dto.setClassName(row.getCell(0).getStringCellValue());
    dto.setWork_id(row.getCell(1).getStringCellValue());
    dto.setEmail(row.getCell(2).getStringCellValue());
    dto.setMemberCode(row.getCell(3).getStringCellValue());
    dto.setFullname(row.getCell(4).getStringCellValue());
    return dto;
  }

  @Override
  public String[] headers() {
    return new String[] { "Class", "RollNumber", "Email", "MemberCode", "FullName" };
  }

  @Override
  public void writeRow(ScoreExcelDTO item, Row row) {
    row.createCell(0).setCellValue(item.getClassName());
    row.createCell(1).setCellValue(item.getWork_id());
    row.createCell(2).setCellValue(item.getEmail());
    row.createCell(3).setCellValue(item.getMemberCode());
    row.createCell(4).setCellValue(item.getFullname());
    int columnIndex = 5;
    if (item.getListScores() != null) {
      for (Map.Entry<String, Double> entry : item.getListScores().entrySet()) {
        Double score = entry.getValue();
        row.createCell(columnIndex).setCellValue(score != null ? score : 0.0);
        columnIndex++;
      }
    }
  }

  @Override
  public Class<ScoreExcelDTO> getType() {
    return ScoreExcelDTO.class;
  }

  public String[] plusHeaders(Map<String, Double> listScores) {
    return (listScores == null || listScores.isEmpty())
        ? new String[0]
        : listScores.keySet().toArray(new String[0]);
  }

}
