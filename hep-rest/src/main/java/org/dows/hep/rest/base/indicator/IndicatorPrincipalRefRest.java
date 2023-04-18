package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorPrincipalRefRequest;
import org.dows.hep.api.base.indicator.response.IndicatorPrincipalRefResponse;
import org.dows.hep.biz.base.indicator.IndicatorPrincipalRefBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:指标:指标主体关联关系
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标主体关联关系", description = "指标主体关联关系")
public class IndicatorPrincipalRefRest {
    private final IndicatorPrincipalRefBiz indicatorPrincipalRefBiz;

    /**
    * 创建指标主体关联关系
    * @param
    * @return
    */
    @Operation(summary = "创建指标主体关联关系")
    @PostMapping("v1/baseIndicator/indicatorPrincipalRef/createIndicatorPrincipalRef")
    public void createIndicatorPrincipalRef(@RequestBody @Validated CreateIndicatorPrincipalRefRequest createIndicatorPrincipalRef ) {
        indicatorPrincipalRefBiz.createIndicatorPrincipalRef(createIndicatorPrincipalRef);
    }

    /**
    * 删除指标主体关联关系
    * @param
    * @return
    */
    @Operation(summary = "删除指标主体关联关系")
    @DeleteMapping("v1/baseIndicator/indicatorPrincipalRef/deleteIndicatorPrincipalRef")
    public void deleteIndicatorPrincipalRef(@Validated String indicatorPrincipalRefId ) {
        indicatorPrincipalRefBiz.deleteIndicatorPrincipalRef(indicatorPrincipalRefId);
    }

    /**
    * 查询指标主体关联关系
    * @param
    * @return
    */
    @Operation(summary = "查询指标主体关联关系")
    @GetMapping("v1/baseIndicator/indicatorPrincipalRef/getIndicatorPrincipalRef")
    public IndicatorPrincipalRefResponse getIndicatorPrincipalRef(@Validated String indicatorPrincipalRefId) {
        return indicatorPrincipalRefBiz.getIndicatorPrincipalRef(indicatorPrincipalRefId);
    }


}