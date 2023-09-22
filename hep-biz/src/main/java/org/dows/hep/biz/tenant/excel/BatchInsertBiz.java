package org.dows.hep.biz.tenant.excel;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.doc.api.entity.excel.ExcelSelector;
import org.dows.framework.doc.api.entity.excel.Point;
import org.dows.framework.doc.api.entity.excel.SheetRange;
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

    public <T> List<T> batchInsert(InputStream fin, Integer endCol, Integer endRow, Class<T> clazz,String primaryKey) {
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
    public InputStream parseImportExcelStream(File file) throws IOException {
        return docBiz.parseImportExcelStream(file);
    }
    public File multipartFileToFile(MultipartFile multipartFile){
        //文件上传前的名称
        String fileName = multipartFile.getOriginalFilename();
        File file = new File(fileName);
        OutputStream out = null;
        try{
            //获取文件流，以文件流的方式输出到新文件
            out = new FileOutputStream(file);
            byte[] ss = multipartFile.getBytes();
            for(int i = 0; i < ss.length; i++){
                out.write(ss[i]);
            }
        }catch(IOException e){
            throw new BizException("MultipartFile转File文件异常");
        }finally {
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    throw new BizException("文件输出流关闭异常");
                }
            }
        }
        return file;
    }
}
