package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorInstanceRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorInstanceRequest;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:指标实例
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class IndicatorInstanceBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标实例
    * @关联表: 
    * @工时: 3H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<IndicatorInstanceResponse> listIndicatorInstance(String appId, Integer core, Integer food, String indicatorCategoryId ) {
        return new ArrayList<IndicatorInstanceResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选指标
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String pageIndicatorInstance(Integer pageNo, Integer pageSize, String appId, Integer core, Integer food, String indicatorCategoryId ) {
        return new String();
    }
}