package org.dows.hep.rest.base.indicator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeDiseaseProblemResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeDiseaseProblemResponseRs;
import org.dows.hep.api.constant.RsPageConstant;
import org.dows.hep.biz.base.indicator.IndicatorJudgeDiseaseProblemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标疾病问题
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "判断指标疾病问题", description = "判断指标疾病问题")
public class IndicatorJudgeDiseaseProblemRest {
    private final IndicatorJudgeDiseaseProblemBiz indicatorJudgeDiseaseProblemBiz;

    @Operation(summary = "Rs创建或保存查看指标疾病问题类")
    @PostMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/createOrUpdateRs")
    public void createOrUpdateRs(@RequestBody @Validated CreateOrUpdateIndicatorJudgeDiseaseProblemRequestRs createOrUpdateIndicatorJudgeDiseaseProblemRequestRs) {
        indicatorJudgeDiseaseProblemBiz.createOrUpdateRs(createOrUpdateIndicatorJudgeDiseaseProblemRequestRs);
    }

    @Operation(summary = "Rs批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/batchDeleteRs")
    public void batchDeleteRs(@RequestBody List<String> indicatorJudgeDiseaseProblemIdList) {
        indicatorJudgeDiseaseProblemBiz.batchDeleteRs(indicatorJudgeDiseaseProblemIdList);
    }

    @Operation(summary = "Rs更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/updateStatusRs")
    public void updateStatusRs(
        @RequestParam String indicatorJudgeDiseaseProblemId,
        @RequestParam Integer status) {
        indicatorJudgeDiseaseProblemBiz.updateStatusRs(indicatorJudgeDiseaseProblemId, status);
    }

    @Operation(summary = "Rs获取查看指标疾病问题类")
    @GetMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/getRs")
    public IndicatorJudgeDiseaseProblemResponseRs getRs(@RequestParam @Validated String indicatorJudgeDiseaseProblemId) {
        return indicatorJudgeDiseaseProblemBiz.getRs(indicatorJudgeDiseaseProblemId);
    }

    @Operation(summary = "Rs分页筛选查看指标疾病问题类")
    @GetMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/pageRs")
    public IPage<IndicatorJudgeDiseaseProblemResponseRs> pageRs(
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_NO) Long pageNo,
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_SIZE) Long pageSize,
        @RequestParam(required = false, defaultValue = RsPageConstant.ORDER) String order,
        @RequestParam(required = false, defaultValue = RsPageConstant.ASC) Boolean asc,
        @RequestParam(required = false) String appId,
        @RequestParam(required = false) String indicatorFuncId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String indicatorCategoryId,
        @RequestParam(required = false) Integer status) {
        return indicatorJudgeDiseaseProblemBiz.pageRs(pageNo,pageSize,order,asc, appId,indicatorFuncId,name,indicatorCategoryId,status);
    }

//    /**
//    * 创建判断指标疾病问题
//    * @param
//    * @return
//    */
//    @Operation(summary = "创建判断指标疾病问题")
//    @PostMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/createIndicatorJudgeDiseaseProblem")
//    public void createIndicatorJudgeDiseaseProblem(@RequestBody @Validated CreateIndicatorJudgeDiseaseProblemRequest createIndicatorJudgeDiseaseProblem ) {
//        indicatorJudgeDiseaseProblemBiz.createIndicatorJudgeDiseaseProblem(createIndicatorJudgeDiseaseProblem);
//    }
//
//    /**
//    * 删除判断指标疾病问题
//    * @param
//    * @return
//    */
//    @Operation(summary = "删除判断指标疾病问题")
//    @DeleteMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/deleteIndicatorJudgeDiseaseProblem")
//    public void deleteIndicatorJudgeDiseaseProblem(@Validated String indicatorJudgeDiseaseProblemId ) {
//        indicatorJudgeDiseaseProblemBiz.deleteIndicatorJudgeDiseaseProblem(indicatorJudgeDiseaseProblemId);
//    }
//
//    /**
//    * 批量删除
//    * @param
//    * @return
//    */
//    @Operation(summary = "批量删除")
//    @DeleteMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/batchDelete")
//    public void batchDelete(@Validated String string ) {
//        indicatorJudgeDiseaseProblemBiz.batchDelete(string);
//    }
//
//    /**
//    * 更改启用状态
//    * @param
//    * @return
//    */
//    @Operation(summary = "更改启用状态")
//    @PutMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/updateStatus")
//    public void updateStatus(@Validated UpdateStatusIndicatorJudgeDiseaseProblemRequest updateStatusIndicatorJudgeDiseaseProblem ) {
//        indicatorJudgeDiseaseProblemBiz.updateStatus(updateStatusIndicatorJudgeDiseaseProblem);
//    }
//
//    /**
//    * 判断指标疾病问题
//    * @param
//    * @return
//    */
//    @Operation(summary = "判断指标疾病问题")
//    @PutMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/updateIndicatorJudgeDiseaseProblem")
//    public void updateIndicatorJudgeDiseaseProblem(@Validated UpdateIndicatorJudgeDiseaseProblemRequest updateIndicatorJudgeDiseaseProblem ) {
//        indicatorJudgeDiseaseProblemBiz.updateIndicatorJudgeDiseaseProblem(updateIndicatorJudgeDiseaseProblem);
//    }
//
//    /**
//    * 获取判断指标疾病问题
//    * @param
//    * @return
//    */
//    @Operation(summary = "获取判断指标疾病问题")
//    @GetMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/getIndicatorJudgeDiseaseProblem")
//    public IndicatorJudgeDiseaseProblemResponse getIndicatorJudgeDiseaseProblem(@Validated String indicatorJudgeDiseaseProblemId) {
//        return indicatorJudgeDiseaseProblemBiz.getIndicatorJudgeDiseaseProblem(indicatorJudgeDiseaseProblemId);
//    }
//
//    /**
//    * 筛选判断指标疾病问题
//    * @param
//    * @return
//    */
//    @Operation(summary = "筛选判断指标疾病问题")
//    @GetMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/listIndicatorJudgeDiseaseProblem")
//    public List<IndicatorJudgeDiseaseProblemResponse> listIndicatorJudgeDiseaseProblem(@Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest point, @Validated String expression, @Validated String resultExplain, @Validated Integer status) {
//        return indicatorJudgeDiseaseProblemBiz.listIndicatorJudgeDiseaseProblem(appId,indicatorCategoryId,name,type,point,expression,resultExplain,status);
//    }
//
//    /**
//    * 分页筛选判断指标疾病问题
//    * @param
//    * @return
//    */
//    @Operation(summary = "分页筛选判断指标疾病问题")
//    @GetMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/pageIndicatorJudgeDiseaseProblem")
//    public String pageIndicatorJudgeDiseaseProblem(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest point, @Validated String expression, @Validated String resultExplain, @Validated Integer status) {
//        return indicatorJudgeDiseaseProblemBiz.pageIndicatorJudgeDiseaseProblem(pageNo,pageSize,appId,indicatorCategoryId,name,type,point,expression,resultExplain,status);
//    }


}