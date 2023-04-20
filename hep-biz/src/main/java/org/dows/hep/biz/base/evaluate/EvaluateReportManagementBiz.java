package org.dows.hep.biz.base.evaluate;

import org.dows.hep.api.base.evaluate.request.CreateEvaluateReportManagementRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateReportManagementResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:评估:评估报告管理
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void deleteEvaluateReportManagement(String evaluateReportManagementId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取评估报告管理
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public EvaluateReportManagementResponse getEvaluateReportManagement(String evaluateReportManagementId ) {
        return new EvaluateReportManagementResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选评估报告管理
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<EvaluateReportManagementResponse> listEvaluateReportManagement(String appId, String questionnaireId, String reportName, String reportDescr, String assessmentResult, String suggestion, Integer minScore, Integer maxScore ) {
        return new ArrayList<EvaluateReportManagementResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选评估报告管理
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String pageEvaluateReportManagement(Integer pageNo, Integer pageSize, String appId, String questionnaireId, String reportName, String reportDescr, String assessmentResult, String suggestion, Integer minScore, Integer maxScore ) {
        return new String();
    }
}