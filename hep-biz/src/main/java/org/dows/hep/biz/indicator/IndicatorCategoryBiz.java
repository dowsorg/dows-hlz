package org.dows.hep.biz.indicator;

import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorCategoryRequest;
import org.dows.hep.api.indicator.request.CreateOrUpdateIndicatorCategoryRequest;
import org.dows.hep.api.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.indicator.response.IndicatorCategoryResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:指标:指标目录
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
public class IndicatorCategoryBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标目录
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public void createIndicatorCategory(CreateIndicatorCategoryRequest createIndicatorCategory ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标目录
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public void deleteIndicatorCategory(String indicatorCategoryId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新指标目录
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public void updateIndicatorCategory(String appId, List<CreateOrUpdateIndicatorCategoryRequest> createOrUpdateIndicatorCategory ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查询指标目录
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public IndicatorCategoryResponse getIndicatorCategory(String indicatorCategoryId ) {
        return new IndicatorCategoryResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标目录
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public List<IndicatorCategoryResponse> listIndicatorCategory(String appId, Long pid, String indicatorCategoryId, String categoryCode, String categoryName ) {
        return new ArrayList<IndicatorCategoryResponse>();
    }
}