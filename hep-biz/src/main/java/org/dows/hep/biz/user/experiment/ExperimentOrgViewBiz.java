package org.dows.hep.biz.user.experiment;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.constant.ViewBaseInfoConstant;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.util.TimeUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:实验:机构操作-查看指标
 * @date 2023年4月23日 上午9:44:34
 */
@Service
@RequiredArgsConstructor
public class ExperimentOrgViewBiz {

    private final OperateOrgFuncService operateOrgFuncService;
    private final OperateOrgFuncSnapService operateOrgFuncSnapService;
    private final ExperimentIndicatorViewPhysicalExamService experimentIndicatorViewPhysicalExamService;
    private final ExperimentIndicatorViewSupportExamService experimentIndicatorViewSupportExamService;
    private final ExperimentViewBaseInfoDescrService experimentViewBaseInfoDescrService;
    private final ExperimentViewBaseInfoDescrRefService experimentViewBaseInfoDescrRefService;
    private final ExperimentIndicatorValService experimentIndicatorValService;
    private final ExperimentIndicatorInstanceService experimentIndicatorInstanceService;
    private final ExperimentViewBaseInfoMonitorService experimentViewBaseInfoMonitorService;
    private final ExperimentViewBaseInfoMonitorContentService experimentViewBaseInfoMonitorContentService;
    private final ExperimentViewBaseInfoMonitorContentRefService experimentViewBaseInfoMonitorContentRefService;
    private final ExperimentViewBaseInfoSingleService experimentViewBaseInfoSingleService;
    private final ExperimentViewMonitorFollowupService experimentViewMonitorFollowupService;
    private final ExperimentViewMonitorFollowupContentService experimentViewMonitorFollowupContentService;
    private final ExperimentViewMonitorFollowupContentRefService experimentViewMonitorFollowupContentRefService;
    private final OperateFollowupTimerService operateFollowupTimerService;
    private final IdGenerator idGenerator;

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
    public OrgPersonBasicResponse getOrgPersonBasic(FindOrgPersonBasicRequest findOrgPersonBasic) {
        return new OrgPersonBasicResponse();
    }

