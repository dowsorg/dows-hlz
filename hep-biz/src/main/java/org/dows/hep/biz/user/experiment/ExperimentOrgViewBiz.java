package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:实验:机构操作-查看指标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class ExperimentOrgViewBiz{
    /**
    * @param
    * @return
    * @说明: 获取人物基本信息（健管，体检，医院）
    * @关联表: OperateOrgFunc，OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgPersonBasicResponse getOrgPersonBasic(FindOrgPersonBasicRequest findOrgPersonBasic ) {
        return new OrgPersonBasicResponse();
    }
    /**
    * @param
    * @return
    * @说明: 监测随访：获取类别，随访表列表
    * @关联表: indicator_view_monitor_followup,indicator_category
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<FollowupDefResponse> listFollowupDef(FindFollowupDefRequest findFollowupDef ) {
        return new ArrayList<FollowupDefResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 监测随访：获取随访表内容
    * @关联表: indicator_view_monitor_followup，indicator_view_monitor_followup_followup_content,indicator_view_monitor_followup_content_ref
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public FollowupDefResponse getFollowupDef(String indicatorViewMonitorFollowupId ) {
        return new FollowupDefResponse();
    }
    /**
    * @param
    * @return
    * @说明: 监测随访：保存随访设置，频率，表格
    * @关联表: OperateFollowupTimer
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean setFollowup(SetFollowupRequest setFollowup ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 监测随访：获取随访状态，是否可随访
    * @关联表: OperateFollowupTimer
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public FollowupStateResponse getFollowupState(FindFollowupStateRequest findFollowupState ) {
        return new FollowupStateResponse();
    }
    /**
    * @param
    * @return
    * @说明: 监测随访：开始随访（保存随访记录）
    * @关联表: OperateFollowupTimer,OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SaveFollowupResponse saveFollowup(SaveFollowupRequest saveFollowup ) {
        return new SaveFollowupResponse();
    }
    /**
    * @param
    * @return
    * @说明: 监测随访：查看随访报告
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public FollowupReportInfoResponse getFollowUpReport(String operateOrgFuncId ) {
        return new FollowupReportInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 体格检查+辅助检查：获取检查类别+项目
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<CategsWithStateResponse> listOrgViewCategs(FindOrgViewCategsRequest findOrgViewCategs ) {
        return new ArrayList<CategsWithStateResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 体格检查+辅助检查：执行检查(开检查单)
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SaveOrgViewResponse saveOrgView(SaveOrgViewRequest saveOrgView ) {
        return new SaveOrgViewResponse();
    }
    /**
    * @param
    * @return
    * @说明: 体格检查+辅助检查：获取最新检查报告
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public GetOrgViewReportResponse getOrgViewReport(GetOrgViewReportRequest getOrgViewReport ) {
        return new GetOrgViewReportResponse();
    }
}