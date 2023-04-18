package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.base.indicator.request.DecimalRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthManagementGoalResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:判断指标健管目标
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class IndicatorJudgeHealthManagementGoalBiz{
    /**
    * @param
    * @return
    * @说明: 创建判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void createIndicatorJudgeHealthManagementGoal(CreateIndicatorJudgeHealthManagementGoalRequest createIndicatorJudgeHealthManagementGoal ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void deleteIndicatorJudgeHealthManagementGoal(String indicatorJudgeHealthManagementGoalId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 批量删除
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void batchDelete(String string ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更改启用状态
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void updateStatus(UpdateStatusIndicatorJudgeHealthManagementGoalRequest updateStatusIndicatorJudgeHealthManagementGoal ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void updateIndicatorJudgeHealthManagementGoal(UpdateIndicatorJudgeHealthManagementGoalRequest updateIndicatorJudgeHealthManagementGoal ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public IndicatorJudgeHealthManagementGoalResponse getIndicatorJudgeHealthManagementGoal(String indicatorJudgeHealthManagementGoalId ) {
        return new IndicatorJudgeHealthManagementGoalResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<IndicatorJudgeHealthManagementGoalResponse> listIndicatorJudgeHealthManagementGoal(String appId, String indicatorCategoryId, DecimalRequest decimal, Integer integer ) {
        return new ArrayList<IndicatorJudgeHealthManagementGoalResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String pageIndicatorJudgeHealthManagementGoal(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, DecimalRequest decimal, Integer integer ) {
        return new String();
    }
}