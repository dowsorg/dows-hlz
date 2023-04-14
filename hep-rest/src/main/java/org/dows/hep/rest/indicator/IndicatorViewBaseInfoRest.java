package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorViewBaseInfoRequest;
import org.dows.hep.api.indicator.request.IndicatorViewBaseInfoIdRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorViewBaseInfoRequest;
import org.dows.hep.api.indicator.request.IndicatorViewBaseInfoIdRequest;
import org.dows.hep.api.indicator.response.IndicatorViewBaseInfoResponse;
import org.dows.hep.biz.indicator.IndicatorViewBaseInfoBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标基本信息类
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "查看指标基本信息类")
public class IndicatorViewBaseInfoRest {
    private final IndicatorViewBaseInfoBiz indicatorViewBaseInfoBiz;

    /**
    * 创建指标基本信息类
    * @param
    * @return
    */
    @ApiOperation("创建指标基本信息类")
    @PostMapping("v1/indicator/indicatorViewBaseInfo/createIndicatorViewBaseInfo")
    public void createIndicatorViewBaseInfo(@RequestBody @Validated CreateIndicatorViewBaseInfoRequest createIndicatorViewBaseInfo ) {
        indicatorViewBaseInfoBiz.createIndicatorViewBaseInfo(createIndicatorViewBaseInfo);
    }

    /**
    * 删除指标基本信息类
    * @param
    * @return
    */
    @ApiOperation("删除指标基本信息类")
    @DeleteMapping("v1/indicator/indicatorViewBaseInfo/deleteIndicatorViewBaseInfo")
    public void deleteIndicatorViewBaseInfo(@Validated IndicatorViewBaseInfoIdRequest indicatorViewBaseInfoId ) {
        indicatorViewBaseInfoBiz.deleteIndicatorViewBaseInfo(indicatorViewBaseInfoId);
    }

    /**
    * 更改指标基本信息类
    * @param
    * @return
    */
    @ApiOperation("更改指标基本信息类")
    @PutMapping("v1/indicator/indicatorViewBaseInfo/updateIndicatorViewBaseInfo")
    public void updateIndicatorViewBaseInfo(@Validated UpdateIndicatorViewBaseInfoRequest updateIndicatorViewBaseInfo ) {
        indicatorViewBaseInfoBiz.updateIndicatorViewBaseInfo(updateIndicatorViewBaseInfo);
    }

    /**
    * 获取查看指标基本信息类
    * @param
    * @return
    */
    @ApiOperation("获取查看指标基本信息类")
    @GetMapping("v1/indicator/indicatorViewBaseInfo/getIndicatorViewBaseInfo")
    public IndicatorViewBaseInfoResponse getIndicatorViewBaseInfo(@Validated IndicatorViewBaseInfoIdRequest indicatorViewBaseInfoId) {
        return indicatorViewBaseInfoBiz.getIndicatorViewBaseInfo(indicatorViewBaseInfoId);
    }


}