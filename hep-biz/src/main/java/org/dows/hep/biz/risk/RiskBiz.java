package org.dows.hep.biz.risk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.base.risk.CrowdsInstanceBiz;
import org.dows.hep.biz.base.risk.RiskModelBiz;
import org.dows.hep.service.ExperimentCrowdsInstanceRsService;
import org.dows.hep.service.ExperimentIndicatorExpressionRefRsService;
import org.dows.hep.service.ExperimentIndicatorExpressionRsService;
import org.dows.hep.service.ExperimentRiskModelRsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RiskBiz {

    private final RiskModelBiz riskModelBiz;

    private final CrowdsInstanceBiz crowdsInstanceBiz;

    private final ExperimentRiskModelRsService experimentRiskModelRsService;

    private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;

    private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;

    private final ExperimentCrowdsInstanceRsService experimentCrowdsInstanceRsService;


    /**
     * 获取死亡原因
     */
    public void getDeadReason(String experimentInstanceId,String experimentGroupId,String period){

        //experimentRiskModelRsService

    }


    /**
     * 获取危险因素
     */
    public void getDangerFactor(String experimentInstanceId,String experimentGroupId,String period){



    }

}
