package org.dows.hep.biz.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.user.experiment.ExperimentReportBiz;
import org.springframework.stereotype.Component;

/**
 * @author fhb
 * @version 1.0
 * @description 报告记录
 * @date 2023/7/21 11:29
 **/

@Slf4j
@AllArgsConstructor
@Component
public class ReportRecordHelper {
    private final ExperimentReportBiz experimentReportBiz;

    // materials && materialsItem && exptReportInstance
    public boolean saveReportRecord() {

        return Boolean.FALSE;
    }
}
