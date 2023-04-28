package org.dows.hep.rest.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.DelSpotItemRequest;
import org.dows.hep.api.base.intervene.request.FindSportRequest;
import org.dows.hep.api.base.intervene.request.SaveSportItemRequest;
import org.dows.hep.api.base.intervene.request.SetSportItemStateRequest;
import org.dows.hep.api.base.intervene.response.SportItemInfoResponse;
import org.dows.hep.api.base.intervene.response.SportItemResponse;
import org.dows.hep.biz.base.intervene.SportItemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:干预:运动项目
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "运动项目", description = "运动项目")
public class SportItemRest {
    private final SportItemBiz sportItemBiz;

    /**
    * 获取运动项目列表
    * @param
    * @return
    */
    @Operation(summary = "获取运动项目列表")
    @PostMapping("v1/baseIntervene/sportItem/pageSportItem")
    public Page<SportItemResponse> pageSportItem(@RequestBody @Validated FindSportRequest findSport ) {
        return sportItemBiz.pageSportItem(findSport);
    }

    /**
    * 获取运动项目详细信息
    * @param
    * @return
    */
    @Operation(summary = "获取运动项目详细信息")
    @GetMapping("v1/baseIntervene/sportItem/getSportItem")
    public SportItemInfoResponse getSportItem(@Validated String sportItemId) {
        return sportItemBiz.getSportItem(sportItemId);
    }

    /**
    * 保存运动项目
    * @param
    * @return
    */
    @Operation(summary = "保存运动项目")
    @PostMapping("v1/baseIntervene/sportItem/saveSportItem")
    public Boolean saveSportItem(@RequestBody @Validated SaveSportItemRequest saveSportItem ) {
        return sportItemBiz.saveSportItem(saveSportItem);
    }

    /**
     * 启用禁用运动项目
     *
     * @param
     * @return
     */
    @Operation(summary = "启用禁用运动项目")
    @PostMapping("v1/baseIntervene/sportItem/setSportItemState")
    public Boolean setSportItemState(@RequestBody @Validated SetSportItemStateRequest setSportItemStateRequest ) {
        return sportItemBiz.setSportItemState(setSportItemStateRequest);
    }

    /**
    * 删除运动项目
    * @param
    * @return
    */
    @Operation(summary = "删除运动项目")
    @DeleteMapping("v1/baseIntervene/sportItem/delSportItem")
    public Boolean delSportItem(@RequestBody @Validated DelSpotItemRequest delSportItem ) {
        return sportItemBiz.delSportItem(delSportItem);
    }


}