package org.dows.hep.rest.intervene;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindSportRequest;
import org.dows.hep.api.intervene.response.SportItemResponse;
import org.dows.hep.api.intervene.response.SportItemInfoResponse;
import org.dows.hep.api.intervene.request.SaveSportItemRequest;
import org.dows.hep.api.intervene.request.DelSpotItemRequest;
import org.dows.hep.biz.intervene.SportItemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:运动项目
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "运动项目")
public class SportItemRest {
    private final SportItemBiz sportItemBiz;

    /**
    * 获取运动项目列表
    * @param
    * @return
    */
    @ApiOperation("获取运动项目列表")
    @PostMapping("v1/intervene/sportItem/pageSportItem")
    public SportItemResponse pageSportItem(@RequestBody @Validated FindSportRequest findSport ) {
        return sportItemBiz.pageSportItem(findSport);
    }

    /**
    * 获取运动项目详细信息
    * @param
    * @return
    */
    @ApiOperation("获取运动项目详细信息")
    @GetMapping("v1/intervene/sportItem/getSportItem")
    public SportItemInfoResponse getSportItem(@Validated String sportItemId) {
        return sportItemBiz.getSportItem(sportItemId);
    }

    /**
    * 保存运动项目
    * @param
    * @return
    */
    @ApiOperation("保存运动项目")
    @PostMapping("v1/intervene/sportItem/saveSportItem")
    public Boolean saveSportItem(@RequestBody @Validated SaveSportItemRequest saveSportItem ) {
        return sportItemBiz.saveSportItem(saveSportItem);
    }

    /**
    * 删除运动项目
    * @param
    * @return
    */
    @ApiOperation("删除运动项目")
    @DeleteMapping("v1/intervene/sportItem/delSportItem")
    public Boolean delSportItem(@Validated DelSpotItemRequest delSpotItem ) {
        return sportItemBiz.delSportItem(delSpotItem);
    }


}