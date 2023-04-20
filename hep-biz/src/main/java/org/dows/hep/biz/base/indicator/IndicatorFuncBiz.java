package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.response.IndicatorFuncResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:指标功能
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class IndicatorFuncBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标功能
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void createIndicatorFunc(CreateIndicatorFuncRequest createIndicatorFunc ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标功能
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void deleteIndicatorFunc(String indicatorFunc ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新指标功能
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void updateIndicatorFunc(UpdateIndicatorFuncRequest updateIndicatorFunc ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取指标功能
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public IndicatorFuncResponse getIndicatorFunc(String indicatorFunc ) {
        return new IndicatorFuncResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<IndicatorFuncResponse> listIndicatorFunc(String appId, String indicatorCategoryId, String name ) {
        return new ArrayList<IndicatorFuncResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选指标类别
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String pageIndicatorFunc(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name ) {
        return new String();
    }
}