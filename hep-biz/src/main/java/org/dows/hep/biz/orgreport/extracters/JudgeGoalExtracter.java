package org.dows.hep.biz.orgreport.extracters;

import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.response.ExptJudgeGoalResponse;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.biz.orgreport.IOrgReportExtracter;
import org.dows.hep.biz.orgreport.OrgReportExtractRequest;
import org.dows.hep.biz.user.experiment.ExperimentJudgeBiz;
import org.dows.hep.biz.util.CopyWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * @author : wuzl
 * @date : 2023/10/20 9:47
 */
@Component
public class JudgeGoalExtracter implements IOrgReportExtracter<ExptJudgeGoalResponse> {

    @Autowired
    private ExperimentJudgeBiz experimentJudgeBiz;

    @Override
    public EnumExptOperateType getOperateType() {
        return EnumExptOperateType.JUDGEHealthGoal;
    }

    @Override
    public void accept(ExptJudgeGoalResponse report, ExptOrgReportNodeDataVO node) {
        node.setJudgeGoal(report);
    }



    @Override
    public ExptJudgeGoalResponse getReportData(OrgReportExtractRequest req) throws ExecutionException, InterruptedException {
        ExptOperateOrgFuncRequest castReq= CopyWrapper.create(ExptOperateOrgFuncRequest::new)
                .endFrom(req);
        return experimentJudgeBiz.getJudgeGoal(castReq);
    }
}
