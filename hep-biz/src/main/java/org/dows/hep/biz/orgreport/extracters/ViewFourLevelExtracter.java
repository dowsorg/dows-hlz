package org.dows.hep.biz.orgreport.extracters;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dows.hep.api.base.indicator.response.ExperimentSupportExamReportResponseRs;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewSupportExamReportRsBiz;
import org.dows.hep.biz.orgreport.IOrgReportExtracter;
import org.dows.hep.biz.orgreport.OrgReportExtractRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/18 11:41
 */
@Component
public class ViewFourLevelExtracter implements IOrgReportExtracter<List<ExperimentSupportExamReportResponseRs>> {

    @Autowired
    private ExperimentIndicatorViewSupportExamReportRsBiz experimentIndicatorViewSupportExamReportRsBiz;

    private static final TypeReference<List<ExperimentSupportExamReportResponseRs>> s_typeRef=new TypeReference<>() {};

    @Override
    public EnumExptOperateType getOperateType() {
        return EnumExptOperateType.VIEWFourLevel;
    }


    @Override
    public List<ExperimentSupportExamReportResponseRs> getReportData(OrgReportExtractRequest req) {
        return experimentIndicatorViewSupportExamReportRsBiz.get(req.getAppId(), req.getExperimentInstanceId(),
                req.getIndicatorFuncId(), req.getExperimentPersonId(), req.getExperimentOrgId());
    }

    @Override
    public void accept(List<ExperimentSupportExamReportResponseRs> report, ExptOrgReportNodeDataVO node) {
        node.setViewFourLevel(report);
    }
}
