package org.dows.hep.rest.risk;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.risk.request.CreateRiskModelRequest;
import org.dows.hep.api.risk.request.UpdateRiskModelRequest;
import org.dows.hep.api.risk.request.UpdateStatusRiskModelRequest;
import org.dows.hep.api.risk.response.RiskModelResponse;
import org.dows.hep.biz.risk.RiskModelBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:风险:风险模型
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "风险模型")
public class RiskModelRest {
    private final RiskModelBiz riskModelBiz;

    /**
    * 创建风险模型
    * @param
    * @return
    */
    @ApiOperation("创建风险模型")
    @PostMapping("v1/risk/riskModel/createRiskModel")
    public void createRiskModel(@RequestBody @Validated CreateRiskModelRequest createRiskModel ) {
        riskModelBiz.createRiskModel(createRiskModel);
    }

    /**
    * 删除风险模型
    * @param
    * @return
    */
    @ApiOperation("删除风险模型")
    @DeleteMapping("v1/risk/riskModel/deleteRiskModel")
    public void deleteRiskModel(@Validated String riskModelId ) {
        riskModelBiz.deleteRiskModel(riskModelId);
    }

    /**
    * 更改风险模型
    * @param
    * @return
    */
    @ApiOperation("更改风险模型")
    @PutMapping("v1/risk/riskModel/updateRiskModel")
    public void updateRiskModel(@Validated UpdateRiskModelRequest updateRiskModel ) {
        riskModelBiz.updateRiskModel(updateRiskModel);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @ApiOperation("更改启用状态")
    @PutMapping("v1/risk/riskModel/updateStatusRiskModel")
    public void updateStatusRiskModel(@Validated UpdateStatusRiskModelRequest updateStatusRiskModel ) {
        riskModelBiz.updateStatusRiskModel(updateStatusRiskModel);
    }

    /**
    * 查询风险模型
    * @param
    * @return
    */
    @ApiOperation("查询风险模型")
    @GetMapping("v1/risk/riskModel/getRiskModel")
    public RiskModelResponse getRiskModel(@Validated String riskModelId) {
        return riskModelBiz.getRiskModel(riskModelId);
    }


}