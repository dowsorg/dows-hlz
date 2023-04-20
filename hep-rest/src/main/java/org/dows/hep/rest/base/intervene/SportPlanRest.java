package org.dows.hep.rest.base.intervene;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.DelSportPlanRequest;
import org.dows.hep.api.base.intervene.request.FindSportRequest;
import org.dows.hep.api.base.intervene.request.SaveSportPlanRequest;
import org.dows.hep.api.base.intervene.request.SetSpotPlanStateRequest;
import org.dows.hep.api.base.intervene.response.SportPlanInfoResponse;
import org.dows.hep.api.base.intervene.response.SportPlanResponse;
import org.dows.hep.biz.base.intervene.SportPlanBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:干预:运动方案
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
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
    public SportPlanResponse pageSportPlan(@RequestBody @Validated FindSportRequest findSport ) {
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
    public Boolean delSportPlan(@Validated DelSportPlanRequest delSportPlan ) {
        return sportPlanBiz.delSportPlan(delSportPlan);
    }

    /**
    * 启用、禁用运动方案
    * @param
    * @return
    */
    @Operation(summary = "启用、禁用运动方案")
    @PostMapping("v1/baseIntervene/sportPlan/setSpotPlanState")
    public Boolean setSpotPlanState(@RequestBody @Validated SetSpotPlanStateRequest setSpotPlanState ) {
        return sportPlanBiz.setSpotPlanState(setSpotPlanState);
    }


}