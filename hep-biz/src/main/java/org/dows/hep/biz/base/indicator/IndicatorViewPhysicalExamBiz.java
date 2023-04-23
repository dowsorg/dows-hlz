package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorViewPhysicalExamRequest;
import org.dows.hep.api.base.indicator.request.DecimalRequest;
import org.dows.hep.api.base.indicator.request.IndicatorViewPhysicalExamRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorViewPhysicalExamRequest;
import org.dows.hep.api.base.indicator.response.IndicatorViewPhysicalExamResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:查看指标体格检查类
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class IndicatorViewPhysicalExamBiz{
    /**
    * @param
    * @return
    * @说明: 创建查看指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorViewPhysicalExam(CreateIndicatorViewPhysicalExamRequest createIndicatorViewPhysicalExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorViewPhysicalExam(String indicatorViewPhysicalExamId ) {
        
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
    public void updateStatus(IndicatorViewPhysicalExamRequest indicatorViewPhysicalExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查看指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorViewPhysicalExam(UpdateIndicatorViewPhysicalExamRequest updateIndicatorViewPhysicalExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取查看指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorViewPhysicalExamResponse getIndicatorViewPhysicalExam(String indicatorViewPhysicalExamId ) {
        return new IndicatorViewPhysicalExamResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选查看指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorViewPhysicalExamResponse> listIndicatorViewPhysicalExam(String appId, String indicatorCategoryId, String name, Integer type, DecimalRequest fee, String resultAnalysis, Integer status ) {
        return new ArrayList<IndicatorViewPhysicalExamResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选查看指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorViewPhysicalExam(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, Integer type, DecimalRequest fee, String resultAnalysis, Integer status ) {
        return new String();
    }
}