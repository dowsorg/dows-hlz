package org.dows.hep.rest.base.risk;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.risk.request.CreateRiskModelRequest;
import org.dows.hep.api.base.risk.request.UpdateRiskModelRequest;
import org.dows.hep.api.base.risk.request.UpdateStatusRiskModelRequest;
import org.dows.hep.api.base.risk.response.RiskModelResponse;
import org.dows.hep.biz.base.risk.RiskModelBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
    * 创建风险模型
    * @param
    * @return
    */
    @Operation(summary = "创建风险模型")
    @PostMapping("v1/baseRisk/riskModel/createRiskModel")
    public void createRiskModel(@RequestBody @Validated CreateRiskModelRequest createRiskModel ) {
        riskModelBiz.createRiskModel(createRiskModel);
    }

    /**
    * 删除风险模型
    * @param
    * @return
    */
    @Operation(summary = "删除风险模型")
    @DeleteMapping("v1/baseRisk/riskModel/deleteRiskModel")
    public void deleteRiskModel(@Validated String riskModelId ) {
        riskModelBiz.deleteRiskModel(riskModelId);
    }

    /**
    * 更改风险模型
    * @param
    * @return
    */
    @Operation(summary = "更改风险模型")
    @PutMapping("v1/baseRisk/riskModel/updateRiskModel")
    public void updateRiskModel(@Validated UpdateRiskModelRequest updateRiskModel ) {
        riskModelBiz.updateRiskModel(updateRiskModel);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @Operation(summary = "更改启用状态")
    @PutMapping("v1/baseRisk/riskModel/updateStatusRiskModel")
    public void updateStatusRiskModel(@Validated UpdateStatusRiskModelRequest updateStatusRiskModel ) {
        riskModelBiz.updateStatusRiskModel(updateStatusRiskModel);
    }

    /**
    * 获取风险模型
    * @param
    * @return
    */
    @Operation(summary = "获取风险模型")
    @GetMapping("v1/baseRisk/riskModel/getRiskModel")
    public RiskModelResponse getRiskModel(@Validated String riskModelId) {
        return riskModelBiz.getRiskModel(riskModelId);
    }

    /**
    * 筛选风险模型
    * @param
    * @return
    */
    @Operation(summary = "筛选风险模型")
    @GetMapping("v1/baseRisk/riskModel/listRiskModel")
    public List<RiskModelResponse> listRiskModel(@Validated String appId, @Validated String riskModelId, @Validated String modelName, @Validated Integer status) {
        return riskModelBiz.listRiskModel(appId,riskModelId,modelName,status);
    }

    /**
    * 分页筛选风险模型
    * @param
    * @return
    */
    @Operation(summary = "分页筛选风险模型")
    @GetMapping("v1/baseRisk/riskModel/pageRiskModel")
    public String pageRiskModel(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String riskModelId, @Validated String modelName, @Validated Integer status) {
        return riskModelBiz.pageRiskModel(pageNo,pageSize,appId,riskModelId,modelName,status);
    }


}