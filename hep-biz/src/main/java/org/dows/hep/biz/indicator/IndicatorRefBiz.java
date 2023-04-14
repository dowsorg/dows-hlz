package org.dows.hep.biz.indicator;

import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorRefRequest;
import org.dows.hep.api.indicator.response.IndicatorRefResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:指标:指标引用
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
public class IndicatorRefBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标引用
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public void createIndicatorRef(CreateIndicatorRefRequest createIndicatorRef ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标引用
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public void deleteIndicatorRef(String indicatorRefId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取指标引用列表
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public List<IndicatorRefResponse> listIndicatorRef(String indicatorInstanceId ) {
        return new ArrayList<IndicatorRefResponse>();
    }
}