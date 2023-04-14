package org.dows.hep.rest.intervene;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindSportRequest;
import org.dows.hep.api.intervene.response.SportPlanResponse;
import org.dows.hep.api.intervene.response.SportPlanInfoResponse;
import org.dows.hep.api.intervene.request.SaveSportPlanRequest;
import org.dows.hep.api.intervene.request.DelSportPlanRequest;
import org.dows.hep.api.intervene.request.SetSpotPlanStateRequest;
import org.dows.hep.biz.intervene.SportPlanBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:运动方案
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "运动方案")
public class SportPlanRest {
    private final SportPlanBiz sportPlanBiz;

    /**
    * 获取运动方案列表
    * @param
    * @return
    */
    @ApiOperation("获取运动方案列表")
    @PostMapping("v1/intervene/sportPlan/pageSportPlan")
    public SportPlanResponse pageSportPlan(@RequestBody @Validated FindSportRequest findSport ) {
        return sportPlanBiz.pageSportPlan(findSport);
    }

    /**
    * 获取运动方案信息
    * @param
    * @return
    */
    @ApiOperation("获取运动方案信息")
    @GetMapping("v1/intervene/sportPlan/getSportPlan")
    public SportPlanInfoResponse getSportPlan(@Validated String sportPlanId) {
        return sportPlanBiz.getSportPlan(sportPlanId);
    }

    /**
    * 保存运动方案
    * @param
    * @return
    */
    @ApiOperation("保存运动方案")
    @PostMapping("v1/intervene/sportPlan/saveSportPlan")
    public Boolean saveSportPlan(@RequestBody @Validated SaveSportPlanRequest saveSportPlan ) {
        return sportPlanBiz.saveSportPlan(saveSportPlan);
    }

    /**
    * 删除运动方案
    * @param
    * @return
    */
    @ApiOperation("删除运动方案")
    @DeleteMapping("v1/intervene/sportPlan/delSportPlan")
    public Boolean delSportPlan(@Validated DelSportPlanRequest delSportPlan ) {
        return sportPlanBiz.delSportPlan(delSportPlan);
    }

    /**
    * 启用、禁用运动方案
    * @param
    * @return
    */
    @ApiOperation("启用、禁用运动方案")
    @PostMapping("v1/intervene/sportPlan/setSpotPlanState")
    public Boolean setSpotPlanState(@RequestBody @Validated SetSpotPlanStateRequest setSpotPlanState ) {
        return sportPlanBiz.setSpotPlanState(setSpotPlanState);
    }


}