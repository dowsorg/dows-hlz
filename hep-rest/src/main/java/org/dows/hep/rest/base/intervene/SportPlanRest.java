package org.dows.hep.rest.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.SportPlanInfoResponse;
import org.dows.hep.api.base.intervene.response.SportPlanResponse;
import org.dows.hep.biz.base.intervene.SportPlanBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:干预:运动方案
*
* @folder admin-hep/运动方案
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "运动方案", description = "运动方案")
public class SportPlanRest {
    private final SportPlanBiz sportPlanBiz;

    /**
    * 获取运动方案列表
    * @param
    * @return
    */
    @Operation(summary = "获取运动方案列表")
    @PostMapping("v1/baseIntervene/sportPlan/pageSportPlan")
    public Page<SportPlanResponse> pageSportPlan(@RequestBody @Validated FindSportRequest findSport ) {
        return sportPlanBiz.pageSportPlan(findSport);
    }

    /**
    * 获取运动方案信息
    * @param
    * @return
    */
    @Operation(summary = "获取运动方案信息")
    @GetMapping("v1/baseIntervene/sportPlan/getSportPlan")
    public SportPlanInfoResponse getSportPlan(@Validated String sportPlanId) {
        return sportPlanBiz.getSportPlan(sportPlanId);
    }

    /**
    * 保存运动方案
    * @param
    * @return
    */
    @Operation(summary = "保存运动方案")
    @PostMapping("v1/baseIntervene/sportPlan/saveSportPlan")
    public Boolean saveSportPlan(@RequestBody @Validated SaveSportPlanRequest saveSportPlan ) {
        return sportPlanBiz.saveSportPlan(saveSportPlan);
    }

    /**
    * 删除运动方案
    * @param
    * @return
    */
    @Operation(summary = "删除运动方案")
    @DeleteMapping("v1/baseIntervene/sportPlan/delSportPlan")
    public Boolean delSportPlan(@RequestBody @Validated DelSportPlanRequest delSportPlan ) {
        return sportPlanBiz.delSportPlan(delSportPlan);
    }

    /**
     * 删除运动项目
     * @param delRefItem
     * @return
     */
    @Operation(summary = "删除运动项目")
    @DeleteMapping("v1/baseIntervene/sportPlan/delRefItem")
    public Boolean delRefItem(@RequestBody @Validated DelRefItemRequest delRefItem ) {
        return sportPlanBiz.delRefItem(delRefItem);
    }

    /**
    * 启用、禁用运动方案
    * @param
    * @return
    */
    @Operation(summary = "启用、禁用运动方案")
    @PostMapping("v1/baseIntervene/sportPlan/setSportPlanState")
    public Boolean setSportPlanState(@RequestBody @Validated SetSpotPlanStateRequest setSpotPlanState ) {
        return sportPlanBiz.setSportPlanState(setSpotPlanState);
    }


}