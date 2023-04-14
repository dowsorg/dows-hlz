package org.dows.hep.biz.indicator;

import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.indicator.request.IndicatorJudgeHealthProblemIdRequest;
import org.dows.hep.api.indicator.request.UpdateStatusIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.indicator.request.IndicatorJudgeHealthProblemIdRequest;
import org.dows.hep.api.indicator.response.IndicatorJudgeHealthProblemResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:指标:判断指标健康问题
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
public class IndicatorJudgeHealthProblemBiz{
    /**
    * @param
    * @return
    * @说明: 创建判断指标健康问题
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public void createIndicatorJudgeHealthProblem(CreateIndicatorJudgeHealthProblemRequest createIndicatorJudgeHealthProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标健康问题
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public void deleteIndicatorJudgeHealthProblem(IndicatorJudgeHealthProblemIdRequest indicatorJudgeHealthProblemId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更改启用状态
    * @关联表: 
    * @工时: 0H
    * @开发者: 
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public void updateStatus(UpdateStatusIndicatorJudgeHealthProblemRequest updateStatusIndicatorJudgeHealthProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标健康问题
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public IndicatorJudgeHealthProblemResponse getIndicatorJudgeHealthProblem(IndicatorJudgeHealthProblemIdRequest indicatorJudgeHealthProblemId ) {
        return new IndicatorJudgeHealthProblemResponse();
    }
}