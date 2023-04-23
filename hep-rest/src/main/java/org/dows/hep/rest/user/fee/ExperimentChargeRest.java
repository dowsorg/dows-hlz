package org.dows.hep.rest.user.fee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.fee.request.RegistrationChargeRequest;
import org.dows.hep.biz.user.fee.ExperimentChargeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:费用:实验扣费
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验扣费", description = "实验扣费")
public class ExperimentChargeRest {
    private final ExperimentChargeBiz experimentChargeBiz;

    /**
    * 扣除挂号费
    * @param
    * @return
    */
    @Operation(summary = "扣除挂号费")
    @PostMapping("v1/userFee/experimentCharge/chargeRegistration")
    public Boolean chargeRegistration(@RequestBody @Validated RegistrationChargeRequest registrationCharge ) {
        return experimentChargeBiz.chargeRegistration(registrationCharge);
    }


}