    /**
     * @param
     * @return
     * @说明: 监测随访：获取类别，随访表列表
     * @关联表: indicator_view_monitor_followup, indicator_category
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<ExperimentViewMonitorFollowupEntity> listFollowup(FindFollowupDefRequest findFollowupDef) {
        //1、获取record记录
        return experimentViewMonitorFollowupService.lambdaQuery()
                .eq(ExperimentViewMonitorFollowupEntity::getAppId, findFollowupDef.getAppId())
                .eq(ExperimentViewMonitorFollowupEntity::getExperimentIndicatorFuncId, findFollowupDef.getIndicatorFuncId())
                .eq(ExperimentViewMonitorFollowupEntity::getDeleted, false)
                .list();
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
    public List<ExperimentIndicatorResponse> getFollowupDef(String experimentViewMonitorFollowupId, String appId, String experimentPersonId, String periods) {
        //1、根据分布式ID找到监测随访内容并排序
        List<ExperimentViewMonitorFollowupContentEntity> contentList = experimentViewMonitorFollowupContentService.lambdaQuery()
                .select(ExperimentViewMonitorFollowupContentEntity::getExperimentViewMonitorFollowupContentId,
                        ExperimentViewMonitorFollowupContentEntity::getName,
                        ExperimentViewMonitorFollowupContentEntity::getSeq)
                .eq(ExperimentViewMonitorFollowupContentEntity::getAppId, appId)
                .eq(ExperimentViewMonitorFollowupContentEntity::getExperimentViewMonitorFollowupId, experimentViewMonitorFollowupId)
                .eq(ExperimentViewMonitorFollowupContentEntity::getDeleted, false)
                .list()
                .stream().sorted(Comparator.comparing(iteam -> iteam.getSeq())).collect(Collectors.toList());
        //2、根据监测随访内容ID找到监测随访内容指标ID并再次排序
        List<ExperimentIndicatorResponse> resultList = new ArrayList<>();
        if (contentList != null && contentList.size() > 0) {
            contentList.forEach(content -> {
                //2.1、根据内容找到对应功能点
                ExperimentIndicatorResponse result = new ExperimentIndicatorResponse();
                //2.2、设置项目内容
                result.setContent(content.getName());
                List<ExperimentViewMonitorFollowupContentRefEntity> contentRefList = experimentViewMonitorFollowupContentRefService.lambdaQuery()
                        .eq(ExperimentViewMonitorFollowupContentRefEntity::getExperimentViewMonitorFollowupContentId, content.getExperimentViewMonitorFollowupContentId())
                        .eq(ExperimentViewMonitorFollowupContentRefEntity::getDeleted, false)
                        .list()
                        .stream().sorted(Comparator.comparing(iteam -> iteam.getSeq())).collect(Collectors.toList());
                if (contentRefList != null && contentRefList.size() > 0) {
                    List<ExperimentIndicatorResponse> responseList = new ArrayList<>();
                    contentRefList.forEach(contentRef -> {
                        ExperimentIndicatorResponse response = new ExperimentIndicatorResponse();
                        //todo 根据indicatorInstanceId和experimentPersonId获取experimentIndicatorInstanceId，先假设experimentIndicatorInstanceId字段
                        String experimentIndicatorInstanceId = "010101";
                        //2.3、获取指标值
                        ExperimentIndicatorValEntity valEntity = experimentIndicatorValService.lambdaQuery()
                                .eq(ExperimentIndicatorValEntity::getDeleted, false)
                                .eq(ExperimentIndicatorValEntity::getExperimentIndicatorInstanceId, experimentIndicatorInstanceId)
                                .eq(ExperimentIndicatorValEntity::getPeriods, periods)
                                .one();
                        response.setExperimentIndicatorCurrentVal(valEntity.getCurrentVal());
                        //2.4、获取指标单位
                        ExperimentIndicatorInstanceEntity instanceEntity = experimentIndicatorInstanceService.lambdaQuery()
                                .eq(ExperimentIndicatorInstanceEntity::getDeleted, false)
                                .eq(ExperimentIndicatorInstanceEntity::getExperimentIndicatorInstanceId, experimentIndicatorInstanceId)
                                .eq(ExperimentIndicatorInstanceEntity::getExperimentPersonId, experimentPersonId)
                                .one();
                        response.setUnit(instanceEntity.getUnit());
                        response.setExperimentIndicatorInstanceName(instanceEntity.getIndicatorName());
                        responseList.add(response);
                    });
                    //4.5、设置指标值
                    result.setIndicatorList(responseList);
                    resultList.add(result);
                }
            });
        }
        return resultList;
    }

    /**
     * @param
     * @return
     * @说明: 监测随访：保存随访设置，频率，表格
     * @关联表: OperateFollowupTimer
     * @工时: 6H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月8日 下午17:48:34
     */
    @DSTransactional
    public Boolean setFollowup(SetFollowupRequest setFollowup, String accountId, String accountName) {
        Boolean flag = false;
        OperateFollowupTimerEntity timerEntity = operateFollowupTimerService.lambdaQuery()
                .eq(OperateFollowupTimerEntity::getAppId, setFollowup.getAppId())
                .eq(OperateFollowupTimerEntity::getExperimentInstanceId, setFollowup.getExperimentInstanceId())
                .eq(OperateFollowupTimerEntity::getExperimentGroupId, setFollowup.getExperimentGroupId())
                .eq(OperateFollowupTimerEntity::getExperimentPersonId, setFollowup.getExperimentPersonId())
                .eq(OperateFollowupTimerEntity::getExperimentOrgId, setFollowup.getExperimentOrgId())
                .eq(OperateFollowupTimerEntity::getIndicatorFuncId, setFollowup.getIndicatorFuncId())
                .eq(OperateFollowupTimerEntity::getExperimentViewMonitorFollowupId, setFollowup.getExperimentViewMonitorFollowupId())
                .eq(OperateFollowupTimerEntity::getDeleted, false)
                .one();
        //1、判断是否存在该人物的该监测随访表记录，存在直接更新，不存在则插入
        if (timerEntity != null && !ReflectUtil.isObjectNull(timerEntity)) {
            //2、更新
            OperateFollowupTimerEntity entity = OperateFollowupTimerEntity.builder()
                    .id(timerEntity.getId())
                    .operateAccountId(accountId)
                    .operateAccountName(accountName)
                    .experimentDeadline(setFollowup.getExperimentDeadline())
                    .dueDays(setFollowup.getDueDays())
                    .isFollowup(false)
                    .todoTime(TimeUtil.timeProcess(new Date(), setFollowup.getDueDays()))
                    .setAtTime(new Date())
                    .followupTimes(timerEntity.getFollowupTimes() + 1)
                    .build();
            flag = operateFollowupTimerService.updateById(entity);
        } else {
            //2、插入
            OperateFollowupTimerEntity entity = OperateFollowupTimerEntity.builder()
                    .operateFollowupTimerId(idGenerator.nextIdStr())
                    .appId(setFollowup.getAppId())
                    .experimentInstanceId(setFollowup.getExperimentInstanceId())
                    .experimentGroupId(setFollowup.getExperimentGroupId())
                    .experimentPersonId(setFollowup.getExperimentPersonId())
                    .experimentOrgId(setFollowup.getExperimentOrgId())
                    .indicatorFuncId(setFollowup.getIndicatorFuncId())
                    .experimentViewMonitorFollowupId(setFollowup.getExperimentViewMonitorFollowupId())
                    .experimentFollowupName(setFollowup.getExperimentFollowupName())
                    .followupTime(setFollowup.getFollowupTime())
                    .isFollowup(false)
                    .operateAccountId(accountId)
                    .operateAccountName(accountName)
                    .experimentDeadline(setFollowup.getExperimentDeadline())
                    .dueDays(setFollowup.getDueDays())
                    .todoTime(TimeUtil.timeProcess(new Date(), setFollowup.getDueDays()))
                    .setAtTime(new Date())
                    .followupTimes(timerEntity.getFollowupTimes() == null ? 1 : timerEntity.getFollowupTimes() + 1)
                    .build();
            flag = operateFollowupTimerService.save(entity);
        }
        return flag;
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
    public FollowupStateResponse getFollowupState(FindFollowupStateRequest findFollowupState) {
        return new FollowupStateResponse();
    }

    /**
     * @param
     * @return
     * @说明: 监测随访：开始随访（保存随访记录）
     * @关联表: OperateFollowupTimer, OperateOrgFunc, OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月8号  下午18:29:34
     */
    @DSTransactional
    public Boolean saveFollowup(SaveFollowupRequest saveFollowup,String accountId,String accountName) {
        Boolean flag = false;
        //todo 1、调用夏海接口计算最新指标
        //2、更新随访频率表
        OperateFollowupTimerEntity timerEntity = operateFollowupTimerService.lambdaQuery()
                .eq(OperateFollowupTimerEntity::getAppId, saveFollowup.getAppId())
                .eq(OperateFollowupTimerEntity::getExperimentInstanceId, saveFollowup.getExperimentInstanceId())
                .eq(OperateFollowupTimerEntity::getExperimentGroupId, saveFollowup.getExperimentGroupId())
                .eq(OperateFollowupTimerEntity::getExperimentPersonId, saveFollowup.getExperimentPersonId())
                .eq(OperateFollowupTimerEntity::getExperimentOrgId, saveFollowup.getExperimentOrgId())
                .eq(OperateFollowupTimerEntity::getIndicatorFuncId, saveFollowup.getIndicatorFuncId())
                .eq(OperateFollowupTimerEntity::getExperimentViewMonitorFollowupId, saveFollowup.getExperimentViewMonitorFollowupId())
                .eq(OperateFollowupTimerEntity::getDeleted, false)
                .one();
        if(timerEntity != null && !ReflectUtil.isObjectNull(timerEntity)){
            OperateFollowupTimerEntity entity = OperateFollowupTimerEntity.builder()
                    .id(timerEntity.getId())
                    .operateAccountId(accountId)
                    .operateAccountName(accountName)
                    .experimentDeadline(saveFollowup.getExperimentDeadline())
                    .dueDays(saveFollowup.getDueDays())
                    .isFollowup(true)
                    .todoTime(TimeUtil.timeProcess(new Date(), saveFollowup.getDueDays()))
                    .setAtTime(new Date())
                    .followupTimes(timerEntity.getFollowupTimes() + 1)
                    .build();
            flag = operateFollowupTimerService.updateById(entity);
        }
        //3、调用savePhysiqueAndAuxiliary接口保存记录
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 监测随访：查看随访报告
     * @关联表: OperateOrgFunc, OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public FollowupReportInfoResponse getFollowUpReport(String operateOrgFuncId) {
        return new FollowupReportInfoResponse();
    }

    /**
     * @param
     * @return
     * @说明: 体格检查+辅助检查：获取检查类别+项目
     * @关联表: OperateOrgFunc, OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<CategsWithStateResponse> listOrgViewCategs(FindOrgViewCategsRequest findOrgViewCategs) {
        return new ArrayList<CategsWithStateResponse>();
    }

    /**
     * @param
     * @return
     * @说明: 体格检查+辅助检查：执行检查(开检查单)
     * @关联表: OperateOrgFunc, OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public SaveOrgViewResponse saveOrgView(SaveOrgViewRequest saveOrgView) {
        return new SaveOrgViewResponse();
    }

    /**
     * @param
     * @return
     * @说明: 体格检查+辅助检查：获取最新检查报告
     * @关联表: OperateOrgFunc, OperateOrgFuncSnap
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月01日 下午13:44:34
     */
    public List<OperateOrgFuncSnapRequest> getOrgViewReport(GetOrgViewReportRequest orgViewReport) {
        List<OperateOrgFuncSnapRequest> requestList = new ArrayList<>();
        //1、获取操作记录
        List<OperateOrgFuncEntity> entityList = operateOrgFuncService.lambdaQuery()
                .select(OperateOrgFuncEntity::getOperateOrgFuncId, OperateOrgFuncEntity::getDeleted)
                .eq(OperateOrgFuncEntity::getPeriods, orgViewReport.getPeriods())
                .eq(OperateOrgFuncEntity::getIndicatorFuncId, orgViewReport.getIndicatorFuncId())
                .eq(OperateOrgFuncEntity::getExperimentPersonId, orgViewReport.getExperimentPersonId())
                .eq(OperateOrgFuncEntity::getAppId, orgViewReport.getAppId())
                .eq(OperateOrgFuncEntity::getDeleted, false)
                .list();
        //2、获取操作记录快照
        if (entityList != null && entityList.size() > 0) {
            entityList.forEach(entity -> {
                OperateOrgFuncSnapEntity snapEntity = operateOrgFuncSnapService.lambdaQuery()
                        .eq(OperateOrgFuncSnapEntity::getOperateOrgFuncId, entity.getOperateOrgFuncId())
                        .eq(OperateOrgFuncSnapEntity::getDeleted, false)
                        .one();
                OperateOrgFuncSnapRequest snapRequest = OperateOrgFuncSnapRequest.builder()
                        .operateOrgFuncSnapId(snapEntity.getOperateOrgFuncSnapId())
                        .inputJson(snapEntity.getInputJson())
                        .build();
                requestList.add(snapRequest);
            });
        }
        return requestList;
    }

    /**
     * @param
     * @return
     * @说明: 体格检查+辅助检查：体格检查+辅助检查保存
     * @关联表: OperateOrgFunc, OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年5月31日 下午17:14:34
     */
    @DSTransactional
    public Boolean savePhysiqueAndAuxiliary(List<GetOrgViewReportRequest> reportRequestList, String accountId, String accountName) {
        //1、删除用户以前功能点信息
        List<OperateOrgFuncEntity> entityList = operateOrgFuncService.lambdaQuery()
                .select(OperateOrgFuncEntity::getId, OperateOrgFuncEntity::getDeleted)
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
//                    .asset(??)
//                    .fee(??)
//                    .refund(??)
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
            ExperimentIndicatorViewPhysicalExamEntity entity = experimentIndicatorViewPhysicalExamService.lambdaQuery()
                    .select(ExperimentIndicatorViewPhysicalExamEntity::getExperimentJudgePhysicalExamId, ExperimentIndicatorViewPhysicalExamEntity::getFee)
                    .eq(ExperimentIndicatorViewPhysicalExamEntity::getExperimentJudgePhysicalExamId, reportRequest.getExperimentJudgePhysicalExamId())
                    .eq(ExperimentIndicatorViewPhysicalExamEntity::getStatus, true)
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
            ExperimentIndicatorViewSupportExamEntity entity = experimentIndicatorViewSupportExamService.lambdaQuery()
                    .select(ExperimentIndicatorViewSupportExamEntity::getExperimentJudgeSupportExamId, ExperimentIndicatorViewSupportExamEntity::getFee)
                    .eq(ExperimentIndicatorViewSupportExamEntity::getExperimentJudgeSupportExamId, reportRequest.getExperimentJudgeSupportExamId())
                    .eq(ExperimentIndicatorViewSupportExamEntity::getStatus, true)
                    .one();
            //todo、根据判断规则判断是否满足条件,将指标值返回
            flag.set(false);
        });
        return responseList;
    }

