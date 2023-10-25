package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.annotation.Resubmit;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceCategoryResponseRs;
import org.dows.hep.biz.base.indicator.IndicatorInstanceBiz;
import org.dows.hep.biz.eval.sync.SyncPersonBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

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

    private final SyncPersonBiz syncPersonBiz;

    @Resubmit(duration = 2)
    @Operation(summary = "创建或修改指标实例")
    @PostMapping("v1/baseIndicator/indicatorInstance/createOrUpdateRs")
    public void createOrUpdateRs(@RequestBody CreateOrUpdateIndicatorInstanceRequestRs createOrUpdateIndicatorInstanceRequestRs) throws InterruptedException {
        indicatorInstanceBiz.createOrUpdateRs(createOrUpdateIndicatorInstanceRequestRs);
    }

    @Operation(summary = "删除指标")
    @DeleteMapping("v1/baseIndicator/indicatorInstance/delete")
    public void delete(@RequestParam String indicatorInstanceId) throws InterruptedException, ExecutionException {
        indicatorInstanceBiz.delete(indicatorInstanceId);
    }

    @Operation(summary = "批量设置关键指标")
    @PutMapping("v1/baseIndicator/indicatorInstance/batchUpdateCore")
    public void batchUpdateCore(@RequestBody BatchUpdateCoreRequestRs batchUpdateCoreRequestRs) throws InterruptedException {
        indicatorInstanceBiz.batchUpdateCore(batchUpdateCoreRequestRs);
    }

    @Operation(summary = "批量设置饮食关键指标")
    @PutMapping("v1/baseIndicator/indicatorInstance/batchUpdateFood")
    public void batchUpdateFood(@RequestBody BatchUpdateFoodRequestRs batchUpdateFoodRequestRs) throws InterruptedException {
        indicatorInstanceBiz.batchUpdateFood(batchUpdateFoodRequestRs);
    }

    @Operation(summary = "上移下移功能")
    @PostMapping("v1/baseIndicator/indicatorInstance/move")
    public void move(@RequestBody UpdateIndicatorInstanceMoveRequestRs updateIndicatorInstanceMoveRequestRs) throws InterruptedException {
        indicatorInstanceBiz.move(updateIndicatorInstanceMoveRequestRs);
    }

    @Operation(summary = "根据appId查询出所有的指标")
    @GetMapping("v1/baseIndicator/indicatorInstance/getByAppId")
    public List<IndicatorInstanceCategoryResponseRs> getByAppId(@RequestParam String appId) {
        return indicatorInstanceBiz.getByAppId(appId);
    }

    @Operation(summary = "一键同步")
    @PostMapping("v1/baseIndicator/indicatorInstance/syncAll")
    public Boolean syncAll(@RequestBody @Validated SyncIndicatorRequest req) {
        return syncPersonBiz.syncAllPerson(req);
    }
}