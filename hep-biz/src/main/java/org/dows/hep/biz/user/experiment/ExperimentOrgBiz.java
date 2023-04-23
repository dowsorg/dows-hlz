package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.user.experiment.request.FindOrgNoticeRequest;
import org.dows.hep.api.user.experiment.request.FindOrgPersonsRequest;
import org.dows.hep.api.user.experiment.request.FindOrgReportRequest;
import org.dows.hep.api.user.experiment.request.StartOrgFlowRequest;
import org.dows.hep.api.user.experiment.response.*;
import org.springframework.stereotype.Service;

/**
* @description project descr:实验:机构操作
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class ExperimentOrgBiz{
    /**
    * @param
    * @return
    * @说明: 获取机构人物列表，挂号费用，挂号状态
    * @关联表: ExperimentPerson
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgPersonResponse pageOrgPersons(FindOrgPersonsRequest findOrgPersons ) {
        return new OrgPersonResponse();
    }
    /**
    * @param
    * @return
    * @说明: 挂号：医院，体检中心
    * @关联表: ExperimentPerson,OperateFlow
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean startOrgFlow(StartOrgFlowRequest startOrgFlow ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取机构通知列表
    * @关联表: ExperimentOrgNotice
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgNoticeResponse pageOrgNotice(FindOrgNoticeRequest findOrgNotice ) {
        return new OrgNoticeResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取机构报告列表
    * @关联表: OperateFlow
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgReportResponse pageOrgReport(FindOrgReportRequest findOrgReport ) {
        return new OrgReportResponse();
    }
    /**
    * @param
    * @return
    * @说明: 查看体检报告详情
    * @关联表: OperateFlow,OperateFlowSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public PhysicalExamReportInfoResponse getPhysicalExamReport(String operateFlowId ) {
        return new PhysicalExamReportInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 查看诊疗报告详情
    * @关联表: OperateFlow,OperateFlowSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public TreatReportInfoResponse getTreatReport(String operateFlowId ) {
        return new TreatReportInfoResponse();
    }
}