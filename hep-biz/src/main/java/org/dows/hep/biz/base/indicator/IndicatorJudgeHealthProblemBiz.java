package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.request.DecimalRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthProblemResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:判断指标健康问题
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class IndicatorJudgeHealthProblemBiz{
    /**
    * @param
    * @return
    * @说明: 创建判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorJudgeHealthProblem(CreateIndicatorJudgeHealthProblemRequest createIndicatorJudgeHealthProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorJudgeHealthProblem(String indicatorJudgeHealthProblemId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 批量删除
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateStatus(UpdateStatusIndicatorJudgeHealthProblemRequest updateStatusIndicatorJudgeHealthProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorJudgeHealthProblem(UpdateIndicatorJudgeHealthProblemRequest updateIndicatorJudgeHealthProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorJudgeHealthProblemResponse getIndicatorJudgeHealthProblem(String indicatorJudgeHealthProblemId ) {
        return new IndicatorJudgeHealthProblemResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorJudgeHealthProblemResponse> listIndicatorJudgeHealthProblem(String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new ArrayList<IndicatorJudgeHealthProblemResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorJudgeHealthProblem(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new String();
    }
}