package org.dows.hep.biz.orgreport.extracters;

import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.response.ExptTreatPlanResponse;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.biz.orgreport.IOrgReportExtracter;
import org.dows.hep.biz.orgreport.OrgReportExtractRequest;
import org.dows.hep.biz.user.experiment.ExperimentOrgInterveneBiz;
import org.dows.hep.biz.util.CopyWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/7/18 11:42
 */
@Component
public class TreatTwoLevelExtracter implements IOrgReportExtracter<ExptTreatPlanResponse> {


    @Autowired
    private ExperimentOrgInterveneBiz experimentOrgInterveneBiz;



    @Override
    public EnumExptOperateType getOperateType() {
        return EnumExptOperateType.INTERVENETreatTwoLevel;
    }

    @Override
    public ExptTreatPlanResponse getReportData(OrgReportExtractRequest req) {
        ExptOperateOrgFuncRequest castReq= CopyWrapper.create(ExptOperateOrgFuncRequest::new)
                .endFrom(req);
        return experimentOrgInterveneBiz.getExptTreatPlan(castReq);
    }

    @Override
    public void accept(ExptTreatPlanResponse report, ExptOrgReportNodeDataVO node) {
        node.setTreatTwoLevel(report);
    }
}
