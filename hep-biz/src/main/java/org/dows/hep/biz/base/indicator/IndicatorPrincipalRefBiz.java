package org.dows.hep.biz.base.indicator;

import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorPrincipalRefRequest;
import org.dows.hep.api.base.indicator.response.IndicatorPrincipalRefResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:指标:指标主体关联关系
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class IndicatorPrincipalRefBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标主体关联关系
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
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
    * @创建时间: 2023年4月14日 下午4:47:53
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
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public IndicatorPrincipalRefResponse getIndicatorPrincipalRef(String indicatorPrincipalRefId ) {
        return new IndicatorPrincipalRefResponse();
    }
}