package org.dows.hep.biz.operate;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.enums.EnumOrgFeeType;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.entity.OperateCostEntity;
import org.dows.hep.service.OperateCostService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OperateCostBiz {
    private final OperateCostService operateCostService;
    private final IdGenerator idGenerator;

    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;

    public String saveCost(CostRequest costRequest) {

        OperateCostEntity operateCostEntity = BeanUtil.copyProperties(costRequest, OperateCostEntity.class);
        operateCostEntity.setOperateCostId(idGenerator.nextIdStr());
        operateCostService.save(operateCostEntity);
        return operateCostEntity.getOperateCostId();

    }

    /**
     * @param
     * @return
     * @说明: 获取实验人物某期的花费, 但不包括保险费
     * @关联表: operate_cost
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年7月25日 下午14:40:34
     */
    public List<OperateCostEntity> getPeriodsCostNotInsurance(CostRequest request) {
        List<OperateCostEntity> costEntityList = operateCostService.lambdaQuery()
                .eq(OperateCostEntity::getPatientId, request.getPatientId())
                .ne(OperateCostEntity::getFeeCode, EnumOrgFeeType.BXF.getCode())
                .eq(OperateCostEntity::getPeriod, request.getPeriod())
                .eq(OperateCostEntity::getDeleted, false)
                .list();
        return costEntityList;
    }

    /**
     * @param
     * @return
     * @说明: 获取实验任务每期的花费
     * @关联表: operate_cost
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年7月25日 下午14:40:34
     */
    public List<OperateCostEntity> getPeriodsRestitution(CostRequest request) {
        List<OperateCostEntity> costEntityList = operateCostService.lambdaQuery()
                .eq(OperateCostEntity::getPatientId, request.getPatientId())
                .eq(OperateCostEntity::getPeriod, request.getPeriod())
                .isNotNull(OperateCostEntity::getRestitution)
                .eq(OperateCostEntity::getDeleted, false)
                .list();
        return costEntityList;
    }


    /**
     * 计算小组医疗占比得分
     */
    public Map<String, BigDecimal> calcGroupTreatmentPercent(CostRequest request) {
        Map<String, List<OperateCostEntity>> collect = operateCostService.lambdaQuery()
                .eq(OperateCostEntity::getExperimentInstanceId, request.getExperimentInstanceId())
                .eq(OperateCostEntity::getPeriod, request.getPeriod())
                .eq(OperateCostEntity::getDeleted, Boolean.FALSE)
                .list()
                .stream()
                .collect(Collectors.groupingBy(OperateCostEntity::getExperimentGroupId));
        // 获取初始资金
        String moneyDef = experimentIndicatorInstanceRsBiz.getMoneyDef(request.getPatientId());
        Map<String, BigDecimal> map = new HashMap<>();
        collect.forEach((k, v) -> {
            // 计算当前期某小组的总费用
            BigDecimal periodTotalCost = v.stream()
                    .map(OperateCostEntity::getCost)
                    .reduce(BigDecimal::add)
                    .get();
            BigDecimal initiaCapital = NumberUtil.toBigDecimal(moneyDef);
            BigDecimal div = NumberUtil.div(periodTotalCost, initiaCapital, 2);

            BigDecimal treatmentPercentScore = NumberUtil.div(NumberUtil.sub(1, div), 100);
            map.put(k, treatmentPercentScore);
        });
        return map;
    }


}
