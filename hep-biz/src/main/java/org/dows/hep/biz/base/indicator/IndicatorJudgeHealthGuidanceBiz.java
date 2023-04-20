package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthGuidanceRequest;
import org.dows.hep.api.base.indicator.request.DecimalRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeHealthGuidanceRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeHealthGuidanceRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthGuidanceResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:判断指标健康指导
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class IndicatorJudgeHealthGuidanceBiz{
    /**
    * @param
    * @return
    * @说明: 创建判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void createIndicatorJudgeHealthGuidance(CreateIndicatorJudgeHealthGuidanceRequest createIndicatorJudgeHealthGuidance ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void deleteIndicatorJudgeHealthGuidance(String indicatorJudgeHealthGuidanceId ) {
        
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
    public void updateStatus(UpdateStatusIndicatorJudgeHealthGuidanceRequest updateStatusIndicatorJudgeHealthGuidance ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void updateIndicatorJudgeHealthGuidance(UpdateIndicatorJudgeHealthGuidanceRequest updateIndicatorJudgeHealthGuidance ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public IndicatorJudgeHealthGuidanceResponse getIndicatorJudgeHealthGuidance(String indicatorJudgeHealthGuidanceId ) {
        return new IndicatorJudgeHealthGuidanceResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<IndicatorJudgeHealthGuidanceResponse> listIndicatorJudgeHealthGuidance(String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new ArrayList<IndicatorJudgeHealthGuidanceResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String pageIndicatorJudgeHealthGuidance(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new String();
    }
}