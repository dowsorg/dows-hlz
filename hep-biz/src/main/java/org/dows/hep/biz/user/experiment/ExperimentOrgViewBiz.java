package org.dows.hep.biz.user.experiment;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.entity.IndicatorViewPhysicalExamEntity;
import org.dows.hep.entity.IndicatorViewSupportExamEntity;
import org.dows.hep.entity.OperateOrgFuncEntity;
import org.dows.hep.entity.OperateOrgFuncSnapEntity;
import org.dows.hep.service.IndicatorViewPhysicalExamService;
import org.dows.hep.service.IndicatorViewSupportExamService;
import org.dows.hep.service.OperateOrgFuncService;
import org.dows.hep.service.OperateOrgFuncSnapService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
* @description project descr:实验:机构操作-查看指标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class ExperimentOrgViewBiz{

    private final OperateOrgFuncService operateOrgFuncService;
    private final OperateOrgFuncSnapService operateOrgFuncSnapService;
    private final IndicatorViewPhysicalExamService indicatorViewPhysicalExamService;
    private final IndicatorViewSupportExamService indicatorViewSupportExamService;
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

    /**
     * @param
     * @return
     * @说明: 体格检查+辅助检查：体格检查+辅助检查保存
     * @关联表: OperateOrgFunc,OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年5月31日 下午17:14:34
     */
    @DSTransactional
    public Boolean savePhysiqueAndAuxiliary(List<GetOrgViewReportRequest> reportRequestList,String accountId, String accountName) {
        //1、删除用户以前功能点信息
        List<OperateOrgFuncEntity> entityList = operateOrgFuncService.lambdaQuery()
                .select(OperateOrgFuncEntity::getId,OperateOrgFuncEntity::getDeleted)
                .eq(OperateOrgFuncEntity::getPeriods, reportRequestList.get(0).getPeriods())
                .eq(OperateOrgFuncEntity::getIndicatorFuncId, reportRequestList.get(0).getIndicatorFuncId())
                .eq(OperateOrgFuncEntity::getExperimentPersonId, reportRequestList.get(0).getExperimentPersonId())
                .eq(OperateOrgFuncEntity::getAppId, reportRequestList.get(0).getAppId())
                .eq(OperateOrgFuncEntity::getDeleted, false)
                .list();
        if (entityList != null && entityList.size() > 0) {
            List<OperateOrgFuncSnapEntity> snapList = new ArrayList<>();
            entityList.forEach(entity -> {
                entity.setDeleted(true);
                operateOrgFuncService.updateById(entity);
                OperateOrgFuncSnapEntity snapEntity = operateOrgFuncSnapService.lambdaQuery()
                        .eq(OperateOrgFuncSnapEntity::getOperateOrgFuncId, entity.getOperateOrgFuncId())
                        .eq(OperateOrgFuncSnapEntity::getAppId, reportRequestList.get(0).getAppId())
                        .eq(OperateOrgFuncSnapEntity::getDeleted, false)
                        .one();
                snapList.add(snapEntity);

            });
            //1.1、删除用户以前的功能点快照
            if (snapList != null && snapList.size() > 0) {
                snapList.forEach(snap -> {
                    snap.setDeleted(true);
                });
                operateOrgFuncSnapService.updateBatchById(snapList);
            }
        }
        //2、todo 扣费
        //3、重新插入数据
        List<OperateOrgFuncSnapEntity> snapList = new ArrayList<>();
        reportRequestList.forEach(operateOrgFunc -> {
            OperateOrgFuncEntity entity = OperateOrgFuncEntity
                    .builder()
                    .appId(operateOrgFunc.getAppId())
                    .operateFlowId(operateOrgFunc.getOperateFlowId())
                    .indicatorFuncId(operateOrgFunc.getIndicatorFuncId())
                    .experimentInstanceId(operateOrgFunc.getExperimentInstanceId())
                    .experimentGroupId(operateOrgFunc.getExperimentGroupId())
                    .experimentPersonId(operateOrgFunc.getExperimentPersonId())
                    .experimentOrgId(operateOrgFunc.getExperimentOrgId())
                    .operateAccountId(accountId)
                    .operateAccountName(accountName)
                    .operateType(operateOrgFunc.getOperateType())
                    .periods(operateOrgFunc.getPeriods())
                    .score(operateOrgFunc.getScore())
                    .operateTime(new Date())
                    .build();
            operateOrgFuncService.save(entity);
            OperateOrgFuncSnapEntity snapEntity = OperateOrgFuncSnapEntity.builder()
                    .appId(operateOrgFunc.getAppId())
                    .operateOrgFuncId(entity.getOperateOrgFuncId())
                    .snapTime(new Date())
                    .inputJson(operateOrgFunc.getInputJson())
                    .build();
            snapList.add(snapEntity);
        });
        return operateOrgFuncSnapService.saveBatch(snapList);
    }

    /**
     * @param
     * @return
     * @说明: 体格检查：判断是否满足指标
     * @关联表: indicatorViewPhysicalExam
     * @工时: 6H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月31日 下午18:06:34
     */
    public List<GetOrgViewReportResponse> getIndicatorPhysicalExamVerifiResults(List<GetOrgViewReportRequest> reportRequestList) {
        List<GetOrgViewReportResponse> responseList = new ArrayList<>();
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        reportRequestList.forEach(reportRequest -> {
            //1、根据ID获取判断规则
            IndicatorViewPhysicalExamEntity entity = indicatorViewPhysicalExamService.lambdaQuery()
                    .select(IndicatorViewPhysicalExamEntity::getIndicatorInstanceId,IndicatorViewPhysicalExamEntity::getFee)
                    .eq(IndicatorViewPhysicalExamEntity::getIndicatorViewPhysicalExamId, reportRequest.getIndicatorViewPhysicalExamId())
                    .eq(IndicatorViewPhysicalExamEntity::getStatus, true)
                    .one();
            //todo、根据判断规则判断是否满足条件,将指标值返回
            flag.set(false);
        });
        return responseList;
    }

    /**
     * @param
     * @return
     * @说明: 辅助检查：获取判断结果
     * @关联表: indicatorViewSupportExam
     * @工时: 6H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月31日 下午18:30:34
     */
    public List<GetOrgViewReportResponse> getIndicatorSupportExamVerifiResults(List<GetOrgViewReportRequest> reportRequestList) {
        List<GetOrgViewReportResponse> responseList = new ArrayList<>();
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        reportRequestList.forEach(reportRequest -> {
            //1、根据ID获取判断规则
            IndicatorViewSupportExamEntity entity = indicatorViewSupportExamService.lambdaQuery()
                    .select(IndicatorViewSupportExamEntity::getIndicatorInstanceId,IndicatorViewSupportExamEntity::getFee)
                    .eq(IndicatorViewSupportExamEntity::getIndicatorViewSupportExamId, reportRequest.getIndicatorViewSupportExamId())
                    .eq(IndicatorViewSupportExamEntity::getStatus, true)
                    .one();
            //todo、根据判断规则判断是否满足条件,将指标值返回
            flag.set(false);
        });
        return responseList;
    }
}