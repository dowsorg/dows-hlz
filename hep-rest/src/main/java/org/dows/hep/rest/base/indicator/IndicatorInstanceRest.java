package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorInstanceRequestRs;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceCategoryResponseRs;
import org.dows.hep.biz.base.indicator.IndicatorInstanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标实例
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标实例", description = "指标实例")
public class IndicatorInstanceRest {
    private final IndicatorInstanceBiz indicatorInstanceBiz;

    @Operation(summary = "创建或修改指标实例")
    @PostMapping("v1/baseIndicator/indicatorInstance/createOrUpdateRs")
    public void createOrUpdateRs(@RequestBody @Validated CreateOrUpdateIndicatorInstanceRequestRs createOrUpdateIndicatorInstanceRequestRs) throws InterruptedException {
        indicatorInstanceBiz.createOrUpdateRs(createOrUpdateIndicatorInstanceRequestRs);
    }

    @Operation(summary = "删除指标")
    @DeleteMapping("v1/baseIndicator/indicatorInstance/delete")
    public void delete(
        @RequestParam @Validated String indicatorInstanceId) throws InterruptedException {
        indicatorInstanceBiz.delete(indicatorInstanceId);
    }

    @Operation(summary = "批量设置关键指标")
    @PutMapping("v1/baseIndicator/indicatorInstance/batchUpdateCore")
    public void batchUpdateCore(@RequestBody List<String> indicatorInstanceIdList) {
        indicatorInstanceBiz.batchUpdateCore(indicatorInstanceIdList);
    }

    @Operation(summary = "批量设置饮食关键指标")
    @PutMapping("v1/baseIndicator/indicatorInstance/batchUpdateFood")
    public void batchUpdateFood(@RequestBody List<String> indicatorInstanceIdList) {
        indicatorInstanceBiz.batchUpdateFood(indicatorInstanceIdList);
    }

    @Operation(summary = "根据appId查询出所有的指标")
    @GetMapping("v1/baseIndicator/indicatorInstance/getByAppId")
    public List<IndicatorInstanceCategoryResponseRs> getByAppId(@RequestParam String appId) {
        return indicatorInstanceBiz.getByAppId(appId);
    }

//    @Operation(summary = "删除指标")
//    @DeleteMapping("v1/baseIndicator/indicatorInstance/deleteIndicatorInstance")
//    public void deleteIndicatorInstance(
//        @RequestParam @Validated String indicatorInstanceId) throws InterruptedException {
//        indicatorInstanceBiz.delete(indicatorInstanceId);
//    }

//    /**
//    * 更新指标
//    * @param
//    * @return
//    */
//    @Operation(summary = "更新指标")
//    @PutMapping("v1/baseIndicator/indicatorInstance/updateIndicatorInstance")
//    public void updateIndicatorInstance(@Validated UpdateIndicatorInstanceRequest updateIndicatorInstance ) {
//        indicatorInstanceBiz.updateIndicatorInstance(updateIndicatorInstance);
//    }

//    /**
//    * 批量更新指标
//    * @param
//    * @return
//    */
//    @Operation(summary = "批量更新指标")
//    @PutMapping("v1/baseIndicator/indicatorInstance/batchUpdateIndicatorInstance")
//    public void batchUpdateIndicatorInstance(@Validated List<UpdateIndicatorInstanceRequest> updateIndicatorInstance ) {
//        indicatorInstanceBiz.batchUpdateIndicatorInstance(updateIndicatorInstance);
//    }

//    /**
//    * 查询指标
//    * @param
//    * @return
//    */
//    @Operation(summary = "查询指标")
//    @GetMapping("v1/baseIndicator/indicatorInstance/getIndicatorInstance")
//    public IndicatorInstanceResponse getIndicatorInstance(@Validated String indicatorInstanceId) {
//        return indicatorInstanceBiz.getIndicatorInstance(indicatorInstanceId);
//    }

//    /**
//    * 筛选指标
//    * @param
//    * @return
//    */
//    @Operation(summary = "筛选指标")
//    @GetMapping("v1/baseIndicator/indicatorInstance/listIndicatorInstance")
//    public List<IndicatorInstanceResponse> listIndicatorInstance(@Validated String appId, @Validated Integer core, @Validated Integer food, @Validated String indicatorCategoryId) {
//        return indicatorInstanceBiz.listIndicatorInstance(appId,core,food,indicatorCategoryId);
//    }

//    /**
//    * 分页筛选指标
//    * @param
//    * @return
//    */
//    @Operation(summary = "分页筛选指标")
//    @GetMapping("v1/baseIndicator/indicatorInstance/pageIndicatorInstance")
//    public String pageIndicatorInstance(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated Integer core, @Validated Integer food, @Validated String indicatorCategoryId) {
//        return indicatorInstanceBiz.pageIndicatorInstance(pageNo,pageSize,appId,core,food,indicatorCategoryId);
//    }


}