package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorVarRequest;
import org.dows.hep.api.base.indicator.request.IndicatorVarIdRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorVarRequest;
import org.dows.hep.api.base.indicator.response.IndicatorVarResponse;
import org.dows.hep.biz.base.indicator.IndicatorVarBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标变量
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标变量", description = "指标变量")
public class IndicatorVarRest {
    private final IndicatorVarBiz indicatorVarBiz;

    /**
    * 创建指标变量
    * @param
    * @return
    */
    @Operation(summary = "创建指标变量")
    @PostMapping("v1/baseIndicator/indicatorVar/createIndicatorVar")
    public void createIndicatorVar(@RequestBody @Validated CreateIndicatorVarRequest createIndicatorVar ) {
        indicatorVarBiz.createIndicatorVar(createIndicatorVar);
    }

    /**
    * 删除指标变量
    * @param
    * @return
    */
    @Operation(summary = "删除指标变量")
    @DeleteMapping("v1/baseIndicator/indicatorVar/deleteIndicatorVar")
    public void deleteIndicatorVar(@Validated IndicatorVarIdRequest indicatorVarId ) {
        indicatorVarBiz.deleteIndicatorVar(indicatorVarId);
    }

    /**
    * 更新指标变量
    * @param
    * @return
    */
    @Operation(summary = "更新指标变量")
    @PutMapping("v1/baseIndicator/indicatorVar/updateIndicatorVar")
    public void updateIndicatorVar(@Validated UpdateIndicatorVarRequest updateIndicatorVar ) {
        indicatorVarBiz.updateIndicatorVar(updateIndicatorVar);
    }

    /**
    * 查询指标变量
    * @param
    * @return
    */
    @Operation(summary = "查询指标变量")
    @GetMapping("v1/baseIndicator/indicatorVar/getIndicatorVar")
    public IndicatorVarResponse getIndicatorVar(@Validated IndicatorVarIdRequest indicatorVarId) {
        return indicatorVarBiz.getIndicatorVar(indicatorVarId);
    }

    /**
    * 筛选指标变量
    * @param
    * @return
    */
    @Operation(summary = "筛选指标变量")
    @GetMapping("v1/baseIndicator/indicatorVar/listIndicatorVar")
    public List<IndicatorVarResponse> listIndicatorVar(@Validated String appId, @Validated String indicatorInstanceId, @Validated String dbName, @Validated String tbName, @Validated String varName, @Validated String varCode, @Validated String periods, @Validated String descr) {
        return indicatorVarBiz.listIndicatorVar(appId,indicatorInstanceId,dbName,tbName,varName,varCode,periods,descr);
    }

    /**
    * 分页筛选指标变量
    * @param
    * @return
    */
    @Operation(summary = "分页筛选指标变量")
    @GetMapping("v1/baseIndicator/indicatorVar/pageIndicatorVar")
    public String pageIndicatorVar(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorInstanceId, @Validated String dbName, @Validated String tbName, @Validated String varName, @Validated String varCode, @Validated String periods, @Validated String descr) {
        return indicatorVarBiz.pageIndicatorVar(pageNo,pageSize,appId,indicatorInstanceId,dbName,tbName,varName,varCode,periods,descr);
    }

    /**
    * 做公式组件
    * @param
    * @return
    */
    @Operation(summary = "做公式组件")
    @PostMapping("v1/baseIndicator/indicatorVar/createExpressionComponent")
    public void createExpressionComponent(@RequestBody @Validated String expressionId ) {
        indicatorVarBiz.createExpressionComponent(expressionId);
    }


}