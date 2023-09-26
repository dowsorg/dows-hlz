package org.dows.hep.biz.tenant.excel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.doc.api.entity.excel.ExcelSelector;
import org.dows.framework.doc.api.entity.excel.Point;
import org.dows.framework.doc.api.entity.excel.SheetRange;
import org.dows.hep.biz.util.AssertUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
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
     */
    public File parseImportExcelStream(MultipartFile multipartFile) {
        //获取临时文件
        File file = multipartFileToFile(multipartFile);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fileInputStream = null;
        Workbook book = null;
        try {
            fileInputStream = new FileInputStream(file);
            boolean fg = file.delete();
            log.info(file.getName() + "临时文件删除：" + fg);
            book = WorkbookFactory.create(fileInputStream);
            fileInputStream.close();

        } catch (IOException e) {
            throw new BizException("不是原始的Excel文件,请下载模版更新后导入:");
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
                parseRow(j, row, resultRow);
            }
        }
        File tempFile = new File("importTemp.xlsx");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            resultBook.write(fileOutputStream);
            fileOutputStream.close();
            book.close();
            resultBook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tempFile;
    }

    private void parseRow(int rowNum, Row row, Row resultRow) {
        if (row != null) {
            int lastCellNum = row.getLastCellNum();
            for (int k = 0; k <= lastCellNum; k++) {
                Cell cell = row.getCell(k);
                Cell resultCell = resultRow.createCell(k);
                if (cell != null) {
                    String value = null;
                    CellType cellType = cell.getCellType();
                    if (CellType.NUMERIC.equals(cellType)) {
                        value = String.valueOf((int) cell.getNumericCellValue());
                    } else {
                        value = cell.getStringCellValue();
                    }
                    checkValue(rowNum + 1, k + 1, value);
                    resultCell.setCellValue(value);
                }
            }
        }
    }

    private final static String TABLE_HEADER_ACCOUNT = "用户账号(code)";
    private final static String TABLE_HEADER_NAME = "用户姓名";

    /**
     * 导入学生文件校验
     * 1为始
     */
    private void checkValue(int rowNum, int cellNum, String value) {
        if (cellNum > 2) {
            throw new BizException("有超出取值范围的列" + cellNum);
        }
        if (rowNum == 1) {
            if (cellNum == 1 && !TABLE_HEADER_ACCOUNT.equals(value)) {
                throw new BizException("表头账号列不对");
            }
            if (cellNum == 2 && !TABLE_HEADER_NAME.equals(value)) {
                throw new BizException("表头姓名列不对");
            }
        } else {
            if (cellNum == 1 && !containsChineseCharacters(value)) {
                throw new BizException("账号只能有数字和字母");
            }
        }
    }

    /**
     * 中文正则
     * true
     */
    private boolean containsChineseCharacters(String str) {
        if (str == null) {
            return false;
        }
        //不能是中文
//        String regex = "[\u4e00-\u9fa5]+";
//        str.matches(".*" + regex + ".*");
        //只能是数字和字母
        return Pattern.matches("^[a-zA-Z0-9]+$", str);

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
