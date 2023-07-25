package org.dows.hep.biz.orgreport.extracters;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dows.hep.api.base.indicator.response.ExperimentHealthGuidanceReportResponseRs;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorJudgeHealthGuidanceReportRsBiz;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.orgreport.IOrgReportExtracter;
import org.dows.hep.biz.orgreport.OrgReportExtractRequest;
import org.dows.hep.biz.util.ShareBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/18 11:42
 */
@Component
public class JudegHealthGuidanceExtracter implements IOrgReportExtracter<List<ExperimentHealthGuidanceReportResponseRs>> {

    @Autowired
    private ExperimentIndicatorJudgeHealthGuidanceReportRsBiz experimentIndicatorJudgeHealthGuidanceReportRsBiz;

    private static final TypeReference<List<ExperimentHealthGuidanceReportResponseRs>> s_typeRef=new TypeReference<>() {};

    @Override
    public EnumExptOperateType getOperateType() {
        return EnumExptOperateType.JUDGEHealthGuidance;
    }


    @Override
    public List<ExperimentHealthGuidanceReportResponseRs> getReportData(OrgReportExtractRequest req) {
        final Integer period= ShareBiz.getCurrentPeriod(req.getAppId(), req.getExperimentInstanceId());
        return experimentIndicatorJudgeHealthGuidanceReportRsBiz.get(req.getAppId(), req.getExperimentInstanceId(),
                req.getIndicatorFuncId(),req.getExperimentPersonId(),req.getExperimentOrgId(),period);
    }

    @Override
    public void accept(List<ExperimentHealthGuidanceReportResponseRs> report, ExptOrgReportNodeDataVO node) {
        node.setJudgeHealthGuidance(report);
    }
}
