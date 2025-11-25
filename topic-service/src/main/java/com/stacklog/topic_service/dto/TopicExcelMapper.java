package com.stacklog.topic_service.dto;

import org.apache.poi.ss.usermodel.Row;

import com.stacklog.core_service.utils.excel.ExcelMapper;

public class TopicExcelMapper implements ExcelMapper<TopicExcelDTO> {

  @Override
  public TopicExcelDTO map(Row row) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'map'");
  }

  @Override
  public String[] headers() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'headers'");
  }

  @Override
  public void writeRow(TopicExcelDTO item, Row row) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'writeRow'");
  }

  @Override
  public Class<TopicExcelDTO> getType() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getType'");
  }
  
}
