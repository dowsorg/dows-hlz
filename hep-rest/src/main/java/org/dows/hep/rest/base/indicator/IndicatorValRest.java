package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorValRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorValRequest;
import org.dows.hep.api.base.indicator.response.IndicatorValResponse;
import org.dows.hep.biz.base.indicator.IndicatorValBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:指标:指标值
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标值", description = "指标值")
public class IndicatorValRest {
    private final IndicatorValBiz indicatorValBiz;

    /**
    * 创建指标值
    * @param
    * @return
    */
    @Operation(summary = "创建指标值")
    @PostMapping("v1/baseIndicator/indicatorVal/createIndicatorVal")
    public void createIndicatorVal(@RequestBody @Validated CreateIndicatorValRequest createIndicatorVal ) {
        indicatorValBiz.createIndicatorVal(createIndicatorVal);
    }

    /**
    * 删除指标值
    * @param
    * @return
    */
    @Operation(summary = "删除指标值")
    @DeleteMapping("v1/baseIndicator/indicatorVal/deleteIndicatorVal")
    public void deleteIndicatorVal(@Validated String indicatorValId ) {
        indicatorValBiz.deleteIndicatorVal(indicatorValId);
    }

    /**
    * 更新指标值
    * @param
    * @return
    */
    @Operation(summary = "更新指标值")
    @PutMapping("v1/baseIndicator/indicatorVal/updateIndicatorVal")
    public void updateIndicatorVal(@Validated UpdateIndicatorValRequest updateIndicatorVal ) {
        indicatorValBiz.updateIndicatorVal(updateIndicatorVal);
    }

    /**
    * 获取指标值
    * @param
    * @return
    */
    @Operation(summary = "获取指标值")
    @GetMapping("v1/baseIndicator/indicatorVal/indicatorVal")
    public IndicatorValResponse indicatorVal(@Validated String indicatorValId) {
        return indicatorValBiz.indicatorVal(indicatorValId);
    }


}