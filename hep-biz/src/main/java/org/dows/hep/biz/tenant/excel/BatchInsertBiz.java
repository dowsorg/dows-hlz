package org.dows.hep.biz.tenant.excel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.doc.api.entity.excel.ExcelSelector;
import org.dows.framework.doc.api.entity.excel.Point;
import org.dows.framework.doc.api.entity.excel.SheetRange;
import org.dows.hep.biz.util.AssertUtil;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchInsertBiz {

    private final DocBiz docBiz;

    public <T> List<T> batchInsert(InputStream fin, Integer endCol, Integer endRow, Class<T> clazz, String primaryKey) {
        // endP(x, -1)
        Point beginP = new Point(0, 1);
        Point endP = new Point(endCol == null ? 1 : endCol, endRow == null ? -1 : endRow);
        // 设置 index 为 0 的 sheet
        SheetRange firstSheet = new SheetRange();
        firstSheet.setSheetIndex(0);
        firstSheet.setBeginPoint(beginP);
        firstSheet.setEndPoint(endP);
        List<SheetRange> sheets = new ArrayList<>();
        sheets.add(firstSheet);
        // 设置表定位符
        ExcelSelector excelSelector = new ExcelSelector();
        excelSelector.setSheetRanges(sheets);

        Object impResult = null;
        try {
            impResult = docBiz.importExcel(fin, excelSelector, clazz);
        } catch (NotOfficeXmlFileException ex) {
            throw new BizException("请上传真实的EXCEL文件");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 获取所有表
        List sheetList = null;
        if (impResult instanceof ArrayList) {
            sheetList = (ArrayList) impResult;
        }

        // 获取 index = 0 的表的所有行
        List<T> rowList = null;
        if (Objects.nonNull(sheetList) && !sheetList.isEmpty()) {
            Object o = sheetList.get(0);
            rowList = (List<T>) o;
        }

        // 过滤不为空的数据
        if (rowList != null && !rowList.isEmpty()) {
            rowList = rowList.stream()
                    .filter(v -> {
                        if (Objects.isNull(v)) {
                            return false;
                        }
                        final String s = JSONUtil.toJsonStr(v);
                        final JSONObject jsonObject = JSONUtil.parseObj(s);
                        if (Objects.isNull(jsonObject)) {
                            return false;
                        }

                        final String first = jsonObject.getStr(primaryKey);
                        if (StrUtil.isBlank(first)) {
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }
        return rowList;
    }

    /**
     * 解析导入文件
     * 兼容 .xls 和 .xlsx
     *
     * @return
     * @throws IOException
     */
    public File parseImportExcelStream(MultipartFile multipartFile) throws IOException {
        //获取临时文件
        File file = multipartFileToFile(multipartFile);
        if (!file.exists()) {
            return null;
        }
        Workbook book = null;
        try {// 2007
            book = new XSSFWorkbook(file);
        } catch (NotOfficeXmlFileException ex) {
            FileInputStream fileInputStream = new FileInputStream(file);
            book = new HSSFWorkbook(fileInputStream);
            fileInputStream.close();
        } catch (NotOLE2FileException ignored) {
            throw new BizException("不是Excel文件");
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
        if (BeanUtil.isEmpty(book)) {
            return null;
        }
        Workbook resultBook = new XSSFWorkbook();
        int sheetNum = book.getNumberOfSheets();
        if (sheetNum == 0) {
            return null;
        }
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = book.getSheetAt(i);
            Sheet resultSheet = resultBook.createSheet();
            int lastRowNum = sheet.getLastRowNum();
            for (int j = 0; j <= lastRowNum; j++) {
                Row row = sheet.getRow(j);
                Row resultRow = resultSheet.createRow(j);
                parseRow(row, resultRow);
            }
        }
        File tempFile = new File("importTemp.xlsx");
        //是否存在
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        resultBook.write(fileOutputStream);
        fileOutputStream.close();
        resultBook.close();
        //删除临时文件
        file.delete();
        return tempFile;
    }

    private void parseRow(Row row, Row resultRow) {
        if (row != null) {
            int lastCellNum = row.getLastCellNum();
            for (int k = 0; k <= lastCellNum; k++) {
                Cell cell = row.getCell(k);
                Cell resultCell = resultRow.createCell(k);
                if (cell != null) {
                    String value = cell.getStringCellValue();
                    resultCell.setCellValue(value);
                }
            }
        }
    }


    /**
     * 获取上传文件
     *
     * @param file
     * @return
     */
    private MultipartFile getMultipartFile(File file) {
        FileInputStream fileInputStream = null;
        MultipartFile multipartFile = null;
        try {
            fileInputStream = new FileInputStream(file);
            multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
            fileInputStream.close();
        } catch (Exception e) {
            throw new BizException("File 转 MultipartFile文件异常");
        }

        return multipartFile;
    }

    public File multipartFileToFile(MultipartFile multipartFile) {
        //文件上传前的名称
        String fileName = multipartFile.getOriginalFilename();
        AssertUtil.trueThenThrow(fileName == null).throwMessage("导入文件不存在");
        File file = new File(multipartFile.getOriginalFilename());
        try {
            InputStream ins = multipartFile.getInputStream();
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            throw new BizException("读取文件错误");
        }
        return file;
    }
}
