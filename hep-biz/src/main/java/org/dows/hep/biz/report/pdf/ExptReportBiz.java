package org.dows.hep.biz.report.pdf;

import org.dows.hep.api.report.pdf.ExptReportVO;

/**
 * @author fhb
 * @version 1.0
 * @description TODO
 * @date 2023/7/7 13:49
 **/
public interface ExptReportBiz {
    ExptReportVO generatePdfReport(String experimentInstanceId, String exptGroupId) ;
}
