package org.dows.hep.rest.base.indicator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorViewMonitorFollowupRequestRs;
import org.dows.hep.api.base.indicator.request.IndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.base.indicator.response.IndicatorViewMonitorFollowupResponse;
import org.dows.hep.api.base.indicator.response.IndicatorViewMonitorFollowupResponseRs;
import org.dows.hep.api.constant.RsPageConstant;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.base.indicator.IndicatorViewMonitorFollowupBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标监测随访类
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "查看指标监测随访类", description = "查看指标监测随访类")
public class IndicatorViewMonitorFollowupRest {
    private final IndicatorViewMonitorFollowupBiz indicatorViewMonitorFollowupBiz;

    /**
     * Rs创建查看指标监测随访类
     * @param
     * @return
     */
    @Operation(summary = "Rs创建查看指标监测随访类")
    @PostMapping("v1/baseIndicator/indicatorViewMonitorFollowup/createOrUpdateRs")
    public void createOrUpdateRs(@RequestBody @Validated CreateOrUpdateIndicatorViewMonitorFollowupRequestRs createOrUpdateIndicatorViewMonitorFollowupRequestRs) throws InterruptedException {
        indicatorViewMonitorFollowupBiz.createOrUpdateRs(createOrUpdateIndicatorViewMonitorFollowupRequestRs);
    }

    /**
     * 删除指标监测随访类
     * @param
     * @return
     */
    @Operation(summary = "删除指标监测随访类")
    @DeleteMapping("v1/baseIndicator/indicatorViewMonitorFollowup/deleteIndicatorViewMonitorFollowup")
    public void deleteIndicatorViewMonitorFollowup(@Validated String indicatorViewMonitorFollowupId) throws InterruptedException {
        indicatorViewMonitorFollowupBiz.deleteIndicatorViewMonitorFollowup(indicatorViewMonitorFollowupId);
    }

    @Operation(summary = "Rs批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorViewMonitorFollowup/batchDeleteRs")
    public void batchDeleteRs(@RequestBody @Validated List<String> indicatorViewMonitorFollowupIdList) {
        indicatorViewMonitorFollowupBiz.batchDeleteRs(indicatorViewMonitorFollowupIdList);
    }

    @Operation(summary = "Rs更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorViewMonitorFollowup/updateStatusRs")
    public void updateStatusRs(
        @RequestParam String indicatorViewMonitorFollowupId,
        @RequestParam Integer status) {
        indicatorViewMonitorFollowupBiz.updateStatusRs(indicatorViewMonitorFollowupId, status);
    }

    @Operation(summary = "Rs获取查看指标监测随访类")
    @GetMapping("v1/baseIndicator/indicatorViewMonitorFollowup/getRs")
    public IndicatorViewMonitorFollowupResponseRs getRs(@Validated String indicatorViewMonitorFollowupId) {
        return indicatorViewMonitorFollowupBiz.getRs(indicatorViewMonitorFollowupId);
    }

    @Operation(summary = "Rs分页筛选查看指标监测随访类")
    @GetMapping("v1/baseIndicator/indicatorViewMonitorFollowup/pageRs")
    public Page<IndicatorViewMonitorFollowupResponseRs> pageRs(
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_NO) Long pageNo,
        @RequestParam(required = false, defaultValue = RsPageConstant.PAGE_SIZE) Long pageSize,
        @RequestParam(required = false, defaultValue = RsPageConstant.ORDER) String order,
        @RequestParam(required = false, defaultValue = RsPageConstant.ASC) Boolean asc,
        @RequestParam(required = false) String appId,
        @RequestParam(required = false) String indicatorFuncId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String indicatorCategoryId,
        @RequestParam(required = false) Integer status) {
        return indicatorViewMonitorFollowupBiz.pageRs(pageNo,pageSize,order,asc, appId,indicatorFuncId,name,indicatorCategoryId,status);
    }

