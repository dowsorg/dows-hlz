package org.dows.hep.rest.risk;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.risk.request.CreateRiskCategoryRequest;
import org.dows.hep.api.risk.request.UpdateRiskCategoryRequest;
import org.dows.hep.api.risk.response.RiskCategoryResponse;
import org.dows.hep.biz.risk.RiskCategoryBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:风险:风险类别
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "风险类别")
public class RiskCategoryRest {
    private final RiskCategoryBiz riskCategoryBiz;

    /**
    * 创建风险类别
    * @param
    * @return
    */
    @ApiOperation("创建风险类别")
    @PostMapping("v1/risk/riskCategory/createRiskCategory")
    public void createRiskCategory(@RequestBody @Validated CreateRiskCategoryRequest createRiskCategory ) {
        riskCategoryBiz.createRiskCategory(createRiskCategory);
    }

    /**
    * 删除风险类别
    * @param
    * @return
    */
    @ApiOperation("删除风险类别")
    @DeleteMapping("v1/risk/riskCategory/deleteRiskCategory")
    public void deleteRiskCategory(@Validated String riskCategoryId ) {
        riskCategoryBiz.deleteRiskCategory(riskCategoryId);
    }

    /**
    * 更改风险类别
    * @param
    * @return
    */
    @ApiOperation("更改风险类别")
    @PutMapping("v1/risk/riskCategory/updateRiskCategory")
    public void updateRiskCategory(@Validated UpdateRiskCategoryRequest updateRiskCategory ) {
        riskCategoryBiz.updateRiskCategory(updateRiskCategory);
    }

    /**
    * 查询风险类别
    * @param
    * @return
    */
    @ApiOperation("查询风险类别")
    @GetMapping("v1/risk/riskCategory/getRiskCategory")
    public RiskCategoryResponse getRiskCategory(@Validated String riskCategoryId) {
        return riskCategoryBiz.getRiskCategory(riskCategoryId);
    }


}