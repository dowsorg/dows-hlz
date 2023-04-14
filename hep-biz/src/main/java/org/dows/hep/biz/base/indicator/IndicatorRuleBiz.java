package org.dows.hep.biz.base.indicator;

import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorRuleRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorRuleRequest;
import org.dows.hep.api.base.indicator.response.IndicatorRuleResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:指标:指标规则
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class IndicatorRuleBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标规则
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void createIndicatorRule(CreateIndicatorRuleRequest createIndicatorRule ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标规则
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void deleteIndicatorRule(String indicatorRuleId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新指标规则
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void updateIndicatorRule(UpdateIndicatorRuleRequest updateIndicatorRule ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取指标规则
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public IndicatorRuleResponse getIndicatorRule(String indicatorRuleId ) {
        return new IndicatorRuleResponse();
    }
}