    /**
     * @param
     * @return
     * @说明: 基本信息：查看
     * @关联表: indicatorViewBaseInfo、indicatorViewBaseInfoDescr、indicatorViewBaseInfoDescrRef
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月02日 下午16:26:34
     */
    public Map<String, Object> getIndicatorBaseInfo(String experimentIndicatorViewBaseInfoId,
                                                    String appId,
                                                    String experimentPersonId,
                                                    String periods
    ) {
        Map<String, Object> map = new HashMap<>();
        /**
         * 描述信息表
         *
         */
        //1、根据分布式ID找到指标描述功能点并排序
        List<ExperimentViewBaseInfoDescrEntity> descrList = experimentViewBaseInfoDescrService.lambdaQuery()
                .select(ExperimentViewBaseInfoDescrEntity::getExperimentViewBaseInfoDescrId,
                        ExperimentViewBaseInfoDescrEntity::getName,
                        ExperimentViewBaseInfoDescrEntity::getSeq)
                .eq(ExperimentViewBaseInfoDescrEntity::getAppId, appId)
                .eq(ExperimentViewBaseInfoDescrEntity::getExperimentIndicatorViewBaseInfoId, experimentIndicatorViewBaseInfoId)
                .eq(ExperimentViewBaseInfoDescrEntity::getDeleted, false)
                .list();
        descrList = descrList.stream().sorted(Comparator.comparing(iteam -> iteam.getSeq())).collect(Collectors.toList());
        //2、根据功能点找到指标信息并再次排序
        if (descrList != null && descrList.size() > 0) {
            descrList.forEach(descr -> {
                List<ExperimentViewBaseInfoDescrRefEntity> refEntityList = experimentViewBaseInfoDescrRefService.lambdaQuery()
                        .select(ExperimentViewBaseInfoDescrRefEntity::getIndicatorInstanceId,
                                ExperimentViewBaseInfoDescrRefEntity::getSeq)
                        .eq(ExperimentViewBaseInfoDescrRefEntity::getAppId, appId)
                        .eq(ExperimentViewBaseInfoDescrRefEntity::getExperimentViewBaseInfoDescrId, descr.getExperimentViewBaseInfoDescrId())
                        .eq(ExperimentViewBaseInfoDescrRefEntity::getDeleted, false)
                        .list().stream().sorted(Comparator.comparing(iteam -> iteam.getSeq())).collect(Collectors.toList());
                List<ExperimentIndicatorResponse> responseList = new ArrayList<>();
                if (refEntityList != null && refEntityList.size() > 0) {
                    refEntityList.forEach(refEntity -> {
                        ExperimentIndicatorResponse response = new ExperimentIndicatorResponse();
                        // todo 通过数据库指标实例和experimentPersonId获取experimentIndicatorInstanceId,先假设数据
                        String experimentIndicatorInstanceId = "010101";
                        //2.1、获取指标值
                        ExperimentIndicatorValEntity valEntity = experimentIndicatorValService.lambdaQuery()
                                .eq(ExperimentIndicatorValEntity::getDeleted, false)
                                .eq(ExperimentIndicatorValEntity::getExperimentIndicatorInstanceId, experimentIndicatorInstanceId)
                                .eq(ExperimentIndicatorValEntity::getPeriods, periods)
                                .one();
                        response.setExperimentIndicatorCurrentVal(valEntity.getCurrentVal());
                        //2.2、获取指标单位
                        ExperimentIndicatorInstanceEntity instanceEntity = experimentIndicatorInstanceService.lambdaQuery()
                                .eq(ExperimentIndicatorInstanceEntity::getDeleted, false)
                                .eq(ExperimentIndicatorInstanceEntity::getExperimentIndicatorInstanceId, experimentIndicatorInstanceId)
                                .eq(ExperimentIndicatorInstanceEntity::getExperimentPersonId, experimentPersonId)
                                .one();
                        response.setExperimentIndicatorInstanceName(instanceEntity.getIndicatorName());
                        response.setType(ViewBaseInfoConstant.DESCR);
                        response.setSeq(refEntity.getSeq());
                        responseList.add(response);
                    });
                }
                map.put(descr.getName(), responseList);
            });
        }
        /**
         * 监测信息表
         */
        //3、根据分布式ID找到指标监测功能点并排序
        List<ExperimentViewBaseInfoMonitorEntity> monitorList = experimentViewBaseInfoMonitorService.lambdaQuery()
                .select(ExperimentViewBaseInfoMonitorEntity::getExperimentViewBaseInfoMonitorId,
                        ExperimentViewBaseInfoMonitorEntity::getName,
                        ExperimentViewBaseInfoMonitorEntity::getSeq)
                .eq(ExperimentViewBaseInfoMonitorEntity::getAppId, appId)
                .eq(ExperimentViewBaseInfoMonitorEntity::getExperimentIndicatorViewBaseInfoId, experimentIndicatorViewBaseInfoId)
                .eq(ExperimentViewBaseInfoMonitorEntity::getDeleted, false)
                .list()
                .stream().sorted(Comparator.comparing(iteam -> iteam.getSeq())).collect(Collectors.toList());
        //4、根据功能点找到指标信息并再次排序
        if (monitorList != null && monitorList.size() > 0) {
            monitorList.forEach(monitor -> {
                List<ExperimentViewBaseInfoMonitorContentEntity> contentList = experimentViewBaseInfoMonitorContentService.lambdaQuery()
                        .select(ExperimentViewBaseInfoMonitorContentEntity::getExperimentViewBaseInfoMonitorContentId,
                                ExperimentViewBaseInfoMonitorContentEntity::getSeq)
                        .eq(ExperimentViewBaseInfoMonitorContentEntity::getExperimentViewBaseInfoMonitorId, monitor.getExperimentViewBaseInfoMonitorId())
                        .eq(ExperimentViewBaseInfoMonitorContentEntity::getDeleted, false)
                        .list()
                        .stream().sorted(Comparator.comparing(iteam -> iteam.getSeq())).collect(Collectors.toList());
                List<ExperimentIndicatorResponse> resultList = new ArrayList<>();
                if (contentList != null && contentList.size() > 0) {
                    contentList.forEach(content -> {
                        //4.1、根据内容找到对应功能点
                        ExperimentIndicatorResponse result = new ExperimentIndicatorResponse();
                        //4.2、设置项目内容
                        result.setContent(content.getName());
                        List<ExperimentViewBaseInfoMonitorContentRefEntity> contentRefList = experimentViewBaseInfoMonitorContentRefService.lambdaQuery()
                                .eq(ExperimentViewBaseInfoMonitorContentRefEntity::getExperimentViewBaseInfoMonitorContentId, content.getExperimentViewBaseInfoMonitorContentId())
                                .eq(ExperimentViewBaseInfoMonitorContentRefEntity::getDeleted, false)
                                .list()
                                .stream().sorted(Comparator.comparing(iteam -> iteam.getSeq())).collect(Collectors.toList());
                        if (contentRefList != null && contentRefList.size() > 0) {
                            List<ExperimentIndicatorResponse> responseList = new ArrayList<>();
                            contentRefList.forEach(contentRef -> {
                                ExperimentIndicatorResponse response = new ExperimentIndicatorResponse();
                                // todo 通过数据库指标实例和experimentPersonId获取experimentIndicatorInstanceId,先假设数据
                                String experimentIndicatorInstanceId = "010102";
                                //4.3、获取指标值
                                ExperimentIndicatorValEntity valEntity = experimentIndicatorValService.lambdaQuery()
                                        .eq(ExperimentIndicatorValEntity::getDeleted, false)
                                        .eq(ExperimentIndicatorValEntity::getExperimentIndicatorInstanceId, experimentIndicatorInstanceId)
                                        .eq(ExperimentIndicatorValEntity::getPeriods, periods)
                                        .one();
                                response.setExperimentIndicatorCurrentVal(valEntity.getCurrentVal());
                                //4.4、获取指标单位
                                ExperimentIndicatorInstanceEntity instanceEntity = experimentIndicatorInstanceService.lambdaQuery()
                                        .eq(ExperimentIndicatorInstanceEntity::getDeleted, false)
                                        .eq(ExperimentIndicatorInstanceEntity::getExperimentIndicatorInstanceId, experimentIndicatorInstanceId)
                                        .eq(ExperimentIndicatorInstanceEntity::getExperimentPersonId, experimentPersonId)
                                        .one();
                                response.setUnit(instanceEntity.getUnit());
                                response.setExperimentIndicatorInstanceName(instanceEntity.getIndicatorName());
                                responseList.add(response);
                            });
                            //4.5、设置指标值
                            result.setIndicatorList(responseList);
                            result.setType(ViewBaseInfoConstant.MONITOR);
                            result.setSeq(content.getSeq());
                            resultList.add(result);
                        }
                    });
                }
                map.put(monitor.getName(), resultList);
            });
        }

        /**
         * 单一指标值
         */
        //5、查看单一指标值
        List<ExperimentViewBaseInfoSingleEntity> singleList = experimentViewBaseInfoSingleService.lambdaQuery()
                .select(ExperimentViewBaseInfoSingleEntity::getExperimentViewBaseInfoSingleId,
                        ExperimentViewBaseInfoSingleEntity::getSeq)
                .eq(ExperimentViewBaseInfoSingleEntity::getAppId, appId)
                .eq(ExperimentViewBaseInfoSingleEntity::getExperimentIndicatorViewBaseInfoId, experimentIndicatorViewBaseInfoId)
                .eq(ExperimentViewBaseInfoSingleEntity::getDeleted, false)
                .list()
                .stream().sorted(Comparator.comparing(iteam -> iteam.getSeq())).collect(Collectors.toList());
        if (singleList != null && singleList.size() > 0) {
            List<ExperimentIndicatorResponse> responseList = new ArrayList<>();
            singleList.forEach(single -> {
                ExperimentIndicatorResponse response = new ExperimentIndicatorResponse();
                // todo 通过数据库指标实例和experimentPersonId获取experimentIndicatorInstanceId,先假设数据
                String experimentIndicatorInstanceId = "010103";
                //5.1、获取指标值
                ExperimentIndicatorValEntity valEntity = experimentIndicatorValService.lambdaQuery()
                        .eq(ExperimentIndicatorValEntity::getDeleted, false)
                        .eq(ExperimentIndicatorValEntity::getExperimentIndicatorInstanceId, experimentIndicatorInstanceId)
                        .eq(ExperimentIndicatorValEntity::getPeriods, periods)
                        .one();
                response.setExperimentIndicatorCurrentVal(valEntity.getCurrentVal());
                //5.2、获取指标单位
                ExperimentIndicatorInstanceEntity instanceEntity = experimentIndicatorInstanceService.lambdaQuery()
                        .eq(ExperimentIndicatorInstanceEntity::getDeleted, false)
                        .eq(ExperimentIndicatorInstanceEntity::getExperimentIndicatorInstanceId, experimentIndicatorInstanceId)
                        .eq(ExperimentIndicatorInstanceEntity::getExperimentPersonId, experimentPersonId)
                        .one();
                response.setExperimentIndicatorInstanceName(instanceEntity.getIndicatorName());
                response.setType(ViewBaseInfoConstant.SINGLE);
                response.setSeq(single.getSeq());
                responseList.add(response);
            });
            map.put("单一表", responseList);
        }
        return map;
    }

