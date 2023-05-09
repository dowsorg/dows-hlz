package org.dows.hep.rest.base.indicator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorViewMonitorFollowupResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorViewPhysicalExamResponse;
import org.dows.hep.api.base.indicator.response.IndicatorViewPhysicalExamResponseRs;
import org.dows.hep.api.constant.RsPageConstant;
import org.dows.hep.biz.base.indicator.IndicatorViewPhysicalExamBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标体格检查类
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "查看指标体格检查类", description = "查看指标体格检查类")
public class IndicatorViewPhysicalExamRest {
    private final IndicatorViewPhysicalExamBiz indicatorViewPhysicalExamBiz;

    /**
    * 创建查看指标体格检查类
    * @param
    * @return
    */
    @Operation(summary = "创建查看指标体格检查类")
    @PostMapping("v1/baseIndicator/indicatorViewPhysicalExam/createIndicatorViewPhysicalExam")
    public void createIndicatorViewPhysicalExam(@RequestBody @Validated CreateIndicatorViewPhysicalExamRequest createIndicatorViewPhysicalExam ) {
        indicatorViewPhysicalExamBiz.createIndicatorViewPhysicalExam(createIndicatorViewPhysicalExam);
    }

    @Operation(summary = "Rs创建或保存查看指标体格检查类")
    @PostMapping("v1/baseIndicator/indicatorViewPhysicalExam/createOrUpdateRs")
    public void createOrUpdateRs(@RequestBody @Validated CreateOrUpdateIndicatorViewPhysicalExamRequestRs createOrUpdateIndicatorViewPhysicalExamRequestRs ) {
        indicatorViewPhysicalExamBiz.createOrUpdateRs(createOrUpdateIndicatorViewPhysicalExamRequestRs);
    }

    @Operation(summary = "Rs批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorViewPhysicalExam/batchDeleteRs")
    public void batchDeleteRs(@RequestBody List<String> indicatorViewPhysicalExamIdList) {
        indicatorViewPhysicalExamBiz.batchDeleteRs(indicatorViewPhysicalExamIdList);
    }

    /**
    * 删除指标体格检查类
    * @param
    * @return
    */
    @Operation(summary = "删除指标体格检查类")
    @DeleteMapping("v1/baseIndicator/indicatorViewPhysicalExam/deleteIndicatorViewPhysicalExam")
    public void deleteIndicatorViewPhysicalExam(@Validated String indicatorViewPhysicalExamId ) {
        indicatorViewPhysicalExamBiz.deleteIndicatorViewPhysicalExam(indicatorViewPhysicalExamId);
    }

    /**
    * 批量删除
    * @param
    * @return
    */
    @Operation(summary = "批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorViewPhysicalExam/batchDelete")
    public void batchDelete(@Validated String string ) {
        indicatorViewPhysicalExamBiz.batchDelete(string);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @Operation(summary = "更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorViewPhysicalExam/updateStatus")
    public void updateStatus(@Validated IndicatorViewPhysicalExamRequest indicatorViewPhysicalExam ) {
        indicatorViewPhysicalExamBiz.updateStatus(indicatorViewPhysicalExam);
    }

    @Operation(summary = "Rs更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorViewPhysicalExam/updateStatusRs")
    public void updateStatusRs(
        @RequestParam String indicatorViewPhysicalExamId,
        @RequestParam Integer status) {
        indicatorViewPhysicalExamBiz.updateStatusRs(indicatorViewPhysicalExamId, status);
    }

    /**
    * 查看指标体格检查类
    * @param
    * @return
    */
    @Operation(summary = "查看指标体格检查类")
    @PutMapping("v1/baseIndicator/indicatorViewPhysicalExam/updateIndicatorViewPhysicalExam")
    public void updateIndicatorViewPhysicalExam(@Validated UpdateIndicatorViewPhysicalExamRequest updateIndicatorViewPhysicalExam ) {
        indicatorViewPhysicalExamBiz.updateIndicatorViewPhysicalExam(updateIndicatorViewPhysicalExam);
    }

    /**
    * 获取查看指标体格检查类
    * @param
    * @return
    */
    @Operation(summary = "获取查看指标体格检查类")
    @GetMapping("v1/baseIndicator/indicatorViewPhysicalExam/getIndicatorViewPhysicalExam")
    public IndicatorViewPhysicalExamResponse getIndicatorViewPhysicalExam(@Validated String indicatorViewPhysicalExamId) {
        return indicatorViewPhysicalExamBiz.getIndicatorViewPhysicalExam(indicatorViewPhysicalExamId);
    }

    @Operation(summary = "Rs获取查看指标体格检查类")
    @GetMapping("v1/baseIndicator/indicatorViewPhysicalExam/getRs")
    public IndicatorViewPhysicalExamResponseRs getRs(@RequestParam @Validated String indicatorViewPhysicalExamId) {
        return indicatorViewPhysicalExamBiz.getRs(indicatorViewPhysicalExamId);
    }

    /**
    * 筛选查看指标体格检查类
    * @param
    * @return
    */
    @Operation(summary = "筛选查看指标体格检查类")
    @GetMapping("v1/baseIndicator/indicatorViewPhysicalExam/listIndicatorViewPhysicalExam")
    public List<IndicatorViewPhysicalExamResponse> listIndicatorViewPhysicalExam(@Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated Integer type, @Validated DecimalRequest fee, @Validated String resultAnalysis, @Validated Integer status) {
        return indicatorViewPhysicalExamBiz.listIndicatorViewPhysicalExam(appId,indicatorCategoryId,name,type,fee,resultAnalysis,status);
    }

    /**
    * 分页筛选查看指标体格检查类
    * @param
    * @return
    */
    @Operation(summary = "分页筛选查看指标体格检查类")
    @GetMapping("v1/baseIndicator/indicatorViewPhysicalExam/pageIndicatorViewPhysicalExam")
    public String pageIndicatorViewPhysicalExam(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated Integer type, @Validated DecimalRequest fee, @Validated String resultAnalysis, @Validated Integer status) {
        return indicatorViewPhysicalExamBiz.pageIndicatorViewPhysicalExam(pageNo,pageSize,appId,indicatorCategoryId,name,type,fee,resultAnalysis,status);
    }

    @Operation(summary = "Rs分页筛选查看指标体格检查类")
    @GetMapping("v1/baseIndicator/indicatorViewPhysicalExam/pageRs")
    public IPage<IndicatorViewPhysicalExamResponseRs> pageRs(
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_NO) Long pageNo,
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_SIZE) Long pageSize,
        @RequestParam(required = false, defaultValue = RsPageConstant.ORDER) String order,
        @RequestParam(required = false, defaultValue = RsPageConstant.ASC) Boolean asc,
        @RequestParam(required = false) String appId,
        @RequestParam(required = false) String indicatorFuncId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String indicatorCategoryId,
        @RequestParam(required = false) Integer status) {
        return indicatorViewPhysicalExamBiz.pageRs(pageNo,pageSize,order,asc, appId,indicatorFuncId,name,indicatorCategoryId,status);
    }
}