package org.dows.hep.biz.user.person;

import lombok.RequiredArgsConstructor;
import org.dows.account.api.AccountInstanceApi;
import org.dows.account.api.AccountOrgApi;
import org.dows.account.api.AccountOrgGeoApi;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountOrgGeoResponse;
import org.dows.account.response.AccountOrgInfoResponse;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.user.experiment.request.ExperimentPersonRequest;
import org.dows.hep.api.user.experiment.response.ExperimentOrgResponse;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.biz.dao.CaseOrgFeeDao;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author jx
 * @date 2023/5/8 13:37
 */
@Service
@RequiredArgsConstructor
public class PersonStatiscBiz {

    private final CaseOrgService caseOrgService;

    private final CasePersonService casePersonService;

    private final AccountInstanceApi accountInstanceApi;

    private final AccountOrgGeoApi accountOrgGeoApi;

    private final ExperimentParticipatorService experimentParticipatorService;

    private final ExperimentGroupService experimentGroupService;

    private final ExperimentOrgService experimentOrgService;

    private final CaseOrgFeeDao caseOrgFeeDao;

    private final AccountOrgApi accountOrgApi;

    private final ExperimentTimerBiz experimentTimerBiz;

    private final ExperimentPersonService experimentPersonService;

    private final OperateCostBiz operateCostBiz;

    private final OperateInsuranceService operateInsuranceService;

    private final CaseOrgFeeService caseOrgFeeService;

    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;

