package org.dows.hep.biz.indicator;

import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorPrincipalRefRequest;
import org.dows.hep.api.indicator.response.IndicatorPrincipalRefResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:指标:指标主体关联关系
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
public class IndicatorPrincipalRefBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标主体关联关系
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public void createIndicatorPrincipalRef(CreateIndicatorPrincipalRefRequest createIndicatorPrincipalRef ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标主体关联关系
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public void deleteIndicatorPrincipalRef(String indicatorPrincipalRefId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查询指标主体关联关系
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public IndicatorPrincipalRefResponse getIndicatorPrincipalRef(String indicatorPrincipalRefId ) {
        return new IndicatorPrincipalRefResponse();
    }
}