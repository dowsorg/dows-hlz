package org.dows.hep.biz.orgreport.extracters;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dows.hep.api.base.indicator.response.ExperimentPhysicalExamReportResponseRs;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewPhysicalExamReportRsBiz;
import org.dows.hep.biz.orgreport.IOrgReportExtracter;
import org.dows.hep.biz.orgreport.OrgReportExtractRequest;
import org.dows.hep.biz.util.ShareBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/18 11:41
 */
@Component
public class ViewTwoLevelExtracter implements IOrgReportExtracter<List<ExperimentPhysicalExamReportResponseRs>> {

    @Autowired
    private ExperimentIndicatorViewPhysicalExamReportRsBiz experimentIndicatorViewPhysicalExamReportRsBiz;

    private static final TypeReference<List<ExperimentPhysicalExamReportResponseRs>> s_typeRef=new TypeReference<>() {};

    @Override
    public EnumExptOperateType getOperateType() {
        return EnumExptOperateType.VIEWTwoLevel;
    }


    @Override
    public List<ExperimentPhysicalExamReportResponseRs> getReportData(OrgReportExtractRequest req) {
        final Integer period= ShareBiz.getCurrentPeriod(req.getAppId(), req.getExperimentInstanceId());
        return experimentIndicatorViewPhysicalExamReportRsBiz.get(req.getAppId(), req.getExperimentInstanceId(),
                req.getIndicatorFuncId(),req.getExperimentPersonId(),req.getExperimentOrgId(),period);
    }

    @Override
    public void accept(List<ExperimentPhysicalExamReportResponseRs> report, ExptOrgReportNodeDataVO node) {
        node.setViewTwoLevel(report);
    }
}
