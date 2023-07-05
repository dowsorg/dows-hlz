package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.AccountUserApi;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.dao.OperateFlowDao;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.entity.OperateFlowEntity;
import org.dows.hep.entity.OperateFlowSnapEntity;
import org.dows.hep.service.ExperimentPersonService;
import org.dows.user.api.api.UserInstanceApi;
import org.dows.user.api.response.UserInstanceResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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
    public Boolean startOrgFlow(StartOrgFlowRequest startOrgFlow, HttpServletRequest request ) {
        ExptRequestValidator validator=ExptRequestValidator.create(startOrgFlow);
        validator.checkExperimentPerson()
                .checkExperimentOrg()
                .checkExperimentInstance();
        //校验登录
        LoginContextVO voLogin= ShareBiz.getLoginUser(request);
        //校验挂号
        final LocalDateTime ldtNow=LocalDateTime.now();
        final Date dateNow=ShareUtil.XDate.localDT2Date(ldtNow);
        ExperimentTimePoint timePoint= ExperimentSettingCache.Instance().getTimePointByRealTime(ExperimentCacheKey.create(validator.getAppId(), validator.getExperimentInstanceId()),
                ldtNow,true);
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(validator);
        OperateFlowEntity  rowFlow=flowValidator.getOrgFlow(false);
        AssertUtil.trueThenThrow(flowValidator.ifOrgFlowRunning(rowFlow,timePoint.getPeriod()))
                .throwMessage("当前已挂过号，无需重复操作");
        //TODO 检验资金,扣费
        BigDecimal asset=BigDecimal.ZERO;
        BigDecimal refund=BigDecimal.ZERO;
        Double ghf= flowValidator.getOrgFee4Ghf().orElse(-1d);
        AssertUtil.trueThenThrow(ghf<=0)
                .throwMessage("未找到有效的挂号费设置");
        rowFlow=createRowOrgFlow(validator)
                .setPeriods(timePoint.getPeriod())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setFee(BigDecimalUtil.valueOf(ghf))
                .setAsset(asset)
                .setRefund(refund)
                .setStartTime(dateNow)
                .setOperateTime(dateNow)
                .setOperateGameDay(timePoint.getGameDay());
        OperateFlowSnapEntity rowFlowSnap=OperateFlowSnapEntity.builder()
                .appId(validator.getAppId())
                .build();
        return operateFlowDao.tranSave(rowFlow, Arrays.asList(rowFlowSnap),false);
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
    public IPage<ExperimentPersonResponse> pageExperimentPersons(ExperimentPersonRequest personRequest) {
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
        IPage<ExperimentPersonResponse> voPage = new Page<>();
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
            responseList.add(person);

        }
        voPage.setRecords(responseList);
        return voPage;
    }
}