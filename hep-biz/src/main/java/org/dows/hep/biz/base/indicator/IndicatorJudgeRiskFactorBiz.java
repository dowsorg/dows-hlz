package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.request.DecimalRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeRiskFactorResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:判断指标危险因素
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class IndicatorJudgeRiskFactorBiz{
    /**
    * @param
    * @return
    * @说明: 创建危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorJudgeRiskFactor(CreateIndicatorJudgeRiskFactorRequest createIndicatorJudgeRiskFactor ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorJudgeRiskFactor(String indicatorJudgeRiskFactorId ) {
        
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
    public void updateStatus(UpdateStatusIndicatorJudgeRiskFactorRequest updateStatusIndicatorJudgeRiskFactor ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 判断指标危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorJudgeRiskFactor(UpdateIndicatorJudgeRiskFactorRequest updateIndicatorJudgeRiskFactor ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 判断指标危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorJudgeRiskFactorResponse getIndicatorJudgeRiskFactor(String indicatorJudgeRiskFactorId ) {
        return new IndicatorJudgeRiskFactorResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选判断指标危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorJudgeRiskFactorResponse> listIndicatorJudgeRiskFactor(String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new ArrayList<IndicatorJudgeRiskFactorResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选判断指标危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorJudgeRiskFactor(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new String();
    }
}