package org.dows.hep.biz.base.evaluate;

import org.dows.framework.api.Response;
import org.dows.hep.api.base.evaluate.request.CreateEvaluateReportManagementRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateReportManagementResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:评估:评估报告管理
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class EvaluateReportManagementBiz{
    /**
    * @param
    * @return
    * @说明: 创建评估报告管理
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void evaluateReportManagement(CreateEvaluateReportManagementRequest createEvaluateReportManagement ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除评估报告管理
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void deleteEvaluateReportManagement(String evaluateReportManagementId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查看评估报告管理
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public EvaluateReportManagementResponse getEvaluateReportManagement(String evaluateReportManagementId ) {
        return new EvaluateReportManagementResponse();
    }
}