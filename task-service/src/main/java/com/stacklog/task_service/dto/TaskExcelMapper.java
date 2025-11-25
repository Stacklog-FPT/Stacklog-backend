package com.stacklog.task_service.dto;

import org.apache.poi.ss.usermodel.Row;

import com.stacklog.core_service.utils.excel.ExcelMapper;

public class TaskExcelMapper implements ExcelMapper<TaskExcelDTO> {

  @Override
  public TaskExcelDTO map(Row row) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'map'");
  }

  @Override
  public String[] headers() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'headers'");
  }

  @Override
  public void writeRow(TaskExcelDTO item, Row row) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'writeRow'");
  }

  @Override
  public Class<TaskExcelDTO> getType() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getType'");
  }
  
}
