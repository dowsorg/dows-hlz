package org.dows.hep.biz.base.indicator;

import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorValRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorValRequest;
import org.dows.hep.api.base.indicator.response.IndicatorValResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:指标:指标值
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class IndicatorValBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标值
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void createIndicatorVal(CreateIndicatorValRequest createIndicatorVal ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标值
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void deleteIndicatorVal(String indicatorValId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新指标值
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void updateIndicatorVal(UpdateIndicatorValRequest updateIndicatorVal ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取指标值
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public IndicatorValResponse indicatorVal(String indicatorValId ) {
        return new IndicatorValResponse();
    }
}