    /**
     * @param
     * @return
     * @说明: 二级类别：根据指标分类ID获取所有符合条件的数据
     * @关联表: experimentIndicatorViewPhysicalExam
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月05日 下午17:40:34
     */
    public List<ExperimentIndicatorJudgePhysicalExamResponse> getIndicatorViewPhysicalExamByCategoryIds(Set<String> experimentIndicatoryCategoryIds) {
        //1、根据指标分类ID获取所有符合条件的数据
        List<ExperimentIndicatorViewPhysicalExamEntity> entityList = experimentIndicatorViewPhysicalExamService.lambdaQuery()
                .select(ExperimentIndicatorViewPhysicalExamEntity::getId,
                        ExperimentIndicatorViewPhysicalExamEntity::getExperimentJudgePhysicalExamId,
                        ExperimentIndicatorViewPhysicalExamEntity::getIndicatorViewPhysicalExamId,
                        ExperimentIndicatorViewPhysicalExamEntity::getName,
                        ExperimentIndicatorViewPhysicalExamEntity::getExperimentIndicatorCategoryId)
                .in(ExperimentIndicatorViewPhysicalExamEntity::getExperimentIndicatorCategoryId, experimentIndicatoryCategoryIds)
                .eq(ExperimentIndicatorViewPhysicalExamEntity::getStatus, true)
                .list();
        List<ExperimentIndicatorJudgePhysicalExamResponse> responseList = new ArrayList<>();
        if (entityList != null && entityList.size() > 0) {
            entityList.forEach(entity -> {
                ExperimentIndicatorJudgePhysicalExamResponse response = ExperimentIndicatorJudgePhysicalExamResponse
                        .builder()
                        .id(entity.getId())
                        .experimentJudgePhysicalExamId(entity.getExperimentJudgePhysicalExamId())
                        .indicatorJudgePhysicalExamId(entity.getIndicatorViewPhysicalExamId())
                        .name(entity.getName())
                        .indicatorCategoryId(entity.getExperimentIndicatorCategoryId())
                        .build();
                responseList.add(response);
            });
        }
        return responseList;
    }

