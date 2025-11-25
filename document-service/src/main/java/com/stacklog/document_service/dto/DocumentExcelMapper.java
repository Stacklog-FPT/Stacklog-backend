package com.stacklog.document_service.dto;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import com.stacklog.core_service.utils.excel.ExcelMapper;

@Component
public class DocumentExcelMapper implements ExcelMapper<DocumentExcelDTO> {

  @Override
  public DocumentExcelDTO map(Row row) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'map'");
  }

  @Override
  public String[] headers() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'headers'");
  }

  @Override
  public void writeRow(DocumentExcelDTO item, Row row) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'writeRow'");
  }

  @Override
  public Class<DocumentExcelDTO> getType() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getType'");
  }
  
}
