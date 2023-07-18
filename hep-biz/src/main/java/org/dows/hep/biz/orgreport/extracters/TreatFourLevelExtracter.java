package org.dows.hep.biz.orgreport.extracters;

import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.response.ExptTreatPlanResponse;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/7/18 11:42
 */
@Component
public class TreatFourLevelExtracter extends TreatTwoLevelExtracter{

    @Override
    public EnumExptOperateType getOperateType() {
        return EnumExptOperateType.INTERVENETreatFourLevel;
    }

    @Override
    public void accept(ExptTreatPlanResponse report, ExptOrgReportNodeDataVO node) {
        node.setTreatFourLevel(report);
    }
}
