package org.dows.hep.rest.base.indicator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorJudgeHealthGuidanceRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorJudgeHealthGuidanceRequestRsV2;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthGuidanceResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthGuidanceResponseRsV2;
import org.dows.hep.api.constant.RsPageConstant;
import org.dows.hep.biz.base.indicator.IndicatorJudgeHealthGuidanceBiz;
import org.dows.hep.biz.base.indicator.IndicatorJudgeHealthGuidanceBizV2;
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
    private final IndicatorJudgeHealthGuidanceBizV2 indicatorJudgeHealthGuidanceBizV2;
    @Operation(summary = "Rs创建或保存查看指标健康指导类")
    @PostMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/createOrUpdateRs")
    public void createOrUpdateRs(@RequestBody @Validated CreateOrUpdateIndicatorJudgeHealthGuidanceRequestRs createOrUpdateIndicatorJudgeHealthGuidanceRequestRs) {
        indicatorJudgeHealthGuidanceBiz.createOrUpdateRs(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs);
    }

    @Operation(summary = "Rs批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/batchDeleteRs")
    public void batchDeleteRs(@RequestBody List<String> indicatorJudgeHealthGuidanceIdList) {
        indicatorJudgeHealthGuidanceBiz.batchDeleteRs(indicatorJudgeHealthGuidanceIdList);
    }

    @Operation(summary = "Rs更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/updateStatusRs")
    public void updateStatusRs(
        @RequestParam String indicatorJudgeHealthGuidanceId,
        @RequestParam Integer status) {
        indicatorJudgeHealthGuidanceBiz.updateStatusRs(indicatorJudgeHealthGuidanceId, status);
    }

    @Operation(summary = "V2Rs创建或保存查看指标健康指导类")
    @PostMapping("v2/baseIndicator/indicatorJudgeHealthGuidance/createOrUpdateRs")
    public void createOrUpdateRs(@RequestBody @Validated CreateOrUpdateIndicatorJudgeHealthGuidanceRequestRsV2 createOrUpdateIndicatorJudgeHealthGuidanceRequestRsV2) throws InterruptedException {
        indicatorJudgeHealthGuidanceBizV2.createOrUpdateRs(createOrUpdateIndicatorJudgeHealthGuidanceRequestRsV2);
    }

    @Operation(summary = "V2Rs获取查看指标健康指导类")
    @GetMapping("v2/baseIndicator/indicatorJudgeHealthGuidance/getRs")
    public IndicatorJudgeHealthGuidanceResponseRsV2 getRsV2(@RequestParam @Validated String indicatorJudgeHealthGuidanceId) {
        return indicatorJudgeHealthGuidanceBizV2.getRs(indicatorJudgeHealthGuidanceId);
    }

    @Operation(summary = "V2Rs分页筛选查看指标健康指导类")
    @GetMapping("v2/baseIndicator/indicatorJudgeHealthGuidance/pageRs")
    public Page<IndicatorJudgeHealthGuidanceResponseRsV2> pageRsV2(
            @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_NO) Long pageNo,
            @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_SIZE) Long pageSize,
            @RequestParam(required = false, defaultValue = RsPageConstant.ORDER) String order,
            @RequestParam(required = false, defaultValue = RsPageConstant.ASC) Boolean asc,
            @RequestParam(required = false) String appId,
            @RequestParam(required = false) String indicatorFuncId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String indicatorCategoryIdList,
            @RequestParam(required = false) Integer status) {
        return indicatorJudgeHealthGuidanceBizV2.pageRs(pageNo,pageSize,order,asc, appId,indicatorFuncId,name, indicatorCategoryIdList,status);
    }

    @Operation(summary = "Rs获取查看指标健康指导类")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/getRs")
    public IndicatorJudgeHealthGuidanceResponseRs getRs(@RequestParam @Validated String indicatorJudgeHealthGuidanceId) {
        return indicatorJudgeHealthGuidanceBiz.getRs(indicatorJudgeHealthGuidanceId);
    }

    @Operation(summary = "Rs分页筛选查看指标健康指导类")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthGuidance/pageRs")
    public Page<IndicatorJudgeHealthGuidanceResponseRs> pageRs(
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_NO) Long pageNo,
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_SIZE) Long pageSize,
        @RequestParam(required = false, defaultValue = RsPageConstant.ORDER) String order,
        @RequestParam(required = false, defaultValue = RsPageConstant.ASC) Boolean asc,
        @RequestParam(required = false) String appId,
        @RequestParam(required = false) String indicatorFuncId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String indicatorCategoryIdList,
        @RequestParam(required = false) Integer status) {
        return indicatorJudgeHealthGuidanceBiz.pageRs(pageNo,pageSize,order,asc, appId,indicatorFuncId,name, indicatorCategoryIdList,status);
    }


}