package org.dows.hep.rest.user.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.user.indicator.IndicatorSummaryBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:指标:指标汇总
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标汇总", description = "指标汇总")
public class IndicatorSummaryRest {
    private final IndicatorSummaryBiz indicatorSummaryBiz;

    /**
    * 指标汇总
    * @param
    * @return
    */
    @Operation(summary = "指标汇总")
    @GetMapping("v1/userindicator/indicatorSummary/getIndicatorSummary")
    public String getIndicatorSummary(@Validated String experimentPsersonId) {
        return indicatorSummaryBiz.getIndicatorSummary(experimentPsersonId);
    }


}