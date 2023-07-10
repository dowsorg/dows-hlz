package org.dows.hep.biz.user.experiment;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.user.experiment.request.ExperimentPersonInsuranceRequest;
import org.dows.hep.biz.util.TimeUtil;
import org.dows.hep.entity.CaseOrgFeeEntity;
import org.dows.hep.entity.ExperimentOrgEntity;
import org.dows.hep.entity.ExperimentPersonInsuranceEntity;
import org.dows.hep.entity.ExperimentPersonMedicalResultEntity;
import org.dows.hep.service.CaseOrgFeeService;
import org.dows.hep.service.ExperimentOrgService;
import org.dows.hep.service.ExperimentPersonInsuranceService;
import org.dows.hep.service.ExperimentPersonMedicalResultService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
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
    public Boolean isPurchaseInsure(ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest) {
        //1、通过机构获取报销比例
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
                .expdate(TimeUtil.addDays(new Date(), 365))
                .build();
        return experimentPersonInsuranceService.save(experimentPersonInsuranceEntity);
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
        BigDecimal per = totalPay.divide(personFund,BigDecimal.ROUND_CEILING);
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
            for(ExperimentPersonMedicalResultEntity result : resultEntityList) {
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
                    for(ExperimentPersonMedicalResultEntity result : resultEntityList) {
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
    public Boolean checkInsureStatus(ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest) throws ParseException {
        ExperimentPersonInsuranceEntity entity = experimentPersonInsuranceService.lambdaQuery()
                .eq(ExperimentPersonInsuranceEntity::getExperimentPersonId,experimentPersonInsuranceRequest.getExperimentPersonId())
                .eq(ExperimentPersonInsuranceEntity::getExperimentInstanceId, experimentPersonInsuranceRequest.getExperimentInstanceId())
                .eq(ExperimentPersonInsuranceEntity::getExperimentGroupId, experimentPersonInsuranceRequest.getExperimentGroupId())
                .eq(ExperimentPersonInsuranceEntity::getPeriods, experimentPersonInsuranceRequest.getPeriods())
                .eq(ExperimentPersonInsuranceEntity::getOperateOrgId, experimentPersonInsuranceRequest.getOperateOrgId())
                .eq(ExperimentPersonInsuranceEntity::getDeleted, false)
                .one();
        if(entity == null || ReflectUtil.isObjectNull(entity)){
            throw new ExperimentException("该保险不存在");
        }
        //判断过期时间
        return TimeUtil.isBeforeTime(new Date(),entity.getExpdate());
    }
}
