package org.dows.hep.biz.tenant.excel;

import lombok.RequiredArgsConstructor;
import org.dows.framework.doc.api.DocumentHandler;
import org.dows.framework.doc.api.entity.DocumentTypeEnum;
import org.dows.framework.doc.api.entity.excel.ExcelSelector;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

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
