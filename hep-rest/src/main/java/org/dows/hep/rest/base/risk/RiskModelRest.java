package org.dows.hep.rest.base.risk;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateRiskModelRequestRs;
import org.dows.hep.biz.base.risk.RiskModelBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:风险:风险模型
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "风险模型", description = "风险模型")
public class RiskModelRest {
    private final RiskModelBiz riskModelBiz;

    @Operation(summary = "创建或更新风险模型")
    @PostMapping("v1/baseRisk/riskModel/createOrUpdateRs")
    public Boolean createOrUpdateRs(@RequestBody @Validated CreateOrUpdateRiskModelRequestRs createOrUpdateRiskModelRequestRs) {
        return riskModelBiz.createOrUpdateRs(createOrUpdateRiskModelRequestRs);
    }
}