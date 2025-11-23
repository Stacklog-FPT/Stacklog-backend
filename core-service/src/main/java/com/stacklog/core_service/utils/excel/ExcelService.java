package com.stacklog.core_service.utils.excel;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelService {

    public <T> List<T> importExcel(String filePath, ExcelMapper<T> mapper) throws Exception {
        ExcelHelper<T> helper = new ExcelHelper<>(mapper);
        return helper.importExcel(filePath);
    }

    public <T> void exportExcel(List<T> data, String filePath, ExcelMapper<T> mapper) throws Exception {
        ExcelHelper<T> helper = new ExcelHelper<>(mapper);
        helper.exportExcel(data, filePath);
    }
}
