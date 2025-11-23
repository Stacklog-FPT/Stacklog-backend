package com.stacklog.class_service.dto;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import com.stacklog.core_service.utils.excel.ExcelMapper;

@Component
public class ClassesExcelMapper implements ExcelMapper<ClassesExcelDTO> {

  @Override
  public ClassesExcelDTO map(Row row) {
    ClassesExcelDTO dto = new ClassesExcelDTO();
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
  public void writeRow(ClassesExcelDTO item, Row row) {
    row.createCell(0).setCellValue(item.getClassName());
    row.createCell(1).setCellValue(item.getWork_id());
    row.createCell(2).setCellValue(item.getEmail());
    row.createCell(3).setCellValue(item.getMemberCode());
    row.createCell(4).setCellValue(item.getFullname());
  }

  @Override
  public Class<ClassesExcelDTO> getType() {
    return ClassesExcelDTO.class;
  }

}