//    /**
//    * 创建查看指标监测随访类
//    * @param
//    * @return
//    */
//    @Operation(summary = "创建查看指标监测随访类")
//    @PostMapping("v1/baseIndicator/indicatorViewMonitorFollowup/createIndicatorViewMonitorFollowup")
//    public void createIndicatorViewMonitorFollowup(@RequestBody @Validated CreateIndicatorViewMonitorFollowupRequest createIndicatorViewMonitorFollowup ) {
//        indicatorViewMonitorFollowupBiz.createIndicatorViewMonitorFollowup(createIndicatorViewMonitorFollowup);
//    }
//
//    /**
//    * 批量删除
//    * @param
//    * @return
//    */
//    @Operation(summary = "批量删除")
//    @DeleteMapping("v1/baseIndicator/indicatorViewMonitorFollowup/batchDelete")
//    public void batchDelete(@Validated String string ) {
//        indicatorViewMonitorFollowupBiz.batchDelete(string);
//    }
//
//    /**
//    * 更改启用状态
//    * @param
//    * @return
//    */
//    @Operation(summary = "更改启用状态")
//    @PutMapping("v1/baseIndicator/indicatorViewMonitorFollowup/updateStatus")
//    public void updateStatus(@Validated IndicatorViewMonitorFollowupRequest indicatorViewMonitorFollowup ) {
//        indicatorViewMonitorFollowupBiz.updateStatus(indicatorViewMonitorFollowup);
//    }
//
//    /**
//    * 更新查看指标监测随访类
//    * @param
//    * @return
//    */
//    @Operation(summary = "更新查看指标监测随访类")
//    @PutMapping("v1/baseIndicator/indicatorViewMonitorFollowup/updateIndicatorViewMonitorFollowup")
//    public void updateIndicatorViewMonitorFollowup(@Validated UpdateIndicatorViewMonitorFollowupRequest updateIndicatorViewMonitorFollowup ) {
//        indicatorViewMonitorFollowupBiz.updateIndicatorViewMonitorFollowup(updateIndicatorViewMonitorFollowup);
//    }
//
//    /**
//    * 获取查看指标监测随访类
//    * @param
//    * @return
//     *
//     *
//    */
//    @Operation(summary = "获取查看指标监测随访类")
//    @GetMapping("v1/baseIndicator/indicatorViewMonitorFollowup/getIndicatorViewMonitorFollowup")
//    public IndicatorViewMonitorFollowupResponse getIndicatorViewMonitorFollowup(@Validated String indicatorViewMonitorFollowupId) {
//        return indicatorViewMonitorFollowupBiz.getIndicatorViewMonitorFollowup(indicatorViewMonitorFollowupId);
//    }
//
//    /**
//    * 筛选查看指标监测随访类
//    * @param
//    * @return
//    */
//    @Operation(summary = "筛选查看指标监测随访类")
//    @GetMapping("v1/baseIndicator/indicatorViewMonitorFollowup/listIndicatorViewMonitorFollowup")
//    public List<IndicatorViewMonitorFollowupResponse> listIndicatorViewMonitorFollowup(@Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated Integer type, @Validated Integer status) {
//        return indicatorViewMonitorFollowupBiz.listIndicatorViewMonitorFollowup(appId,indicatorCategoryId,name,type,status);
//    }
//
//    /**
//    * 分页筛选查看指标监测随访类
//    * @param
//    * @return
//    */
//    @Operation(summary = "分页筛选查看指标监测随访类")
//    @GetMapping("v1/baseIndicator/indicatorViewMonitorFollowup/pageIndicatorViewMonitorFollowup")
//    public String pageIndicatorViewMonitorFollowup(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated Integer type, @Validated Integer status) {
//        return indicatorViewMonitorFollowupBiz.pageIndicatorViewMonitorFollowup(pageNo,pageSize,appId,indicatorCategoryId,name,type,status);
//    }
}