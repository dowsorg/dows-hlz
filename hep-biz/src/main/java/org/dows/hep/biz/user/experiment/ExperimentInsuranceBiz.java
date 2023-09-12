package org.dows.hep.biz.user.experiment;

import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.enums.EnumOrgFeeType;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.request.ExperimentPersonInsuranceRequest;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.util.TimeUtil;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jx
 * @date 2023/6/28 11:41
 */
@RequiredArgsConstructor
@Service
public class ExperimentInsuranceBiz {

    private final ExperimentPersonMedicalResultService experimentPersonMedicalResultService;
    private final ExperimentPersonInsuranceService experimentPersonInsuranceService;
    private final ExperimentOrgService experimentOrgService;
    private final CaseOrgFeeService caseOrgFeeService;
    private final IdGenerator idGenerator;
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExperimentTimerBiz experimentTimerBiz;
    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;
    private final OperateCostBiz operateCostBiz;
    private final OperateInsuranceService operateInsuranceService;

    /**
     * @param
     * @return
     * @说明: 是否购买保险
     * @关联表: experimentPersonProperty
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月27日 下午18:29:34
     */
    @DSTransactional
    public Boolean isPurchaseInsure(ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest, HttpServletRequest request) {
        //1、扣除购买保险费
        experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest.builder()
                .appId(experimentPersonInsuranceRequest.getAppId())
                .experimentId(experimentPersonInsuranceRequest.getExperimentInstanceId())
                .experimentPersonId(experimentPersonInsuranceRequest.getExperimentPersonId())
                .periods(Integer.parseInt(experimentPersonInsuranceRequest.getPeriods()))
                .moneyChange(experimentPersonInsuranceRequest.getInsuranceAmount().negate())
                .assertEnough(true)
                .build());
        LoginContextVO voLogin= ShareBiz.getLoginUser(request);
        String operateFlowId = ShareBiz.checkRunningOperateFlowId(experimentPersonInsuranceRequest.getAppId(),
                experimentPersonInsuranceRequest.getExperimentInstanceId(),
                experimentPersonInsuranceRequest.getOperateOrgId(),
                experimentPersonInsuranceRequest.getExperimentPersonId());
        //2、保存消费记录
        CostRequest costRequest = CostRequest.builder()
                .operateFlowId(idGenerator.nextIdStr())
                .experimentInstanceId(experimentPersonInsuranceRequest.getExperimentInstanceId())
                .experimentGroupId(experimentPersonInsuranceRequest.getExperimentGroupId())
                .operatorId(voLogin.getAccountId())
                .experimentOrgId(experimentPersonInsuranceRequest.getOperateOrgId())
                .operateFlowId(operateFlowId)
                .patientId(experimentPersonInsuranceRequest.getExperimentPersonId())
                .feeName(EnumOrgFeeType.BXF.getName())
                .feeCode(EnumOrgFeeType.BXF.getCode())
                .cost(experimentPersonInsuranceRequest.getInsuranceAmount())
                .period(Integer.parseInt(experimentPersonInsuranceRequest.getPeriods()))
                .build();
        operateCostBiz.saveCost(costRequest);
        //3、通过机构获取报销比例
        ExperimentOrgEntity orgEntity = experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentOrgId, experimentPersonInsuranceRequest.getOperateOrgId())
                .eq(ExperimentOrgEntity::getDeleted, false)
                .one();
        Double reimburseRatio = 0.0d;
        if (orgEntity != null && !ReflectUtil.isObjectNull(orgEntity)) {
            CaseOrgFeeEntity feeEntity = caseOrgFeeService.lambdaQuery()
                    .eq(CaseOrgFeeEntity::getCaseOrgId, orgEntity.getCaseOrgId())
                    .eq(CaseOrgFeeEntity::getFeeCode, "BXF")
                    .eq(CaseOrgFeeEntity::getDeleted, false)
                    .one();
            if (feeEntity != null && !ReflectUtil.isObjectNull(feeEntity)) {
                reimburseRatio = feeEntity.getReimburseRatio();
            }
        }
        //4、计算失效时间
        //4.1、首先获取实验设置，判断真实天数与模拟数据的比例
        Date expdate = new Date();
        String configVals = experimentSettingBiz.getSandSettingStr(experimentPersonInsuranceRequest.getExperimentInstanceId());
        ExperimentSetting.SandSetting sandSetting = JSONUtil.toBean(configVals, ExperimentSetting.SandSetting.class);
        //4.2、判断有几期,获取总的换算天数，计算
        int periods = sandSetting.getPeriods();
        //4.3、获取实验每期时间
        Map<Integer, ExperimentTimerEntity> experimentPeriodsStartAnsEndTime =
                experimentTimerBiz.getExperimentPeriodsStartAnsEndTime(experimentPersonInsuranceRequest.getExperimentInstanceId());
        int currentPeriods = Integer.parseInt(experimentPersonInsuranceRequest.getPeriods());
        int remainDay = 0;
        for (int i = currentPeriods; i <= periods; i++) {
            Integer duration = sandSetting.getDurationMap().get(String.valueOf(i));
            Integer periodsand = sandSetting.getPeriodMap().get(String.valueOf(i));
            if (i == currentPeriods) {
                //4.4、判断当前时间在本期还剩多少
                long remainTime = experimentPeriodsStartAnsEndTime.get(i).getEndTime().getTime() - new Date().getTime();
                int remainSecond = (int)remainTime / 1000;
                //4.5、假设365天都在一期需要的时间
                BigDecimal assumSecond = BigDecimal.valueOf(duration).
                        multiply(BigDecimal.valueOf(365)).
                        divide(BigDecimal.valueOf(periodsand),2, RoundingMode.DOWN).
                        multiply(BigDecimal.valueOf(60));
                if(assumSecond.intValue() > remainSecond){
                    remainDay += BigDecimal.valueOf(remainSecond)
                            .divide(BigDecimal.valueOf(60),2, RoundingMode.DOWN)
                            .multiply(BigDecimal.valueOf(periodsand))
                            .divide(BigDecimal.valueOf(duration),2, RoundingMode.DOWN)
                            .intValue();
                    expdate = TimeUtil.timeAddSecond(new Date(),remainSecond);
                }else{
                    expdate = TimeUtil.timeAddSecond(new Date(),assumSecond.intValue());
                    break;
                }
            }
            if(i != currentPeriods){
                //加上每期间隔
                expdate = TimeUtil.timeAddSecond(expdate,sandSetting.getInterval().intValue());
                //4.6、后面的期数，都是完整的
                long remainTime = experimentPeriodsStartAnsEndTime.get(i).getEndTime().getTime()
                        - experimentPeriodsStartAnsEndTime.get(i).getStartTime().getTime();
                int remainSecond = (int) remainTime / 1000;
                //4.7、判断剩下天数都在一期需要的时间
                int leftDay = 365 - remainDay;
                BigDecimal assumSecond = BigDecimal.valueOf(duration).
                        multiply(BigDecimal.valueOf(leftDay)).
                        divide(BigDecimal.valueOf(periodsand),2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(60));
                if(assumSecond.intValue() > remainSecond){
                    remainDay += BigDecimal.valueOf(remainSecond)
                            .divide(BigDecimal.valueOf(60),2, RoundingMode.DOWN)
                            .multiply(BigDecimal.valueOf(periodsand))
                            .divide(BigDecimal.valueOf(duration),2, RoundingMode.DOWN)
                            .intValue();
                    expdate = TimeUtil.timeAddSecond(expdate,remainSecond);
                }else{
                    expdate = TimeUtil.timeAddSecond(expdate,assumSecond.intValue());
                    break;
                }
            }
        }
        ExperimentPersonInsuranceEntity insuranceEntity = experimentPersonInsuranceService.lambdaQuery()
                .eq(ExperimentPersonInsuranceEntity::getExperimentInstanceId,experimentPersonInsuranceRequest.getExperimentInstanceId())
                .eq(ExperimentPersonInsuranceEntity::getExperimentGroupId,experimentPersonInsuranceRequest.getExperimentGroupId())
                .eq(ExperimentPersonInsuranceEntity::getExperimentPersonId,experimentPersonInsuranceRequest.getExperimentPersonId())
                .eq(ExperimentPersonInsuranceEntity::getOperateOrgId,experimentPersonInsuranceRequest.getOperateOrgId())
                .one();
        Boolean flag;
        ExperimentTimePoint nowPoint= ExperimentSettingCache.Instance().getTimePointByRealTimeSilence(ExperimentCacheKey.create("3",experimentPersonInsuranceRequest.getExperimentInstanceId()),
                LocalDateTime.now(), true);
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(nowPoint))
                .throwMessage("未找到实验时间设置");
        Integer buyDay=nowPoint.getGameDay();
        if(insuranceEntity != null && !ReflectUtil.isObjectNull(insuranceEntity)){
            //更新
            flag = experimentPersonInsuranceService.lambdaUpdate()
                    .set(ExperimentPersonInsuranceEntity::getIndate,new Date())
                    .set(ExperimentPersonInsuranceEntity::getExpdate,expdate)
                    .set(ExperimentPersonInsuranceEntity::getBuyGameDay,buyDay)
                    .set(ExperimentPersonInsuranceEntity::getPeriods,experimentPersonInsuranceRequest.getPeriods())
                    .eq(ExperimentPersonInsuranceEntity::getId,insuranceEntity.getId())
                    .update();
        }else {
            ExperimentPersonInsuranceEntity experimentPersonInsuranceEntity = ExperimentPersonInsuranceEntity
                    .builder()
                    .experimentPersonInsuranceId(idGenerator.nextIdStr())
                    .appId(experimentPersonInsuranceRequest.getAppId())
                    .experimentPersonId(experimentPersonInsuranceRequest.getExperimentPersonId())
                    .experimentInstanceId(experimentPersonInsuranceRequest.getExperimentInstanceId())
                    .experimentGroupId(experimentPersonInsuranceRequest.getExperimentGroupId())
                    .periods(experimentPersonInsuranceRequest.getPeriods())
                    .operateOrgId(experimentPersonInsuranceRequest.getOperateOrgId())
                    .insuranceAmount(experimentPersonInsuranceRequest.getInsuranceAmount())
                    .reimburseRatio(reimburseRatio)
                    .indate(new Date())
                    .expdate(expdate)
                    .buyGameDay(buyDay)
                    .build();
            flag = experimentPersonInsuranceService.save(experimentPersonInsuranceEntity);
        }
        //保存购买保险记录
        OperateInsuranceEntity insuranceEntity1 = OperateInsuranceEntity.builder()
                .operateInsuranceId(idGenerator.nextIdStr())
                .experimentInstanceId(experimentPersonInsuranceRequest.getExperimentInstanceId())
                .experimentGroupId(experimentPersonInsuranceRequest.getExperimentGroupId())
                .experimentPersonId(experimentPersonInsuranceRequest.getExperimentPersonId())
                .experimentOrgId(experimentPersonInsuranceRequest.getOperateOrgId())
                .periods(Integer.parseInt(experimentPersonInsuranceRequest.getPeriods()))
                .indate(new Date())
                .expdate(expdate)
                .build();
        operateInsuranceService.save(insuranceEntity1);
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 每期单人计算医疗占比
     * @关联表: experimentPersonInsurance
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月28日 上午09:30:34
     */
    @DSTransactional
    public BigDecimal calculatePeriodsFee(ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest) {
        //1、获取该期数的保险购买记录
        List<ExperimentPersonInsuranceEntity> insuranceEntityList = experimentPersonInsuranceService.lambdaQuery()
                .eq(ExperimentPersonInsuranceEntity::getExperimentPersonId, experimentPersonInsuranceRequest.getExperimentPersonId())
                .eq(ExperimentPersonInsuranceEntity::getExperimentInstanceId, experimentPersonInsuranceRequest.getExperimentInstanceId())
                .eq(ExperimentPersonInsuranceEntity::getExperimentGroupId, experimentPersonInsuranceRequest.getExperimentGroupId())
                .eq(ExperimentPersonInsuranceEntity::getPeriods, experimentPersonInsuranceRequest.getPeriods())
                .eq(ExperimentPersonInsuranceEntity::getDeleted, false)
                .list();
        //2、获取保险购买期间的所有消费记录
        BigDecimal totalPay = new BigDecimal(0);
        if (insuranceEntityList != null && insuranceEntityList.size() > 0) {
            for (ExperimentPersonInsuranceEntity insurance : insuranceEntityList) {
                Date startTime = insurance.getIndate();
                Date endTime = insurance.getExpdate();
                //3、todo 获取这段时间的消费记录(包括医疗支出和挂号)
                BigDecimal periodsFund = new BigDecimal(122.00);
                totalPay = totalPay.add(periodsFund.multiply(BigDecimal.valueOf(insurance.getReimburseRatio())).add(insurance.getInsuranceAmount()));
            }
        }
        //3、计算每期医疗占比，并保存进数据库，todo personFund为用户总金额
        BigDecimal personFund = new BigDecimal(22222);
        BigDecimal per = totalPay.divide(personFund, BigDecimal.ROUND_CEILING);
        //3.1、计算得分
        BigDecimal newPer = new BigDecimal(1).subtract(per);
        ExperimentPersonMedicalResultEntity resultEntity = ExperimentPersonMedicalResultEntity.builder()
                .experimentPersonMedicalResultId(idGenerator.nextIdStr())
                .appId(experimentPersonInsuranceRequest.getAppId())
                .experimentPersonId(experimentPersonInsuranceRequest.getExperimentPersonId())
                .experimentInstanceId(experimentPersonInsuranceRequest.getExperimentInstanceId())
                .experimentGroupId(experimentPersonInsuranceRequest.getExperimentGroupId())
                .medicalPer(per)
                .periods(experimentPersonInsuranceRequest.getPeriods())
                .medicalScore(newPer.multiply(new BigDecimal(100)))
                .build();
        experimentPersonMedicalResultService.save(resultEntity);
        //4、todo 用户总金额扣除这部分费用
        personFund.subtract(totalPay);
        return per;
    }

    /**
     * @param
     * @return
     * @说明: 每期计算支出费用
     * @关联表: experimentPersonInsurance
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月28日 上午09:30:34
     */
    public BigDecimal calculatePeriodsScore(ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest) {
        BigDecimal per = new BigDecimal(1).subtract(experimentPersonInsuranceRequest.getPer());
        return per.multiply(new BigDecimal(100));
    }

    /**
     * @param
     * @return
     * @说明: 每期小组的平均医疗得分
     * @关联表: experimentPersonInsurance
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月28日 上午11:15:34
     */
    public BigDecimal calculatePeriodsGroupScore(ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest) {
        List<ExperimentPersonMedicalResultEntity> resultEntityList = experimentPersonMedicalResultService.lambdaQuery()
                .eq(ExperimentPersonMedicalResultEntity::getExperimentInstanceId, experimentPersonInsuranceRequest.getExperimentInstanceId())
                .eq(ExperimentPersonMedicalResultEntity::getExperimentGroupId, experimentPersonInsuranceRequest.getExperimentGroupId())
                .eq(ExperimentPersonMedicalResultEntity::getPeriods, experimentPersonInsuranceRequest.getPeriods())
                .eq(ExperimentPersonMedicalResultEntity::getDeleted, false)
                .list();
        BigDecimal totalScore = new BigDecimal(0);
        if (resultEntityList != null && resultEntityList.size() > 0) {
            for (ExperimentPersonMedicalResultEntity result : resultEntityList) {
                totalScore = totalScore.add(result.getMedicalScore());
            }
        }
        return totalScore.divide(new BigDecimal(resultEntityList.size()));
    }

    /**
     * @param
     * @return
     * @说明: 每期医疗占比得分的平均值
     * @关联表: experimentPersonInsurance
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月28日 上午11:27:34
     */
    public Map<String, BigDecimal> calculatePeriodsTotalScore(ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest) {
        Map<String, BigDecimal> resultMap = new HashMap<>();
        Map<String, List<ExperimentPersonMedicalResultEntity>> map = experimentPersonMedicalResultService.lambdaQuery()
                .eq(ExperimentPersonMedicalResultEntity::getExperimentInstanceId, experimentPersonInsuranceRequest.getExperimentInstanceId())
                .eq(ExperimentPersonMedicalResultEntity::getDeleted, false)
                .list()
                .stream().collect(Collectors.groupingBy(ExperimentPersonMedicalResultEntity::getPeriods));
        for (Map.Entry<String, List<ExperimentPersonMedicalResultEntity>> entry : map.entrySet()) {
            BigDecimal totalScore = new BigDecimal(0);
            List<ExperimentPersonMedicalResultEntity> resultEntityList = entry.getValue();
            if (resultEntityList != null && resultEntityList.size() > 0) {
                for (ExperimentPersonMedicalResultEntity result : resultEntityList) {
                    totalScore = totalScore.add(result.getMedicalScore());
                }
                BigDecimal avgScore = totalScore.divide(new BigDecimal(resultEntityList.size()));
                resultMap.put(entry.getKey(), avgScore);
            }
        }
        return resultMap;
    }

    /**
     * @param
     * @return
     * @说明: 获取保险状态
     * @关联表: experimentPersonInsurance
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月28日 上午11:27:34
     */
    public Map<String,Object> checkInsureStatus(ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest) throws ParseException {
        Map<String,Object> map = new HashMap<>();
        ExperimentPersonInsuranceEntity entity = experimentPersonInsuranceService.lambdaQuery()
                .eq(ExperimentPersonInsuranceEntity::getExperimentPersonId, experimentPersonInsuranceRequest.getExperimentPersonId())
                .eq(ExperimentPersonInsuranceEntity::getExperimentInstanceId, experimentPersonInsuranceRequest.getExperimentInstanceId())
                .eq(ExperimentPersonInsuranceEntity::getExperimentGroupId, experimentPersonInsuranceRequest.getExperimentGroupId())
//                .eq(ExperimentPersonInsuranceEntity::getPeriods, experimentPersonInsuranceRequest.getPeriods())
                .eq(ExperimentPersonInsuranceEntity::getOperateOrgId, experimentPersonInsuranceRequest.getOperateOrgId())
                .eq(ExperimentPersonInsuranceEntity::getDeleted, false)
                .one();
        if (entity == null || ReflectUtil.isObjectNull(entity)) {
            map.put("result",false);
            return map;
        }
        int intervalDays=0;
        ExperimentTimePoint nowPoint= ExperimentSettingCache.Instance().getTimePointByRealTimeSilence(ExperimentCacheKey.create("3",experimentPersonInsuranceRequest.getExperimentInstanceId()),
                LocalDateTime.now(), true);
        if(ShareUtil.XObject.notEmpty(nowPoint)){
            intervalDays=Math.max(360,360+entity.getBuyGameDay()-nowPoint.getGameDay());
        }
        if(intervalDays>0){
            long interval = (entity.getExpdate().getTime() - new Date().getTime())/1000;
            map.put("result", true);
            map.put("interval",interval);
            map.put("intervalDays",intervalDays);
        }else{
            map.put("result", false);
        }
        return map;
    }
}
