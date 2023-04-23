package org.dows.hep.biz.user.fee;

import org.dows.hep.api.user.fee.request.RegistrationChargeRequest;
import org.springframework.stereotype.Service;

/**
* @description project descr:费用:实验扣费
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class ExperimentChargeBiz{
    /**
    * @param
    * @return
    * @说明: 扣除挂号费
    * @关联表: CaseOrgFee、IndicatorInstance、IndicatorPrincipalRef
    * @工时: 5H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean chargeRegistration(RegistrationChargeRequest registrationCharge ) {
        return Boolean.FALSE;
    }
}