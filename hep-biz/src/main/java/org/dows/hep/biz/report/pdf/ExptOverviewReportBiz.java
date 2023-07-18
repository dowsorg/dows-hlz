package org.dows.hep.biz.report.pdf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.vo.report.ExptReportModel;
import org.dows.hep.vo.report.ExptReportVO;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

/**
 * @author fhb
 * @version 1.0
 * @description 实验 `报告总分` biz
 * @date 2023/7/7 10:20
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ExptOverviewReportBiz implements ExptReportBiz {

    @Override
    public ExptReportVO generatePdfReport(String experimentInstanceId, String exptGroupId) {
        return ExptReportVO.builder()
                .groupReportList(new ArrayList<>())
                .build();
    }

    @Override
    public ExptReportData prepareData(String exptInstanceId, String exptGroupId) {
        return null;
    }

    @Override
    public ExptReportModel getExptReportModel(String exptGroupId, ExptReportData exptReportData) {
        return null;
    }

    @Override
    public File getTempFile(String exptGroupId, ExptReportData exptReportData) {
        return null;
    }

    @Override
    public String getSchemeFlt() {
        return null;
    }


}
