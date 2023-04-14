package org.dows.hep.biz.base.indicator;

import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorViewBaseInfoRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorViewBaseInfoRequest;
import org.dows.hep.api.base.indicator.response.IndicatorViewBaseInfoResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:指标:查看指标基本信息类
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class IndicatorViewBaseInfoBiz{
    /**
    * @param
    * @return
    * @说明: 创建指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void createIndicatorViewBaseInfo(CreateIndicatorViewBaseInfoRequest createIndicatorViewBaseInfo ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void deleteIndicatorViewBaseInfo(String indicatorViewBaseInfoId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更改指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void updateIndicatorViewBaseInfo(UpdateIndicatorViewBaseInfoRequest updateIndicatorViewBaseInfo ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取查看指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public IndicatorViewBaseInfoResponse getIndicatorViewBaseInfo(String indicatorViewBaseInfoId ) {
        return new IndicatorViewBaseInfoResponse();
    }
}