package com.stacklog.core_service.utils.excel;

import org.apache.poi.ss.usermodel.Row;

public interface ExcelMapper<T> {
  T map(Row row);

  String[] headers();

  void writeRow(T item, Row row);

  Class<T> getType();
}
