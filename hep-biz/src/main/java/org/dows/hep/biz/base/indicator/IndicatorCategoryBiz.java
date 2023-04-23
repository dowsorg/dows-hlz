package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorCategoryRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorCategoryRequest;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:指标类别
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class IndicatorCategoryBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标类别
    * @关联表: indicator_category
    * @工时: 3H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorCategory(CreateIndicatorCategoryRequest createIndicatorCategory ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标类别
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorCategory(String indicatorCategoryId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新指标类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorCategory(UpdateIndicatorCategoryRequest updateIndicatorCategory ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查询指标类别
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorCategoryResponse getIndicatorCategory(String indicatorCategoryId ) {
        return new IndicatorCategoryResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标类别
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorCategoryResponse> listIndicatorCategory(String appId, Long pid, String indicatorCategoryId, String categoryCode, String categoryName ) {
        return new ArrayList<IndicatorCategoryResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选指标类别
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorCategory(Integer pageNo, Integer pageSize, String appId, Long pid, String indicatorCategoryId, String categoryCode, String categoryName ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 一键同步（非常复杂）
    * @关联表: 
    * @工时: 8H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void sync(String appId ) {
        
    }
}