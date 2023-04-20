package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorViewSupportExamRequest;
import org.dows.hep.api.base.indicator.request.DecimalRequest;
import org.dows.hep.api.base.indicator.request.IndicatorViewSupportExamRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorViewSupportExamRequest;
import org.dows.hep.api.base.indicator.response.IndicatorViewSupportExamResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:查看指标辅助检查类
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class IndicatorViewSupportExamBiz{
    /**
    * @param
    * @return
    * @说明: 创建查看指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void createIndicatorViewSupportExam(CreateIndicatorViewSupportExamRequest createIndicatorViewSupportExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除查看指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void deleteIndicatorViewSupportExam(String indicatorViewSupportExamId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 批量删除
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void updateStatus(IndicatorViewSupportExamRequest indicatorViewSupportExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查看指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void updateIndicatorViewSupportExam(UpdateIndicatorViewSupportExamRequest updateIndicatorViewSupportExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查看指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public IndicatorViewSupportExamResponse getIndicatorViewSupportExam(String indicatorViewSupportExamId ) {
        return new IndicatorViewSupportExamResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<IndicatorViewSupportExamResponse> listIndicatorViewSupportExam(String appId, String indicatorCategoryId, String name, String type, DecimalRequest fee, String resultAnalysis, Integer status ) {
        return new ArrayList<IndicatorViewSupportExamResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String pageIndicatorViewSupportExam(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, String type, DecimalRequest fee, String resultAnalysis, Integer status ) {
        return new String();
    }
}