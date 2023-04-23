package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorRefRequest;
import org.dows.hep.api.base.indicator.response.IndicatorRefResponse;
import org.dows.hep.biz.base.indicator.IndicatorRefBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标引用
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标引用", description = "指标引用")
public class IndicatorRefRest {
    private final IndicatorRefBiz indicatorRefBiz;

    /**
    * 创建指标引用
    * @param
    * @return
    */
    @Operation(summary = "创建指标引用")
    @PostMapping("v1/baseIndicator/indicatorRef/createIndicatorRef")
    public void createIndicatorRef(@RequestBody @Validated CreateIndicatorRefRequest createIndicatorRef ) {
        indicatorRefBiz.createIndicatorRef(createIndicatorRef);
    }

    /**
    * 删除指标引用
    * @param
    * @return
    */
    @Operation(summary = "删除指标引用")
    @DeleteMapping("v1/baseIndicator/indicatorRef/deleteIndicatorRef")
    public void deleteIndicatorRef(@Validated String indicatorRefId ) {
        indicatorRefBiz.deleteIndicatorRef(indicatorRefId);
    }

    /**
    * 获取指标引用列表
    * @param
    * @return
    */
    @Operation(summary = "获取指标引用列表")
    @GetMapping("v1/baseIndicator/indicatorRef/listIndicatorRef")
    public List<IndicatorRefResponse> listIndicatorRef(@Validated String indicatorInstanceId) {
        return indicatorRefBiz.listIndicatorRef(indicatorInstanceId);
    }


}