package org.dows.hep.biz.orgreport.extracters;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dows.hep.api.base.indicator.response.ExperimentHealthProblemReportResponseRs;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorJudgeHealthProblemReportRsBiz;
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
public class JudegHealthProblemExtracter implements IOrgReportExtracter<List<ExperimentHealthProblemReportResponseRs>> {

    @Autowired
    private ExperimentIndicatorJudgeHealthProblemReportRsBiz experimentIndicatorJudgeHealthProblemReportRsBiz;
    private static final TypeReference<List<ExperimentHealthProblemReportResponseRs>> s_typeRef=new TypeReference<>() {};

    @Override
    public EnumExptOperateType getOperateType() {
        return EnumExptOperateType.JUDGEHealthProblem;
    }


    @Override
    public List<ExperimentHealthProblemReportResponseRs> getReportData(OrgReportExtractRequest req) {
        return experimentIndicatorJudgeHealthProblemReportRsBiz.get(req.getAppId(), req.getExperimentInstanceId(),
                req.getIndicatorFuncId(),req.getExperimentPersonId(),req.getExperimentOrgId());
    }

    @Override
    public void accept(List<ExperimentHealthProblemReportResponseRs> report, ExptOrgReportNodeDataVO node) {
        node.setJudgeHealthProblem(report);
    }
}
