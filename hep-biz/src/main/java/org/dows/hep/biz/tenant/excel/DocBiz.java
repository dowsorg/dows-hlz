package org.dows.hep.biz.tenant.excel;

import cn.hutool.core.bean.BeanUtil;
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
import org.dows.framework.doc.api.DocumentHandler;
import org.dows.framework.doc.api.entity.DocumentTypeEnum;
import org.dows.framework.doc.api.entity.excel.ExcelSelector;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RequiredArgsConstructor
@Component
public class DocBiz {

    private final DocumentHandler documentHandler;

    /*
    *   // selector

        List<SheetRange> sheetRanges = new ArrayList<>();
        SheetRange sheetRange1 = new SheetRange();
        // index 为0的表
        sheetRange1.setSheetIndex(0);
        // 第三行到第六行，第一列到第十一列
        sheetRange1.setBeginPoint(new Point(1, 3));
        sheetRange1.setEndPoint(new Point(11, 6))a;
        sheetRanges.add(sheetRange1);

        ExcelSelector selector = new ExcelSelector();
        selector.setSheetRanges(sheetRanges);
    * */
    public Object importExcel(InputStream fin, ExcelSelector selector, Class entityClass) throws IOException {
        return documentHandler.read(fin, DocumentTypeEnum.EXCEL, selector, entityClass);
    }

    /**
     * 解析导入文件
     * 兼容 .xls 和 .xlsx
     *
     * @return
     * @throws IOException
     */
    public InputStream parseImportExcelStream(File file) throws IOException {
        if (!file.canExecute()) {
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
        resultBook.close();
        fileOutputStream.close();
        MultipartFile multipartFile = getMultipartFile(tempFile);
        tempFile.delete();
        return multipartFile.getInputStream();
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
    public MultipartFile getMultipartFile(File file) {
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

    /*
    *   // Locator

        List<SheetRange> sheetRanges = new ArrayList<>();
        // 起始写位置
        SheetRange sheetRange = new SheetRange();
        sheetRange.setSheetIndex(0);
        sheetRange.setBeginPoint(new Point(1, 5));
        sheetRanges.add(sheetRange);

        ExcelLocator excelLocator = new ExcelLocator();
        excelLocator.setSheetRanges(sheetRanges);
    * */
    // todo 调试
//    public void exportExcel(InputStream templateIn, OutputStream fout, List entity, ExcelLocator locator) throws IOException {
//        documentHandler.write(templateIn, fout, DocumentTypeEnum.EXCEL, entity, locator);
//    }
}
