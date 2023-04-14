package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorViewBaseInfoRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorViewBaseInfoRequest;
import org.dows.hep.api.base.indicator.response.IndicatorViewBaseInfoResponse;
import org.dows.hep.biz.base.indicator.IndicatorViewBaseInfoBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标基本信息类
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "查看指标基本信息类", description = "查看指标基本信息类")
public class IndicatorViewBaseInfoRest {
    private final IndicatorViewBaseInfoBiz indicatorViewBaseInfoBiz;

    /**
    * 创建指标基本信息类
    * @param
    * @return
    */
    @Operation(summary = "创建指标基本信息类")
    @PostMapping("v1/baseIndicator/indicatorViewBaseInfo/createIndicatorViewBaseInfo")
    public void createIndicatorViewBaseInfo(@RequestBody @Validated CreateIndicatorViewBaseInfoRequest createIndicatorViewBaseInfo ) {
        indicatorViewBaseInfoBiz.createIndicatorViewBaseInfo(createIndicatorViewBaseInfo);
    }

    /**
    * 删除指标基本信息类
    * @param
    * @return
    */
    @Operation(summary = "删除指标基本信息类")
    @DeleteMapping("v1/baseIndicator/indicatorViewBaseInfo/deleteIndicatorViewBaseInfo")
    public void deleteIndicatorViewBaseInfo(@Validated String indicatorViewBaseInfoId ) {
        indicatorViewBaseInfoBiz.deleteIndicatorViewBaseInfo(indicatorViewBaseInfoId);
    }

    /**
    * 更改指标基本信息类
    * @param
    * @return
    */
    @Operation(summary = "更改指标基本信息类")
    @PutMapping("v1/baseIndicator/indicatorViewBaseInfo/updateIndicatorViewBaseInfo")
    public void updateIndicatorViewBaseInfo(@Validated UpdateIndicatorViewBaseInfoRequest updateIndicatorViewBaseInfo ) {
        indicatorViewBaseInfoBiz.updateIndicatorViewBaseInfo(updateIndicatorViewBaseInfo);
    }

    /**
    * 获取查看指标基本信息类
    * @param
    * @return
    */
    @Operation(summary = "获取查看指标基本信息类")
    @GetMapping("v1/baseIndicator/indicatorViewBaseInfo/getIndicatorViewBaseInfo")
    public IndicatorViewBaseInfoResponse getIndicatorViewBaseInfo(@Validated String indicatorViewBaseInfoId) {
        return indicatorViewBaseInfoBiz.getIndicatorViewBaseInfo(indicatorViewBaseInfoId);
    }


}