    /**
     * @param
     * @return
     * @说明: 获取社区人数
     * @关联表: experiment_person、experiment_org
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 上午13:57:34
     */
    public Integer countExperimentPersons(String experimentInstanceId) {
        Integer count = 0;
        //1、获取案例中的已经被开启的机构
        List<ExperimentOrgEntity> experimentOrgList = experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentOrgEntity::getDeleted, false)
                .list();
        if (experimentOrgList != null && experimentOrgList.size() > 0) {
            List<CaseOrgEntity> orgList = caseOrgService.lambdaQuery()
                    .eq(CaseOrgEntity::getCaseOrgId, experimentOrgList.get(0).getCaseOrgId())
                    .eq(CaseOrgEntity::getDeleted, false)
                    .list();
            //2、遍历机构，获取每个机构中已经开启的案例人物
            if (orgList != null && orgList.size() > 0) {
                for (CaseOrgEntity org : orgList) {
                    List<CasePersonEntity> personList = casePersonService.lambdaQuery()
                            .eq(CasePersonEntity::getCaseOrgId, org.getCaseOrgId())
                            .eq(CasePersonEntity::getDeleted, false)
                            .list();
                    if (personList != null && personList.size() > 0) {
                        for (CasePersonEntity person : personList) {
                            AccountInstanceResponse instance = accountInstanceApi.getAccountInstanceByAccountId(person.getAccountId());
                            if (instance != null && !ReflectUtil.isObjectNull(instance)) {
                                if (instance.getStatus() == 1) {
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * @param
     * @return
     * @说明: 获取实验机构
     * @关联表: experiment_org、account_org_geo
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 上午13:57:34
     */
    public List<ExperimentOrgResponse> listExperimentOrgs(String experimentInstanceId, String experimentGroupId) {
        List<ExperimentOrgResponse> orgResponses = new ArrayList<>();
        //1、根据实验找到案例机构ID
        List<ExperimentOrgEntity> experimentOrgList = experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentOrgEntity::getExperimentGroupId, experimentGroupId)
                .eq(ExperimentOrgEntity::getDeleted, false)
                .list();
        if (experimentOrgList != null && experimentOrgList.size() > 0) {
            //2、获取机构的经纬度信息
            experimentOrgList.forEach(org -> {
                ExperimentOrgResponse orgResponse = (ExperimentOrgResponse) new ExperimentOrgResponse()
                        .setHandbook(org.getHandbook())
                        .setCaseOrgId(org.getCaseOrgId())
                        .setExperimentOrgId(org.getExperimentOrgId())
                        .setOrgId(org.getOrgId());

                AccountOrgGeoResponse orgGeo = accountOrgGeoApi.getAccountOrgInfoByOrgId(org.getOrgId());
                orgResponse.setOrgLatitude(orgGeo.getOrgLatitude());
                orgResponse.setOrgLongitude(orgGeo.getOrgLongitude());
                orgResponse.setOrgName(orgGeo.getOrgName());
                //获取是否开启数字档案
                AccountOrgInfoResponse orgInfoResponse = accountOrgApi.getAccountOrgInfoByOrgId(org.getOrgId());
                orgResponse.setIsEnable(orgInfoResponse.getIsEnable());
                if (orgInfoResponse.getIsEnable() == null) {
                    orgResponse.setIsEnable(false);
                }
                orgResponses.add(orgResponse);
            });
        }
        List<String> orgIds = ShareUtil.XCollection.map(orgResponses, ExperimentOrgResponse::getOrgId);
        List<CaseOrgFeeEntity> rowsFee = caseOrgFeeDao.getFeeList(orgIds,
                CaseOrgFeeEntity::getCaseOrgId,
                CaseOrgFeeEntity::getFeeCode,
                CaseOrgFeeEntity::getFeeName,
                CaseOrgFeeEntity::getFee,
                CaseOrgFeeEntity::getReimburseRatio
        );
        Map<String, List<ExperimentOrgResponse.ExperimentOrgFeeResponse>> mapFee = ShareUtil.XCollection.groupBy(rowsFee,
                i -> CopyWrapper.create(ExperimentOrgResponse.ExperimentOrgFeeResponse::new).endFrom(i),
                ExperimentOrgResponse.ExperimentOrgFeeResponse::getCaseOrgId);
        orgResponses.forEach(i -> i.setFeeList(mapFee.get(i.getOrgId())));
        rowsFee.clear();
        mapFee.clear();
        return orgResponses;
    }

    /**
     * @param
     * @return
     * @说明: 获取参与者信息
     * @关联表: experiment_participator、account_instance
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 下午16:35:34
     */
    public ExperimentParticipatorResponse getParticipatorInfo(String experimentParticipatorId) {
        ExperimentParticipatorResponse experimentParticipatorResponse = new ExperimentParticipatorResponse();
        ExperimentParticipatorEntity participatorEntity = experimentParticipatorService
                .lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentParticipatorId, experimentParticipatorId)
                .eq(ExperimentParticipatorEntity::getDeleted, false)
                .one();
        //1、获取参与者小组信息
        if (participatorEntity != null && !ReflectUtil.isObjectNull(participatorEntity)) {
            experimentParticipatorResponse.setAccountId(participatorEntity.getAccountId());
            experimentParticipatorResponse.setAccountName(participatorEntity.getAccountName());
            ExperimentGroupEntity groupEntity = experimentGroupService
                    .lambdaQuery()
                    .eq(ExperimentGroupEntity::getDeleted, false)
                    .eq(ExperimentGroupEntity::getExperimentGroupId, participatorEntity.getExperimentGroupId())
                    .one();
            if (groupEntity != null && !ReflectUtil.isObjectNull(groupEntity)) {
                experimentParticipatorResponse.setGroupName(groupEntity.getGroupName());
            }
        }
        //2、获取参与者负责的uim机构
        List<String> experimentOrgIds = Arrays.asList(participatorEntity.getExperimentOrgIds().split(","));
        List<String> orgIds = new ArrayList<>();
        if (experimentOrgIds != null && experimentOrgIds.size() > 0) {
            experimentOrgIds.forEach(experimentOrgId -> {
                ExperimentOrgEntity orgEntity = experimentOrgService.lambdaQuery()
                        .eq(ExperimentOrgEntity::getExperimentOrgId, experimentOrgId)
                        .eq(ExperimentOrgEntity::getDeleted, false)
                        .one();
                if (orgEntity != null) {
                    orgIds.add(orgEntity.getOrgId());
                }
            });
        }
        experimentParticipatorResponse.setOrgIds(orgIds);
        //3、根据账号ID找到头像
        AccountInstanceResponse instanceResponse = accountInstanceApi.getAccountInstanceByAccountId(participatorEntity.getAccountId());
        experimentParticipatorResponse.setAvatar(instanceResponse.getAvatar());
        return experimentParticipatorResponse;
    }

    /**
     * @param
     * @return
     * @说明: 每期结束后，返回剩余的资金
     * @关联表: operate_cost、operate_insurance
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年7月25日 下午16:35:34
     */
//    @Transactional(rollbackFor = Exception.class)
//    public void refundFunds(ExperimentPersonRequest request) {
//        //1、获取该期的结束时间
//        Map<Integer, ExperimentTimerEntity> timerEntityMap = experimentTimerBiz.getExperimentPeriodsStartAnsEndTime(request.getExperimentInstanceId());
//        ExperimentTimerEntity timerEntity = timerEntityMap.get(request.getPeriods());
//        //2、获取该实验人物，所有人物都要统计保险报销
//        List<ExperimentPersonEntity> personEntityList = experimentPersonService.lambdaQuery()
//                .eq(ExperimentPersonEntity::getExperimentInstanceId, request.getExperimentInstanceId())
//                .eq(ExperimentPersonEntity::getDeleted, false)
//                .list();
//        /* runsix: TODO must optimize 这里面存在循环修改 */
//        //3、判断在该期结束之前每个人购买了多少保险，并报销，保险是累加的
//        if (personEntityList != null && personEntityList.size() > 0) {
//            personEntityList.forEach(person -> {
//                BigDecimal fee = new BigDecimal(0);
//                //3.1、获取人物的所有花费，不包括保险费
//                List<OperateCostEntity> costEntityList = operateCostBiz.getPeriodsCostNotInsurance(CostRequest.builder()
//                        .patientId(person.getExperimentPersonId())
//                        .period(request.getPeriods())
//                        .build());
//                costEntityList = costEntityList.stream().filter(c -> ((c.getDt().after(timerEntity.getStartTime()) || c.getDt().equals(timerEntity.getStartTime()))
//                                && (c.getDt().before(timerEntity.getEndTime()) || c.getDt().equals(timerEntity.getEndTime()))))
//                        .collect(Collectors.toList());
//                //3.2、判断人物消费期间购买了哪些保险
//                if (costEntityList != null && costEntityList.size() > 0) {
//                    for (int i = 0; i < costEntityList.size(); i++) {
//                        List<OperateInsuranceEntity> insuranceEntityList = operateInsuranceService.lambdaQuery()
//                                .eq(OperateInsuranceEntity::getExperimentPersonId, person.getExperimentPersonId())
//                                .le(OperateInsuranceEntity::getIndate, costEntityList.get(i).getDt())
//                                .ge(OperateInsuranceEntity::getExpdate, costEntityList.get(i).getDt())
//                                .list();
//                        //3.3、可能会存在多个机构购买情况，金钱要叠加
//                        if (insuranceEntityList != null && insuranceEntityList.size() > 0) {
//                            for (int j = 0; j < insuranceEntityList.size(); j++) {
//                                //3.4、通过机构获取报销比例
//                                ExperimentOrgEntity orgEntity = experimentOrgService.lambdaQuery()
//                                        .eq(ExperimentOrgEntity::getExperimentOrgId, insuranceEntityList.get(j).getExperimentOrgId())
//                                        .eq(ExperimentOrgEntity::getDeleted, false)
//                                        .one();
//                                if (orgEntity != null && !ReflectUtil.isObjectNull(orgEntity)) {
//                                    CaseOrgFeeEntity feeEntity = caseOrgFeeService.lambdaQuery()
//                                            .eq(CaseOrgFeeEntity::getCaseOrgId, orgEntity.getCaseOrgId())
//                                            .eq(CaseOrgFeeEntity::getFeeCode, "BXF")
//                                            .one();
//                                    if (feeEntity != null && !ReflectUtil.isObjectNull(feeEntity)) {
//                                        fee = fee.add(costEntityList.get(i).getCost().multiply(BigDecimal.valueOf(feeEntity.getReimburseRatio()).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN)));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    //3.3、扣费
//                    experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest.builder()
//                            .appId(request.getAppId())
//                            .experimentId(request.getExperimentInstanceId())
//                            .experimentPersonId(person.getExperimentPersonId())
//                            .periods(request.getPeriods())
//                            .moneyChange(fee)
//                            .build());
//                }
//            });
//        }
//    }

    /**
     * @param
     * @return
     * @说明: 每期结束后，返回剩余的资金
     * @关联表: operate_cost、operate_insurance
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年7月25日 下午16:35:34
     */

    @Transactional(rollbackFor = Exception.class)
    public void refundFunds(ExperimentPersonRequest request) {
        //1、获取该实验人物，所有人物都要统计保险报销
        List<ExperimentPersonEntity> personEntityList = experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentInstanceId, request.getExperimentInstanceId())
                .eq(ExperimentPersonEntity::getDeleted, false)
                .list();
        if (personEntityList != null && personEntityList.size() > 0) {
            for (ExperimentPersonEntity personEntity : personEntityList) {
                BigDecimal fee = new BigDecimal(0);
                List<OperateCostEntity> operateCostEntityList = operateCostBiz.getPeriodsRestitution(CostRequest.builder().patientId(personEntity.getExperimentPersonId()).period(request.getPeriods()).build());
                if (operateCostEntityList != null && operateCostEntityList.size() > 0) {
                    for (OperateCostEntity operateCostEntity : operateCostEntityList) {
                        fee = fee.add(operateCostEntity.getRestitution());
                    }
                }
                //2、扣费
                experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest.builder()
                        .appId(request.getAppId())
                        .experimentId(request.getExperimentInstanceId())
                        .experimentPersonId(personEntity.getExperimentPersonId())
                        .periods(request.getPeriods())
                        .moneyChange(fee)
                        .build());
            }
        }
    }
}
