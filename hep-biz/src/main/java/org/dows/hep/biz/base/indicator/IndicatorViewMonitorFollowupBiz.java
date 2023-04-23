package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.base.indicator.request.IndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.base.indicator.response.IndicatorViewMonitorFollowupResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:查看指标监测随访类
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class IndicatorViewMonitorFollowupBiz{
    /**
    * @param
    * @return
    * @说明: 创建查看指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorViewMonitorFollowup(CreateIndicatorViewMonitorFollowupRequest createIndicatorViewMonitorFollowup ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorViewMonitorFollowup(String indicatorViewMonitorFollowupId ) {
        
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
    public void updateStatus(IndicatorViewMonitorFollowupRequest indicatorViewMonitorFollowup ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新查看指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorViewMonitorFollowup(UpdateIndicatorViewMonitorFollowupRequest updateIndicatorViewMonitorFollowup ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取查看指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorViewMonitorFollowupResponse getIndicatorViewMonitorFollowup(String indicatorViewMonitorFollowupId ) {
        return new IndicatorViewMonitorFollowupResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选查看指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorViewMonitorFollowupResponse> listIndicatorViewMonitorFollowup(String appId, String indicatorCategoryId, String name, Integer type, Integer status ) {
        return new ArrayList<IndicatorViewMonitorFollowupResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选查看指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorViewMonitorFollowup(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, Integer type, Integer status ) {
        return new String();
    }
}