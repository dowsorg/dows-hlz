package org.dows.hep.rest.base.risk;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.risk.request.CreateRiskCategoryRequest;
import org.dows.hep.api.base.risk.request.UpdateRiskCategoryRequest;
import org.dows.hep.api.base.risk.response.RiskCategoryResponse;
import org.dows.hep.biz.base.risk.RiskCategoryBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:风险:风险类别
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "风险类别", description = "风险类别")
public class RiskCategoryRest {
    private final RiskCategoryBiz riskCategoryBiz;

    /**
    * 创建风险类别
    * @param
    * @return
    */
    @Operation(summary = "创建风险类别")
    @PostMapping("v1/baseRisk/riskCategory/createRiskCategory")
    public void createRiskCategory(@RequestBody @Validated CreateRiskCategoryRequest createRiskCategory ) {
        riskCategoryBiz.createRiskCategory(createRiskCategory);
    }

    /**
    * 删除风险类别
    * @param
    * @return
    */
    @Operation(summary = "删除风险类别")
    @DeleteMapping("v1/baseRisk/riskCategory/deleteRiskCategory")
    public void deleteRiskCategory(@Validated String riskCategoryId ) {
        riskCategoryBiz.deleteRiskCategory(riskCategoryId);
    }

    /**
    * 更改风险类别
    * @param
    * @return
    */
    @Operation(summary = "更改风险类别")
    @PutMapping("v1/baseRisk/riskCategory/updateRiskCategory")
    public void updateRiskCategory(@Validated UpdateRiskCategoryRequest updateRiskCategory ) {
        riskCategoryBiz.updateRiskCategory(updateRiskCategory);
    }

    /**
    * 查询风险类别
    * @param
    * @return
    */
    @Operation(summary = "查询风险类别")
    @GetMapping("v1/baseRisk/riskCategory/getRiskCategory")
    public RiskCategoryResponse getRiskCategory(@Validated String riskCategoryId) {
        return riskCategoryBiz.getRiskCategory(riskCategoryId);
    }


}