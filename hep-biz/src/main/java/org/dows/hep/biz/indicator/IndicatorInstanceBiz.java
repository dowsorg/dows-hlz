package org.dows.hep.biz.indicator;

import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorInstanceRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorInstanceRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorInstanceRequest;
import org.dows.hep.api.indicator.response.IndicatorInstanceResponse;
import org.dows.hep.api.indicator.request.VarcharRequest;
import org.dows.hep.api.indicator.response.IndicatorInstanceResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:指标:指标实例
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
public class IndicatorInstanceBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标实例
    * @关联表: 
    * @工时: 3H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public void createIndicatorInstance(CreateIndicatorInstanceRequest createIndicatorInstance ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public void deleteIndicatorInstance(String indicatorInstanceId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新指标
    * @关联表: 
    * @工时: 3H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public void updateIndicatorInstance(UpdateIndicatorInstanceRequest updateIndicatorInstance ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 批量更新指标
    * @关联表: 
    * @工时: 3H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public void batchUpdateIndicatorInstance(List<UpdateIndicatorInstanceRequest> updateIndicatorInstance ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查询指标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public IndicatorInstanceResponse getIndicatorInstance(String indicatorInstanceId ) {
        return new IndicatorInstanceResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public List<IndicatorInstanceResponse> listIndicatorInstance(String appId, VarcharRequest varchar ) {
        return new ArrayList<IndicatorInstanceResponse>();
    }
}