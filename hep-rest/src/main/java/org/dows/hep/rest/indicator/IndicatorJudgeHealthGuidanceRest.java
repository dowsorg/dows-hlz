package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorJudgeHealthGuidanceRequest;
import org.dows.hep.api.indicator.request.UpdateStatusIndicatorJudgeHealthGuidanceRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorJudgeHealthGuidanceRequest;
import org.dows.hep.api.indicator.response.IndicatorJudgeHealthGuidanceResponse;
import org.dows.hep.biz.indicator.IndicatorJudgeHealthGuidanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标健康指导
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "判断指标健康指导")
public class IndicatorJudgeHealthGuidanceRest {
    private final IndicatorJudgeHealthGuidanceBiz indicatorJudgeHealthGuidanceBiz;

    /**
    * 创建判断指标健康指导
    * @param
    * @return
    */
    @ApiOperation("创建判断指标健康指导")
    @PostMapping("v1/indicator/indicatorJudgeHealthGuidance/createIndicatorJudgeHealthGuidance")
    public void createIndicatorJudgeHealthGuidance(@RequestBody @Validated CreateIndicatorJudgeHealthGuidanceRequest createIndicatorJudgeHealthGuidance ) {
        indicatorJudgeHealthGuidanceBiz.createIndicatorJudgeHealthGuidance(createIndicatorJudgeHealthGuidance);
    }

    /**
    * 删除判断指标健康指导
    * @param
    * @return
    */
    @ApiOperation("删除判断指标健康指导")
    @DeleteMapping("v1/indicator/indicatorJudgeHealthGuidance/deleteIndicatorJudgeHealthGuidance")
    public void deleteIndicatorJudgeHealthGuidance(@Validated String indicatorJudgeHealthGuidanceId ) {
        indicatorJudgeHealthGuidanceBiz.deleteIndicatorJudgeHealthGuidance(indicatorJudgeHealthGuidanceId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @ApiOperation("更改启用状态")
    @PutMapping("v1/indicator/indicatorJudgeHealthGuidance/updateStatus")
    public void updateStatus(@Validated UpdateStatusIndicatorJudgeHealthGuidanceRequest updateStatusIndicatorJudgeHealthGuidance ) {
        indicatorJudgeHealthGuidanceBiz.updateStatus(updateStatusIndicatorJudgeHealthGuidance);
    }

    /**
    * 判断指标健康指导
    * @param
    * @return
    */
    @ApiOperation("判断指标健康指导")
    @PutMapping("v1/indicator/indicatorJudgeHealthGuidance/updateIndicatorJudgeHealthGuidance")
    public void updateIndicatorJudgeHealthGuidance(@Validated UpdateIndicatorJudgeHealthGuidanceRequest updateIndicatorJudgeHealthGuidance ) {
        indicatorJudgeHealthGuidanceBiz.updateIndicatorJudgeHealthGuidance(updateIndicatorJudgeHealthGuidance);
    }

    /**
    * 获取判断指标健康指导
    * @param
    * @return
    */
    @ApiOperation("获取判断指标健康指导")
    @GetMapping("v1/indicator/indicatorJudgeHealthGuidance/getIndicatorJudgeHealthGuidance")
    public IndicatorJudgeHealthGuidanceResponse getIndicatorJudgeHealthGuidance(@Validated String indicatorJudgeHealthGuidanceId) {
        return indicatorJudgeHealthGuidanceBiz.getIndicatorJudgeHealthGuidance(indicatorJudgeHealthGuidanceId);
    }


}