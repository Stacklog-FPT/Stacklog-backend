package com.stacklog.core_service.utils.excel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExcelHelper<T> {

  private final ExcelMapper<T> mapper;

  public List<T> importExcel(String filePath) throws Exception {
    List<T> result = new ArrayList<>();

    try (FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = WorkbookFactory.create(fis)) {

      Sheet sheet = workbook.getSheetAt(0);

      for (Row row : sheet) {
        if (row.getRowNum() == 0)
          continue; // skip header
        result.add(mapper.map(row));
      }
    }
    return result;
  }

  public void exportExcel(List<T> data, String filePath) throws Exception {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Sheet1");

      // Header
      Row header = sheet.createRow(0);
      String[] headers = mapper.headers();
      for (int i = 0; i < headers.length; i++)
        header.createCell(i).setCellValue(headers[i]);

      // Body
      int rowIdx = 1;
      for (T item : data) {
        Row row = sheet.createRow(rowIdx++);
        mapper.writeRow(item, row);
      }

      try (FileOutputStream fos = new FileOutputStream(filePath)) {
        workbook.write(fos);
      }
    }
  }
}
