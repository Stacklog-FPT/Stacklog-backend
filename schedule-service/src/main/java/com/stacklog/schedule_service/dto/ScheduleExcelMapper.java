package com.stacklog.schedule_service.dto;

import org.apache.poi.ss.usermodel.Row;

import com.stacklog.core_service.utils.excel.ExcelMapper;

public class ScheduleExcelMapper implements ExcelMapper<ScheduleExcelDTO> {

  @Override
  public ScheduleExcelDTO map(Row row) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'map'");
  }

  @Override
  public String[] headers() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'headers'");
  }

  @Override
  public void writeRow(ScheduleExcelDTO item, Row row) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'writeRow'");
  }

  @Override
  public Class<ScheduleExcelDTO> getType() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getType'");
  }
  
}