    /**
     * @param
     * @return
     * @说明: 四级类别：根据指标分类ID获取所有符合条件的数据
     * @关联表: experimentIndicatorViewPhysicalExam
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月05日 下午17:40:34
     */
    public List<ExperimentIndicatorJudgeSupportExamResponse> getIndicatorViewSupportExamByCategoryIds(Set<String> experimentIndicatoryCategoryIds) {
        //1、根据指标分类ID获取所有符合条件的数据
        List<ExperimentIndicatorViewSupportExamEntity> entityList = experimentIndicatorViewSupportExamService.lambdaQuery()
                .select(ExperimentIndicatorViewSupportExamEntity::getId,
                        ExperimentIndicatorViewSupportExamEntity::getExperimentJudgeSupportExamId,
                        ExperimentIndicatorViewSupportExamEntity::getIndicatorViewSupportExamId,
                        ExperimentIndicatorViewSupportExamEntity::getName,
                        ExperimentIndicatorViewSupportExamEntity::getExperimentIndicatorCategoryId)
                .in(ExperimentIndicatorViewSupportExamEntity::getExperimentIndicatorCategoryId, experimentIndicatoryCategoryIds)
                .eq(ExperimentIndicatorViewSupportExamEntity::getStatus, true)
                .list();
        List<ExperimentIndicatorJudgeSupportExamResponse> responseList = new ArrayList<>();
        if (entityList != null && entityList.size() > 0) {
            entityList.forEach(entity -> {
                ExperimentIndicatorJudgeSupportExamResponse response = ExperimentIndicatorJudgeSupportExamResponse
                        .builder()
                        .id(entity.getId())
                        .experimentJudgeSupportExamId(entity.getExperimentJudgeSupportExamId())
                        .indicatorJudgeSupportExamId(entity.getIndicatorViewSupportExamId())
                        .name(entity.getName())
                        .indicatorCategoryId(entity.getExperimentIndicatorCategoryId())
                        .build();
                responseList.add(response);
            });
        }
        return responseList;
    }

    /**
     * @param
     * @return
     * @说明: 监测随访：实验暂停导致时间延后
     * @关联表: operateFollowupTimer
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月10日 下午18:30:34
     */
    @DSTransactional
    public Boolean delayFollowupTimer(long diffTime, String appId,String experimentInstanceId, String accountId, String accountName) {
        List<OperateFollowupTimerEntity> entityList = operateFollowupTimerService.lambdaQuery()
                .select(OperateFollowupTimerEntity::getId)
                .eq(OperateFollowupTimerEntity::getAppId, appId)
                .eq(OperateFollowupTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(OperateFollowupTimerEntity::getIsFollowup, false)
                .eq(OperateFollowupTimerEntity::getDeleted, false)
                .list();
        if(entityList != null && entityList.size() > 0){
            entityList.forEach(entity->{
                //1、批量加时间
                entity.setFollowupTime(TimeUtil.addTimeByLong(entity.getFollowupTime(),diffTime));
                entity.setOperateAccountId(accountId);
                entity.setOperateAccountName(accountName);
            });
        }
        return operateFollowupTimerService.updateBatchById(entityList);
    }
}