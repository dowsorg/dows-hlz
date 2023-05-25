package org.dows.hep.rest.base.indicator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeRiskFactorResponseRs;
import org.dows.hep.api.constant.RsPageConstant;
import org.dows.hep.biz.base.indicator.IndicatorJudgeRiskFactorBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标危险因素
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "判断指标危险因素", description = "判断指标危险因素")
public class IndicatorJudgeRiskFactorRest {
    private final IndicatorJudgeRiskFactorBiz indicatorJudgeRiskFactorBiz;

    @Operation(summary = "Rs创建或保存查看指标危险因素类")
    @PostMapping("v1/baseIndicator/indicatorJudgeRiskFactor/createOrUpdateRs")
    public void createOrUpdateRs(@RequestBody @Validated CreateOrUpdateIndicatorJudgeRiskFactorRequestRs createOrUpdateIndicatorJudgeRiskFactorRequestRs) {
        indicatorJudgeRiskFactorBiz.createOrUpdateRs(createOrUpdateIndicatorJudgeRiskFactorRequestRs);
    }

    @Operation(summary = "Rs批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeRiskFactor/batchDeleteRs")
    public void batchDeleteRs(@RequestBody List<String> indicatorJudgeRiskFactorIdList) {
        indicatorJudgeRiskFactorBiz.batchDeleteRs(indicatorJudgeRiskFactorIdList);
    }

    @Operation(summary = "Rs更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorJudgeRiskFactor/updateStatusRs")
    public void updateStatusRs(
        @RequestParam String indicatorJudgeRiskFactorId,
        @RequestParam Integer status) {
        indicatorJudgeRiskFactorBiz.updateStatusRs(indicatorJudgeRiskFactorId, status);
    }

    @Operation(summary = "Rs获取查看指标危险因素类")
    @GetMapping("v1/baseIndicator/indicatorJudgeRiskFactor/getRs")
    public IndicatorJudgeRiskFactorResponseRs getRs(@RequestParam @Validated String indicatorJudgeRiskFactorId) {
        return indicatorJudgeRiskFactorBiz.getRs(indicatorJudgeRiskFactorId);
    }

    @Operation(summary = "Rs分页筛选查看指标危险因素类")
    @GetMapping("v1/baseIndicator/indicatorJudgeRiskFactor/pageRs")
    public Page<IndicatorJudgeRiskFactorResponseRs> pageRs(
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_NO) Long pageNo,
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_SIZE) Long pageSize,
        @RequestParam(required = false, defaultValue = RsPageConstant.ORDER) String order,
        @RequestParam(required = false, defaultValue = RsPageConstant.ASC) Boolean asc,
        @RequestParam(required = false) String appId,
        @RequestParam(required = false) String indicatorFuncId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String indicatorCategoryIdList,
        @RequestParam(required = false) Integer status) {
        return indicatorJudgeRiskFactorBiz.pageRs(pageNo,pageSize,order,asc, appId,indicatorFuncId,name, indicatorCategoryIdList,status);
    }

//    /**
//    * 创建危险因素
//    * @param
//    * @return
//    */
//    @Operation(summary = "创建危险因素")
//    @PostMapping("v1/baseIndicator/indicatorJudgeRiskFactor/createIndicatorJudgeRiskFactor")
//    public void createIndicatorJudgeRiskFactor(@RequestBody @Validated CreateIndicatorJudgeRiskFactorRequest createIndicatorJudgeRiskFactor ) {
//        indicatorJudgeRiskFactorBiz.createIndicatorJudgeRiskFactor(createIndicatorJudgeRiskFactor);
//    }
//
//    /**
//    * 删除判断指标危险因素
//    * @param
//    * @return
//    */
//    @Operation(summary = "删除判断指标危险因素")
//    @DeleteMapping("v1/baseIndicator/indicatorJudgeRiskFactor/deleteIndicatorJudgeRiskFactor")
//    public void deleteIndicatorJudgeRiskFactor(@Validated String indicatorJudgeRiskFactorId) {
//        indicatorJudgeRiskFactorBiz.deleteIndicatorJudgeRiskFactor(indicatorJudgeRiskFactorId);
//    }
//
//    /**
//    * 批量删除
//    * @param
//    * @return
//    */
//    @Operation(summary = "批量删除")
//    @DeleteMapping("v1/baseIndicator/indicatorJudgeRiskFactor/batchDelete")
//    public void batchDelete(@Validated String string ) {
//        indicatorJudgeRiskFactorBiz.batchDelete(string);
//    }
//
//    /**
//    * 更改启用状态
//    * @param
//    * @return
//    */
//    @Operation(summary = "更改启用状态")
//    @PutMapping("v1/baseIndicator/indicatorJudgeRiskFactor/updateStatus")
//    public void updateStatus(@Validated UpdateStatusIndicatorJudgeRiskFactorRequest updateStatusIndicatorJudgeRiskFactor ) {
//        indicatorJudgeRiskFactorBiz.updateStatus(updateStatusIndicatorJudgeRiskFactor);
//    }
//
//    /**
//    * 判断指标危险因素
//    * @param
//    * @return
//    */
//    @Operation(summary = "判断指标危险因素")
//    @PutMapping("v1/baseIndicator/indicatorJudgeRiskFactor/updateIndicatorJudgeRiskFactor")
//    public void updateIndicatorJudgeRiskFactor(@Validated UpdateIndicatorJudgeRiskFactorRequest updateIndicatorJudgeRiskFactor ) {
//        indicatorJudgeRiskFactorBiz.updateIndicatorJudgeRiskFactor(updateIndicatorJudgeRiskFactor);
//    }
//
//    /**
//    * 判断指标危险因素
//    * @param
//    * @return
//    */
//    @Operation(summary = "判断指标危险因素")
//    @GetMapping("v1/baseIndicator/indicatorJudgeRiskFactor/getIndicatorJudgeRiskFactor")
//    public IndicatorJudgeRiskFactorResponse getIndicatorJudgeRiskFactor(@Validated String indicatorJudgeRiskFactorId) {
//        return indicatorJudgeRiskFactorBiz.getIndicatorJudgeRiskFactor(indicatorJudgeRiskFactorId);
//    }
//
//    /**
//    * 筛选判断指标危险因素
//    * @param
//    * @return
//    */
//    @Operation(summary = "筛选判断指标危险因素")
//    @GetMapping("v1/baseIndicator/indicatorJudgeRiskFactor/listIndicatorJudgeRiskFactor")
//    public List<IndicatorJudgeRiskFactorResponse> listIndicatorJudgeRiskFactor(@Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest point, @Validated String expression, @Validated String resultExplain, @Validated Integer status) {
//        return indicatorJudgeRiskFactorBiz.listIndicatorJudgeRiskFactor(appId,indicatorCategoryId,name,type,point,expression,resultExplain,status);
//    }
//
//    /**
//    * 分页筛选判断指标危险因素
//    * @param
//    * @return
//    */
//    @Operation(summary = "分页筛选判断指标危险因素")
//    @GetMapping("v1/baseIndicator/indicatorJudgeRiskFactor/pageIndicatorJudgeRiskFactor")
//    public String pageIndicatorJudgeRiskFactor(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest point, @Validated String expression, @Validated String resultExplain, @Validated Integer status) {
//        return indicatorJudgeRiskFactorBiz.pageIndicatorJudgeRiskFactor(pageNo,pageSize,appId,indicatorCategoryId,name,type,point,expression,resultExplain,status);
//    }


}