package org.dows.hep.biz.intervene;

import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindSportRequest;
import org.dows.hep.api.intervene.response.SportPlanResponse;
import org.dows.hep.api.intervene.response.SportPlanInfoResponse;
import org.dows.hep.api.intervene.request.SaveSportPlanRequest;
import org.dows.hep.api.intervene.request.DelSportPlanRequest;
import org.dows.hep.api.intervene.request.SetSpotPlanStateRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:干预:运动方案
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
public class SportPlanBiz{
    /**
    * @param
    * @return
    * @说明: 获取运动方案列表
    * @关联表: sport_plan
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public SportPlanResponse pageSportPlan(FindSportRequest findSport ) {
        return new SportPlanResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取运动方案信息
    * @关联表: sport_plan,sport_plan_items
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public SportPlanInfoResponse getSportPlan(String sportPlanId ) {
        return new SportPlanInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 保存运动方案
    * @关联表: sport_plan,sport_plan_items
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public Boolean saveSportPlan(SaveSportPlanRequest saveSportPlan ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除运动方案
    * @关联表: sport_plan,sport_plan_items
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public Boolean delSportPlan(DelSportPlanRequest delSportPlan ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 启用、禁用运动方案
    * @关联表: sport_plan
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public Boolean setSpotPlanState(SetSpotPlanStateRequest setSpotPlanState ) {
        return Boolean.FALSE;
    }
}