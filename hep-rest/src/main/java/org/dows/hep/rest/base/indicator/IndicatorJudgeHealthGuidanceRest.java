package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthGuidanceRequest;
import org.dows.hep.api.base.indicator.request.DecimalRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeHealthGuidanceRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeHealthGuidanceRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthGuidanceResponse;
import org.dows.hep.biz.base.indicator.IndicatorJudgeHealthGuidanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标健康指导
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "判断指标健康指导", description = "判断指标健康指导")
public class IndicatorJudgeHealthGuidanceRest {
    private final IndicatorJudgeHealthGuidanceBiz indicatorJudgeHealthGuidanceBiz;

    /**
    * 创建判断指标健康指导
    * @param
    * @return
    */
    @Operation(summary = "创建判断指标健康指导")
    @PostMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/createIndicatorJudgeHealthGuidance")
    public void createIndicatorJudgeHealthGuidance(@RequestBody @Validated CreateIndicatorJudgeHealthGuidanceRequest createIndicatorJudgeHealthGuidance ) {
        indicatorJudgeHealthGuidanceBiz.createIndicatorJudgeHealthGuidance(createIndicatorJudgeHealthGuidance);
    }

    /**
    * 删除判断指标健康指导
    * @param
    * @return
    */
    @Operation(summary = "删除判断指标健康指导")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/deleteIndicatorJudgeHealthGuidance")
    public void deleteIndicatorJudgeHealthGuidance(@Validated String indicatorJudgeHealthGuidanceId ) {
        indicatorJudgeHealthGuidanceBiz.deleteIndicatorJudgeHealthGuidance(indicatorJudgeHealthGuidanceId);
    }

    /**
    * 批量删除
    * @param
    * @return
    */
    @Operation(summary = "批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/batchDelete")
    public void batchDelete(@Validated String string ) {
        indicatorJudgeHealthGuidanceBiz.batchDelete(string);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @Operation(summary = "更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/updateStatus")
    public void updateStatus(@Validated UpdateStatusIndicatorJudgeHealthGuidanceRequest updateStatusIndicatorJudgeHealthGuidance ) {
        indicatorJudgeHealthGuidanceBiz.updateStatus(updateStatusIndicatorJudgeHealthGuidance);
    }

    /**
    * 判断指标健康指导
    * @param
    * @return
    */
    @Operation(summary = "判断指标健康指导")
    @PutMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/updateIndicatorJudgeHealthGuidance")
    public void updateIndicatorJudgeHealthGuidance(@Validated UpdateIndicatorJudgeHealthGuidanceRequest updateIndicatorJudgeHealthGuidance ) {
        indicatorJudgeHealthGuidanceBiz.updateIndicatorJudgeHealthGuidance(updateIndicatorJudgeHealthGuidance);
    }

    /**
    * 获取判断指标健康指导
    * @param
    * @return
    */
    @Operation(summary = "获取判断指标健康指导")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/getIndicatorJudgeHealthGuidance")
    public IndicatorJudgeHealthGuidanceResponse getIndicatorJudgeHealthGuidance(@Validated String indicatorJudgeHealthGuidanceId) {
        return indicatorJudgeHealthGuidanceBiz.getIndicatorJudgeHealthGuidance(indicatorJudgeHealthGuidanceId);
    }

    /**
    * 筛选判断指标健康指导
    * @param
    * @return
    */
    @Operation(summary = "筛选判断指标健康指导")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/listIndicatorJudgeHealthGuidance")
    public List<IndicatorJudgeHealthGuidanceResponse> listIndicatorJudgeHealthGuidance(@Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest point, @Validated String expression, @Validated String resultExplain, @Validated Integer status) {
        return indicatorJudgeHealthGuidanceBiz.listIndicatorJudgeHealthGuidance(appId,indicatorCategoryId,name,type,point,expression,resultExplain,status);
    }

    /**
    * 分页筛选判断指标健康指导
    * @param
    * @return
    */
    @Operation(summary = "分页筛选判断指标健康指导")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/pageIndicatorJudgeHealthGuidance")
    public String pageIndicatorJudgeHealthGuidance(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest point, @Validated String expression, @Validated String resultExplain, @Validated Integer status) {
        return indicatorJudgeHealthGuidanceBiz.pageIndicatorJudgeHealthGuidance(pageNo,pageSize,appId,indicatorCategoryId,name,type,point,expression,resultExplain,status);
    }


}