package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorVarRequest;
import org.dows.hep.api.base.indicator.request.IndicatorVarIdRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorVarRequest;
import org.dows.hep.api.base.indicator.response.IndicatorVarResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:指标变量
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class IndicatorVarBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标变量
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void createIndicatorVar(CreateIndicatorVarRequest createIndicatorVar ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标变量
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void deleteIndicatorVar(IndicatorVarIdRequest indicatorVarId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新指标变量
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void updateIndicatorVar(UpdateIndicatorVarRequest updateIndicatorVar ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查询指标变量
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public IndicatorVarResponse getIndicatorVar(IndicatorVarIdRequest indicatorVarId ) {
        return new IndicatorVarResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标变量
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<IndicatorVarResponse> listIndicatorVar(String appId, String indicatorInstanceId, String dbName, String tbName, String varName, String varCode, String periods, String descr ) {
        return new ArrayList<IndicatorVarResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选指标变量
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String pageIndicatorVar(Integer pageNo, Integer pageSize, String appId, String indicatorInstanceId, String dbName, String tbName, String varName, String varCode, String periods, String descr ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 做公式组件
    * @关联表: 
    * @工时: 40H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void createExpressionComponent(String expressionId ) {
        
    }
}