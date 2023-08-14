package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.AccountUserApi;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.enums.EnumEventActionState;
import org.dows.hep.api.enums.EnumExperimentEventState;
import org.dows.hep.api.enums.EnumExperimentOrgNoticeType;
import org.dows.hep.api.enums.EnumOrgFeeType;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.api.user.experiment.vo.ExptOrgNoticeActionVO;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.biz.dao.ExperimentEventDao;
import org.dows.hep.biz.dao.ExperimentOrgNoticeDao;
import org.dows.hep.biz.dao.ExperimentPersonDao;
import org.dows.hep.biz.dao.OperateFlowDao;
import org.dows.hep.biz.event.ExperimentEventRules;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.ExperimentEventBox;
import org.dows.hep.biz.vo.ExperimentOrgNoticeBox;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
import org.dows.hep.service.ExperimentPersonService;
import org.dows.sequence.api.IdGenerator;
import org.dows.user.api.api.UserInstanceApi;
import org.dows.user.api.response.UserInstanceResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

/**
* @description project descr:实验:机构操作
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Slf4j
@RequiredArgsConstructor
@Service
public class ExperimentOrgBiz{
    private final ExperimentPersonService experimentPersonService;

    private final AccountUserApi accountUserApi;

    private final UserInstanceApi userInstanceApi;

    private final OperateFlowDao operateFlowDao;

    private final ExperimentOrgNoticeDao experimentOrgNoticeDao;

    private final ExperimentEventDao experimentEventDao;

    private final ExperimentPersonDao experimentPersonDao;

    private final ExperimentOrgNoticeBiz experimentOrgNoticeBiz;

    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;

    private final OperateCostBiz operateCostBiz;

    private final IdGenerator idGenerator;

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
    @DSTransactional
    public Boolean startOrgFlow(StartOrgFlowRequest startOrgFlow, HttpServletRequest request ) {
        ExptRequestValidator validator=ExptRequestValidator.create(startOrgFlow)
                .checkExperimentPerson()
                .checkExperimentOrg()
                .checkExperimentInstance();
        //校验登录
        LoginContextVO voLogin= ShareBiz.getLoginUser(request);
        //校验挂号
        final LocalDateTime ldtNow=LocalDateTime.now();
        final Date dateNow=ShareUtil.XDate.localDT2Date(ldtNow);
        ExperimentTimePoint timePoint=validator.getTimePoint(true, ldtNow, true);
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(validator);
        OperateFlowEntity  rowFlow=flowValidator.getOrgFlow(false);
        AssertUtil.trueThenThrow(flowValidator.ifOrgFlowRunning(rowFlow,timePoint.getPeriod()))
                .throwMessage("当前已挂过号");
        Double ghf= flowValidator.getOrgFee4Ghf().orElse(0d);

        //检验资金,扣费
        ExperimentIndicatorValRsEntity costIndicator=experimentIndicatorInstanceRsBiz.getChangeMoney(RsChangeMoneyRequest.builder()
                .appId(startOrgFlow.getAppId())
                .experimentId(startOrgFlow.getExperimentInstanceId())
                .experimentPersonId(startOrgFlow.getExperimentPersonId())
                .periods(startOrgFlow.getPeriods())
                .moneyChange(BigDecimal.valueOf(ghf).negate())
                .assertEnough(true)
                .build());
        String operateFlowId = idGenerator.nextIdStr();
        rowFlow=createRowOrgFlow(validator)
                .setReportFlag(0)
                .setOperateFlowId(operateFlowId)
                .setPeriods(timePoint.getPeriod())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setFee(BigDecimalUtil.valueOf(ghf))
                .setStartTime(dateNow)
                .setOperateTime(dateNow)
                .setOperateGameDay(timePoint.getGameDay());
        //保存消费记录
        CostRequest costRequest = CostRequest.builder()
                .operateCostId(idGenerator.nextIdStr())
                .experimentInstanceId(validator.getExperimentInstanceId())
                .experimentGroupId(validator.getExperimentGroupId())
                .operatorId(voLogin.getAccountId())
                .experimentOrgId(startOrgFlow.getExperimentOrgId())
                .operateFlowId(operateFlowId)
                .patientId(startOrgFlow.getExperimentPersonId())
                .feeName(EnumOrgFeeType.GHF.getName())
                .feeCode(EnumOrgFeeType.GHF.getCode())
                .cost(BigDecimalUtil.valueOf(ghf))
                .period(startOrgFlow.getPeriods())
                .build();
        return operateFlowDao.tranSave(rowFlow, null,false,()->{
            experimentIndicatorInstanceRsBiz.saveChangeMoney(costIndicator);
            operateCostBiz.saveCost(costRequest);
            return true;
        });
    }
    private OperateFlowEntity createRowOrgFlow(ExptRequestValidator req){
        return OperateFlowEntity.builder()
                .appId(req.getAppId())
                .experimentInstanceId(req.getExperimentInstanceId())
                .experimentGroupId(req.getExperimentGroupId())
                .experimentOrgId(req.getExperimentOrgId())
                .experimentPersonId(req.getExperimentPersonId())
                .periods(req.getPeriods())
                .flowName(req.getCachedExptOrg().get().getCaseOrgName().concat("挂号"))
                .build();
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
    public Page<OrgNoticeResponse> pageOrgNotice(FindOrgNoticeRequest findOrgNotice ) {
        ExptRequestValidator.create(findOrgNotice)
                .checkExperimentOrgId()
                .checkExperimentGroup();
        if (ShareUtil.XObject.notEmpty(findOrgNotice.getExperimentPersonId())) {
            findOrgNotice.setExperimentPersonIds(List.of(findOrgNotice.getExperimentPersonId()));
        } else {
            findOrgNotice.setExperimentPersonIds(ShareUtil.XCollection.map(experimentPersonDao.getByOrgId(findOrgNotice.getExperimentOrgId(),
                    ExperimentPersonEntity::getExperimentPersonId), ExperimentPersonEntity::getExperimentPersonId));
        }
        return ShareBiz.buildPage(experimentOrgNoticeDao.pageByCondition(findOrgNotice,
                ExperimentOrgNoticeEntity::getId,
                ExperimentOrgNoticeEntity::getExperimentOrgNoticeId,
                ExperimentOrgNoticeEntity::getExperimentPersonId,
                ExperimentOrgNoticeEntity::getPersonName,
                ExperimentOrgNoticeEntity::getAvatar,
                ExperimentOrgNoticeEntity::getPeriods,
                ExperimentOrgNoticeEntity::getGameDay,
                ExperimentOrgNoticeEntity::getNoticeTime,
                ExperimentOrgNoticeEntity::getNoticeSrcType,
                ExperimentOrgNoticeEntity::getTitle,
                ExperimentOrgNoticeEntity::getContent,
                //ExperimentOrgNoticeEntity::getTips,
                ExperimentOrgNoticeEntity::getReadState,
                ExperimentOrgNoticeEntity::getActionState
        ), i -> CopyWrapper.create(OrgNoticeResponse::new).endFrom(i));
    }

    /**
     * 获取机构通知详情（主要是事件操作提示+处理措施列表）
     * @param findOrgNotice
     * @return
     * @throws JsonProcessingException
     */

    public OrgNoticeResponse getOrgNotice(FindOrgNoticeRequest findOrgNotice) throws JsonProcessingException{
        ExperimentOrgNoticeEntity rowNotice= AssertUtil.getNotNull(experimentOrgNoticeDao.getById(findOrgNotice.getExperimentOrgNoticeId(),
                ExperimentOrgNoticeEntity::getId,
                ExperimentOrgNoticeEntity::getExperimentOrgNoticeId,
                ExperimentOrgNoticeEntity::getExperimentPersonId,
                ExperimentOrgNoticeEntity::getPersonName,
                ExperimentOrgNoticeEntity::getAvatar,
                ExperimentOrgNoticeEntity::getPeriods,
                ExperimentOrgNoticeEntity::getGameDay,
                ExperimentOrgNoticeEntity::getNoticeTime,
                ExperimentOrgNoticeEntity::getNoticeSrcType,
                ExperimentOrgNoticeEntity::getTitle,
                ExperimentOrgNoticeEntity::getContent,
                ExperimentOrgNoticeEntity::getTips,
                ExperimentOrgNoticeEntity::getReadState,
                ExperimentOrgNoticeEntity::getActionState,
                ExperimentOrgNoticeEntity::getEventActions
        )).orElseThrow("未找到机构通知信息");
        return experimentOrgNoticeBiz.CreateOrgNoticeResponse(rowNotice);
    }

    /**
     * 处理突发事件
     *
     * @param saveNoticeAction
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    public OrgNoticeResponse saveOrgNoticeAction(SaveNoticeActionRequest saveNoticeAction, HttpServletRequest request) throws JsonProcessingException {
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveNoticeAction.getActions()))
                .throwMessage("请选择事件处理措施");
        //校验登录
        LoginContextVO voLogin= ShareBiz.getLoginUser(request);
        ExperimentOrgNoticeEntity rowNotice= AssertUtil.getNotNull(experimentOrgNoticeDao.getById(saveNoticeAction.getExperimentOrgNoticeId(),
                ExperimentOrgNoticeEntity::getId,
                ExperimentOrgNoticeEntity::getExperimentOrgNoticeId,
                ExperimentOrgNoticeEntity::getExperimentInstanceId,
                ExperimentOrgNoticeEntity::getExperimentPersonId,
                ExperimentOrgNoticeEntity::getPersonName,
                ExperimentOrgNoticeEntity::getAvatar,
                ExperimentOrgNoticeEntity::getPeriods,
                ExperimentOrgNoticeEntity::getGameDay,
                ExperimentOrgNoticeEntity::getNoticeTime,
                ExperimentOrgNoticeEntity::getNoticeSrcType,
                ExperimentOrgNoticeEntity::getNoticeSrcId,
                ExperimentOrgNoticeEntity::getTitle,
                ExperimentOrgNoticeEntity::getContent,
                ExperimentOrgNoticeEntity::getTips,
                ExperimentOrgNoticeEntity::getReadState,
                ExperimentOrgNoticeEntity::getActionState,
                ExperimentOrgNoticeEntity::getEventActions
        )).orElseThrow("未找到机构通知信息");
        saveNoticeAction.setExperimentInstanceId(rowNotice.getExperimentInstanceId());
        ExptRequestValidator validator=ExptRequestValidator.create(saveNoticeAction)
                .checkExperimentInstanceId();

        AssertUtil.trueThenThrow(EnumEventActionState.DONE.getCode().equals(rowNotice.getActionState()))
                .throwMessage("该事件已处理");
        AssertUtil.trueThenThrow(!EnumExperimentOrgNoticeType.EVENTTriggered.getCode().equals(rowNotice.getNoticeSrcType())
                ||ShareUtil.XObject.isEmpty(rowNotice.getNoticeSrcId()))
                .throwMessage("未找到突发事件id");
        ExperimentEventEntity rowEvent=AssertUtil.getNotNull(experimentEventDao.getById(rowNotice.getNoticeSrcId(),
                ExperimentEventEntity::getId,
                ExperimentEventEntity::getExperimentEventId,
                ExperimentEventEntity::getCaseEventId,
                ExperimentEventEntity::getExperimentInstanceId,
                ExperimentEventEntity::getExperimentPersonId,
                ExperimentEventEntity::getState
                )).orElseThrow("未找到突发事件信息");
        ExperimentOrgNoticeBox noticeBox=ExperimentOrgNoticeBox.create(rowNotice);
        List<ExptOrgNoticeActionVO> actions=noticeBox.fromActionsJson(true);
        Map<String,ExptOrgNoticeActionVO> mapAction=ShareUtil.XCollection.toMap(actions, ExptOrgNoticeActionVO::getCaseEventActionId);
        final List<String> actedIds=new ArrayList<>();
        for(ExptOrgNoticeActionVO item:saveNoticeAction.getActions()){
            ExptOrgNoticeActionVO vAction=mapAction.get(item.getCaseEventActionId());
            AssertUtil.trueThenThrow(null==vAction)
                    .throwMessage(String.format("未找到处理措施[%s]的定义", item.getActionDesc()));
            vAction.setActedFlag(Optional.ofNullable(item.getActedFlag()).orElse(0)>0?1:0);
            if(vAction.getActedFlag()>0){
                actedIds.add(vAction.getCaseEventActionId());
            }
        }
        AssertUtil.trueThenThrow(actedIds.size()==0)
                .throwMessage("请选择事件处理措施");

        noticeBox.toActionsJson(true);
        rowNotice.setActionState(EnumEventActionState.DONE.getCode())
                .setReadState(1);
        final LocalDateTime ldtNow=LocalDateTime.now();
        final Date dateNow=ShareUtil.XDate.localDT2Date(ldtNow);
        ExperimentTimePoint timePoint=validator.getTimePoint(true, ldtNow, true);
        ExperimentEventBox eventBox=ExperimentEventBox.create(rowEvent);
        eventBox.setActionJsonData(noticeBox.getJsonData()).toActionJson(true);
        rowEvent.setActionAccountId(voLogin.getAccountId())
                .setActionAccountName(voLogin.getAccountName())
                .setActionTime(dateNow)
                .setActionPeriod(timePoint.getPeriod())
                .setActionGameDay(timePoint.getGameDay())
                .setState(EnumExperimentEventState.USERAction.getCode());
        if(!ExperimentEventRules.Instance().saveActionEvent(rowEvent, rowNotice,actedIds)){
            return null;
        }
        return experimentOrgNoticeBiz.CreateOrgNoticeResponse(noticeBox);

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
    public Page<OrgFlowReportResponse> pageOrgReport(FindOrgReportRequest findOrgReport) {
        return ShareBiz.buildPage(operateFlowDao.pageByCondition(findOrgReport), i-> CopyWrapper.create(OrgFlowReportResponse::new)
                .endFrom(i));
    }


    /**
     * 获取机构报告详情
     * @param orgReportRequest
     * @return
     */
    public ExptOrgFlowReportResponse getOrgReportInfo(ExptOrgFlowReportRequest orgReportRequest) {
        return getReportSnapData(orgReportRequest, ExptOrgFlowReportResponse.class, ExptOrgFlowReportResponse::new);
    }

    private <T> T getReportSnapData(ExptOrgFlowReportRequest orgReportRequest, Class<T> clazz, Supplier<T> creator){
        T rst=creator.get();
        ExptRequestValidator.create(orgReportRequest)
                .checkExperimentOrgId()
                .checkExperimentInstanceId();
        final String flowId=orgReportRequest.getOperateFlowId();
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(flowId))
                .throwMessage("未找到挂号记录ID");
        OperateFlowSnapEntity rowFlowSnap=AssertUtil.getNotNull(operateFlowDao.getReportSnapByFlowId(flowId,
                OperateFlowSnapEntity::getRecordJson
                )).orElseThrow("未找到报告记录");
        if(ShareUtil.XObject.isEmpty(rowFlowSnap.getRecordJson())) {
            return rst;
        }
        try{
            return JacksonUtil.fromJson(rowFlowSnap.getRecordJson(), clazz);
        }catch (Exception ex) {
            AssertUtil.justThrow(String.format("机构报告数据解析失败：%s", ex.getMessage()), ex);
        }
        return rst;
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

    /**
     * @param
     * @return
     * @说明: 获取实验人物列表
     * @关联表: experiment_person
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月10日 上午10:11:34
     */
    public Page<ExperimentPersonResponse> pageExperimentPersons(ExperimentPersonRequest personRequest) {
        List<ExperimentPersonResponse> responseList = new ArrayList<>();
        LambdaQueryWrapper<ExperimentPersonEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExperimentPersonEntity::getExperimentOrgId, personRequest.getExperimentOrgId())
                .orderByDesc(ExperimentPersonEntity::getDt);
        Page<ExperimentPersonEntity> page = new Page<>(personRequest.getPageNo(), personRequest.getPageSize());
        IPage<ExperimentPersonEntity> entityIPage = experimentPersonService.page(page, queryWrapper);

        //获取人物挂号状态
        ExperimentTimePoint timePoint=ExperimentSettingCache.Instance().getTimePointByRealTime(ExperimentCacheKey.create(personRequest.getAppId(),personRequest.getExperimentInstanceId()),
                LocalDateTime.now(), false);
        final Integer period=timePoint.getPeriod();
        List<String> personIds=ShareUtil.XCollection.map(entityIPage.getRecords(),ExperimentPersonEntity::getExperimentPersonId);
        List<OperateFlowEntity> rowsFlow=operateFlowDao.getCurrentFlowList(personRequest.getExperimentOrgId(),personIds,period,
                OperateFlowEntity::getExperimentPersonId,
                OperateFlowEntity::getOperateFlowId,
                OperateFlowEntity::getPeriods);
        Map<String,OperateFlowEntity> mapFlow=ShareUtil.XCollection.toMap(rowsFlow, OperateFlowEntity::getExperimentPersonId);


        //复制
        Page<ExperimentPersonResponse> voPage = new Page<>();
        BeanUtils.copyProperties(entityIPage, voPage, new String[]{"records"});
        for(ExperimentPersonEntity entity : entityIPage.getRecords()){
            ExperimentPersonResponse person = new ExperimentPersonResponse();
            BeanUtil.copyProperties(entity,person);
            person.setId(entity.getId().toString());
            // 1、获取用户姓名
            String userId = accountUserApi.getUserByAccountId(entity.getAccountId()).getUserId();
            UserInstanceResponse instanceResponse = userInstanceApi.getUserInstanceByUserId(userId);
            String userName = instanceResponse.getName();
            // 2、获取用户头像
            person.setAvatar(instanceResponse.getAvatar());
            person.setName(userName);
            //设置挂号状态
            OperateFlowEntity rowFlow=mapFlow.get(person.getExperimentPersonId());
            if(null!=rowFlow){
                person.setOperateFlowId(rowFlow.getOperateFlowId());
                person.setFlowPeriod(rowFlow.getPeriods());
            }
            // 获取健康指数
            String healthPoint = experimentIndicatorInstanceRsBiz.getHealthPoint(personRequest.getPeriods(),person.getExperimentPersonId());
            person.setHealthPoint(healthPoint);
            responseList.add(person);
        }
        voPage.setRecords(responseList);
        return voPage;
    }

    public Integer countExperimentPersons(ExperimentPersonRequest personRequest) {
        List<ExperimentPersonEntity> personEntityList = experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentInstanceId, personRequest.getExperimentInstanceId())
                .eq(ExperimentPersonEntity::getExperimentGroupId,personRequest.getExperimentGroupId())
                .eq(ExperimentPersonEntity::getDeleted,false)
                .list();
        return personEntityList.size();
    }
}