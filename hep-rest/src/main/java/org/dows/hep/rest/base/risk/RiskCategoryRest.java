package org.dows.hep.rest.base.risk;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.risk.request.CreateRiskCategoryRequest;
import org.dows.hep.api.base.risk.request.UpdateRiskCategoryRequest;
import org.dows.hep.api.base.risk.response.RiskCategoryResponse;
import org.dows.hep.biz.base.risk.RiskCategoryBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:风险:风险类别
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
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
    * 获取风险类别
    * @param
    * @return
    */
    @Operation(summary = "获取风险类别")
    @GetMapping("v1/baseRisk/riskCategory/getRiskCategory")
    public RiskCategoryResponse getRiskCategory(@Validated String riskCategoryId) {
        return riskCategoryBiz.getRiskCategory(riskCategoryId);
    }

    /**
    * 筛选风险类别
    * @param
    * @return
    */
    @Operation(summary = "筛选风险类别")
    @GetMapping("v1/baseRisk/riskCategory/listRiskCategory")
    public List<RiskCategoryResponse> listRiskCategory(@Validated String appId, @Validated String riskCategoryName) {
        return riskCategoryBiz.listRiskCategory(appId,riskCategoryName);
    }

    /**
    * 分页筛选风险类别
    * @param
    * @return
    */
    @Operation(summary = "分页筛选风险类别")
    @GetMapping("v1/baseRisk/riskCategory/pageRiskCategory")
    public String pageRiskCategory(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String riskCategoryName) {
        return riskCategoryBiz.pageRiskCategory(pageNo,pageSize,appId,riskCategoryName);
    }


}