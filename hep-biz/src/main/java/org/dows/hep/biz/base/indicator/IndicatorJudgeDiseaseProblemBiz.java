package org.dows.hep.biz.base.indicator;

import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeDiseaseProblemRequest;
import org.dows.hep.api.base.indicator.request.DecimalRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeDiseaseProblemRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeDiseaseProblemRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeDiseaseProblemResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:判断指标疾病问题
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class IndicatorJudgeDiseaseProblemBiz{
    /**
    * @param
    * @return
    * @说明: 创建判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void createIndicatorJudgeDiseaseProblem(CreateIndicatorJudgeDiseaseProblemRequest createIndicatorJudgeDiseaseProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void deleteIndicatorJudgeDiseaseProblem(String indicatorJudgeDiseaseProblemId ) {
        
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
    public void updateStatus(UpdateStatusIndicatorJudgeDiseaseProblemRequest updateStatusIndicatorJudgeDiseaseProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void updateIndicatorJudgeDiseaseProblem(UpdateIndicatorJudgeDiseaseProblemRequest updateIndicatorJudgeDiseaseProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public IndicatorJudgeDiseaseProblemResponse getIndicatorJudgeDiseaseProblem(String indicatorJudgeDiseaseProblemId ) {
        return new IndicatorJudgeDiseaseProblemResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<IndicatorJudgeDiseaseProblemResponse> listIndicatorJudgeDiseaseProblem(String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new ArrayList<IndicatorJudgeDiseaseProblemResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String pageIndicatorJudgeDiseaseProblem(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new String();
    }
}