package org.dows.hep.rest.base.indicator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorViewSupportExamResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorViewSupportExamResponse;
import org.dows.hep.api.constant.RsPageConstant;
import org.dows.hep.biz.base.indicator.IndicatorViewSupportExamBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标辅助检查类
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "查看指标辅助检查类", description = "查看指标辅助检查类")
public class IndicatorViewSupportExamRest {
    private final IndicatorViewSupportExamBiz indicatorViewSupportExamBiz;

    @Operation(summary = "Rs创建或保存查看指标辅助检查类")
    @PostMapping("v1/baseIndicator/indicatorViewSupportExam/createOrUpdateRs")
    public void createOrUpdateRs(@RequestBody @Validated CreateOrUpdateIndicatorViewSupportExamRequestRs createOrUpdateIndicatorViewSupportExamRequestRs) {
        indicatorViewSupportExamBiz.createOrUpdateRs(createOrUpdateIndicatorViewSupportExamRequestRs);
    }

    @Operation(summary = "Rs批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorViewSupportExam/batchDeleteRs")
    public void batchDeleteRs(@RequestBody List<String> indicatorViewSupportExamIdList) {
        indicatorViewSupportExamBiz.batchDeleteRs(indicatorViewSupportExamIdList);
    }

    @Operation(summary = "Rs更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorViewSupportExam/updateStatusRs")
    public void updateStatusRs(
        @RequestParam String indicatorViewSupportExamId,
        @RequestParam Integer status) {
        indicatorViewSupportExamBiz.updateStatusRs(indicatorViewSupportExamId, status);
    }

    @Operation(summary = "Rs获取查看指标辅助检查类")
    @GetMapping("v1/baseIndicator/indicatorViewSupportExam/getRs")
    public IndicatorViewSupportExamResponseRs getRs(@RequestParam @Validated String indicatorViewSupportExamId) {
        return indicatorViewSupportExamBiz.getRs(indicatorViewSupportExamId);
    }

    @Operation(summary = "Rs分页筛选查看指标辅助检查类")
    @GetMapping("v1/baseIndicator/indicatorViewSupportExam/pageRs")
    public IPage<IndicatorViewSupportExamResponseRs> pageRs(
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_NO) Long pageNo,
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_SIZE) Long pageSize,
        @RequestParam(required = false, defaultValue = RsPageConstant.ORDER) String order,
        @RequestParam(required = false, defaultValue = RsPageConstant.ASC) Boolean asc,
        @RequestParam(required = false) String appId,
        @RequestParam(required = false) String indicatorFuncId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String indicatorCategoryId,
        @RequestParam(required = false) Integer status) {
        return indicatorViewSupportExamBiz.pageRs(pageNo,pageSize,order,asc, appId,indicatorFuncId,name,indicatorCategoryId,status);
    }

//    /**
//    * 创建查看指标辅助检查类
//    * @param
//    * @return
//    */
//    @Operation(summary = "创建查看指标辅助检查类")
//    @PostMapping("v1/baseIndicator/indicatorViewSupportExam/createIndicatorViewSupportExam")
//    public void createIndicatorViewSupportExam(@RequestBody @Validated CreateIndicatorViewSupportExamRequest createIndicatorViewSupportExam ) {
//        indicatorViewSupportExamBiz.createIndicatorViewSupportExam(createIndicatorViewSupportExam);
//    }
//
//    /**
//    * 删除查看指标辅助检查类
//    * @param
//    * @return
//    */
//    @Operation(summary = "删除查看指标辅助检查类")
//    @DeleteMapping("v1/baseIndicator/indicatorViewSupportExam/deleteIndicatorViewSupportExam")
//    public void deleteIndicatorViewSupportExam(@Validated String indicatorViewSupportExamId ) {
//        indicatorViewSupportExamBiz.deleteIndicatorViewSupportExam(indicatorViewSupportExamId);
//    }
//
//    /**
//    * 批量删除
//    * @param
//    * @return
//    */
//    @Operation(summary = "批量删除")
//    @DeleteMapping("v1/baseIndicator/indicatorViewSupportExam/batchDelete")
//    public void batchDelete(@Validated String string ) {
//        indicatorViewSupportExamBiz.batchDelete(string);
//    }
//
//    /**
//    * 更改启用状态
//    * @param
//    * @return
//    */
//    @Operation(summary = "更改启用状态")
//    @PutMapping("v1/baseIndicator/indicatorViewSupportExam/updateStatus")
//    public void updateStatus(@Validated IndicatorViewSupportExamRequest indicatorViewSupportExam ) {
//        indicatorViewSupportExamBiz.updateStatus(indicatorViewSupportExam);
//    }
//
//    /**
//    * 查看指标辅助检查类
//    * @param
//    * @return
//    */
//    @Operation(summary = "查看指标辅助检查类")
//    @PutMapping("v1/baseIndicator/indicatorViewSupportExam/updateIndicatorViewSupportExam")
//    public void updateIndicatorViewSupportExam(@Validated UpdateIndicatorViewSupportExamRequest updateIndicatorViewSupportExam ) {
//        indicatorViewSupportExamBiz.updateIndicatorViewSupportExam(updateIndicatorViewSupportExam);
//    }
//
//    /**
//    * 查看指标辅助检查类
//    * @param
//    * @return
//    */
//    @Operation(summary = "查看指标辅助检查类")
//    @GetMapping("v1/baseIndicator/indicatorViewSupportExam/getIndicatorViewSupportExam")
//    public IndicatorViewSupportExamResponse getIndicatorViewSupportExam(@Validated String indicatorViewSupportExamId) {
//        return indicatorViewSupportExamBiz.getIndicatorViewSupportExam(indicatorViewSupportExamId);
//    }
//
//    /**
//    * 筛选指标辅助检查类
//    * @param
//    * @return
//    */
//    @Operation(summary = "筛选指标辅助检查类")
//    @GetMapping("v1/baseIndicator/indicatorViewSupportExam/listIndicatorViewSupportExam")
//    public List<IndicatorViewSupportExamResponse> listIndicatorViewSupportExam(@Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest fee, @Validated String resultAnalysis, @Validated Integer status) {
//        return indicatorViewSupportExamBiz.listIndicatorViewSupportExam(appId,indicatorCategoryId,name,type,fee,resultAnalysis,status);
//    }
//
//    /**
//    * 分页筛选指标辅助检查类
//    * @param
//    * @return
//    */
//    @Operation(summary = "分页筛选指标辅助检查类")
//    @GetMapping("v1/baseIndicator/indicatorViewSupportExam/pageIndicatorViewSupportExam")
//    public String pageIndicatorViewSupportExam(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest fee, @Validated String resultAnalysis, @Validated Integer status) {
//        return indicatorViewSupportExamBiz.pageIndicatorViewSupportExam(pageNo,pageSize,appId,indicatorCategoryId,name,type,fee,resultAnalysis,status);
//    }


}