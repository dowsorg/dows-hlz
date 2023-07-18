package org.dows.hep.biz.orgreport.extracters;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dows.hep.api.base.indicator.response.ExperimentRiskFactorReportResponseRs;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorJudgeRiskFactorReportRsBiz;
import org.dows.hep.biz.orgreport.IOrgReportExtracter;
import org.dows.hep.biz.orgreport.OrgReportExtractRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/18 11:42
 */
@Component
public class JudegRiskFactorExtracter implements IOrgReportExtracter<List<ExperimentRiskFactorReportResponseRs>> {

    @Autowired
    private ExperimentIndicatorJudgeRiskFactorReportRsBiz experimentIndicatorJudgeRiskFactorReportRsBiz;

    private static final TypeReference<List<ExperimentRiskFactorReportResponseRs>> s_typeRef=new TypeReference<>() {};
    @Override
    public EnumExptOperateType getOperateType() {
        return EnumExptOperateType.JUDGERiskFactor;
    }

    @Override
    public TypeReference<List<ExperimentRiskFactorReportResponseRs>> getReportClass() {
        return s_typeRef;
    }

    @Override
    public List<ExperimentRiskFactorReportResponseRs> getReportData(OrgReportExtractRequest req) {
        return experimentIndicatorJudgeRiskFactorReportRsBiz.get(req.getAppId(), req.getExperimentInstanceId(),
                req.getIndicatorFuncId(), req.getExperimentPersonId(), req.getExperimentOrgId());
    }

    @Override
    public void accept(List<ExperimentRiskFactorReportResponseRs> report, ExptOrgReportNodeDataVO node) {
        node.setJudgeRiskFactor(report);
    }
}
