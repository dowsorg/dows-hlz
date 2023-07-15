package org.dows.hep.biz.report.pdf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.vo.report.ExptReportVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author fhb
 * @version 1.0
 * @description 实验 `沙盘报告` biz
 * @date 2023/7/7 10:21
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ExptSandReportBiz implements ExptReportBiz{
    @Override
    public ExptReportVO generatePdfReport(String experimentInstanceId, String exptGroupId) {
        return ExptReportVO.builder()
                .groupReportList(new ArrayList<>())
                .build();
    }
}
