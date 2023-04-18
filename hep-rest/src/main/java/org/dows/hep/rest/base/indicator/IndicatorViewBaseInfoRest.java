package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorViewBaseInfoRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorViewBaseInfoRequest;
import org.dows.hep.api.base.indicator.response.IndicatorViewBaseInfoResponse;
import org.dows.hep.biz.base.indicator.IndicatorViewBaseInfoBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标基本信息类
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
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
    * 批量删除
    * @param
    * @return
    */
    @Operation(summary = "批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorViewBaseInfo/batchDelete")
    public void batchDelete(@Validated String string ) {
        indicatorViewBaseInfoBiz.batchDelete(string);
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

    /**
    * 筛选查看指标基本信息类
    * @param
    * @return
    */
    @Operation(summary = "筛选查看指标基本信息类")
    @GetMapping("v1/baseIndicator/indicatorViewBaseInfo/listIndicatorViewBaseInfo")
    public List<IndicatorViewBaseInfoResponse> listIndicatorViewBaseInfo(@Validated String appId, @Validated String indicatorCategoryId, @Validated String name) {
        return indicatorViewBaseInfoBiz.listIndicatorViewBaseInfo(appId,indicatorCategoryId,name);
    }

    /**
    * 分页筛选查看指标基本信息类
    * @param
    * @return
    */
    @Operation(summary = "分页筛选查看指标基本信息类")
    @GetMapping("v1/baseIndicator/indicatorViewBaseInfo/pageIndicatorViewBaseInfo")
    public String pageIndicatorViewBaseInfo(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorCategoryId, @Validated String name) {
        return indicatorViewBaseInfoBiz.pageIndicatorViewBaseInfo(pageNo,pageSize,appId,indicatorCategoryId,name);
    }


}