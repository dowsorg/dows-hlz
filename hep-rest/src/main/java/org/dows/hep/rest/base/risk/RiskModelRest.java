package org.dows.hep.rest.base.risk;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.crud.api.model.PageResponse;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateRiskModelRequestRs;
import org.dows.hep.api.base.risk.request.PageRiskModelRequest;
import org.dows.hep.api.base.risk.response.RiskModelResponse;
import org.dows.hep.biz.base.risk.RiskModelBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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
    @PostMapping("v1/baseRisk/riskModel/createOrUpdateRiskModel")
    public Boolean createOrUpdateRiskModel(@RequestBody @Validated CreateOrUpdateRiskModelRequestRs createOrUpdateRiskModelRequestRs) {
        return riskModelBiz.createOrUpdateRiskModel(createOrUpdateRiskModelRequestRs);
    }

    @Operation(summary = "查询风险模型")
    @GetMapping("v1/baseRisk/riskModel/getRiskModel/{riskModelId}")
    public RiskModelResponse getRiskModelByRiskModelId(@PathVariable @Validated String riskModelId) {
        return riskModelBiz.getRiskModelByRiskModelId(riskModelId);
    }

    @Operation(summary = "删除风险模型")
    @DeleteMapping("v1/baseRisk/riskModel/batchDelRiskModels")
    public Boolean batchDelRiskModels(@RequestBody @Validated Set<String> riskModelIds) {
        return riskModelBiz.batchDelRiskModels(riskModelIds);
    }

    @Operation(summary = "更新风险模型状态")
    @PutMapping("v1/baseRisk/riskModel/updateRiskModelStatus")
    public Boolean updateRiskModelStatus(@RequestParam @Validated String riskModelId,
                                         @RequestParam @Validated Integer status) {
        return riskModelBiz.updateRiskModelStatus(riskModelId,status);
    }

    @Operation(summary = "分页获取风险模型列表")
    @GetMapping("v1/baseRisk/riskModel/page")
    public PageResponse<RiskModelResponse> page(PageRiskModelRequest pageRiskModelRequest) {
        return riskModelBiz.page(pageRiskModelRequest);
    }
}