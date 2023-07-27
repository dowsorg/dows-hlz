package org.dows.hep.biz.risk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.base.risk.CrowdsInstanceBiz;
import org.dows.hep.biz.base.risk.RiskModelBiz;
import org.dows.hep.entity.ExperimentIndicatorExpressionRefRsEntity;
import org.dows.hep.entity.ExperimentIndicatorExpressionRsEntity;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentRiskModelRsEntity;
import org.dows.hep.service.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RiskBiz {

    private final RiskModelBiz riskModelBiz;

    private final CrowdsInstanceBiz crowdsInstanceBiz;

    private final ExperimentRiskModelRsService experimentRiskModelRsService;

    private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;

    private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;

    private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;

    private final ExperimentCrowdsInstanceRsService experimentCrowdsInstanceRsService;


    /**
     * 获取死亡原因
     */
    public void getDeadReason(String experimentInstanceId, String experimentGroupId, String period) {

        List<ExperimentRiskModelRsEntity> experimentRiskModelRsEntities = experimentRiskModelRsService.lambdaQuery()
                .eq(ExperimentRiskModelRsEntity::getExperimentId, experimentInstanceId)
                .list();
        Map<String, ExperimentRiskModelRsEntity> experimentRiskModelRsEntityMap = experimentRiskModelRsEntities.stream()
                .collect(Collectors.toMap(ExperimentRiskModelRsEntity::getExperimentRiskModelId, Function.identity()));
//        List<String> reskModeIds = experimentRiskModelRsEntities.stream()
//                .map(ExperimentRiskModelRsEntity::getExperimentRiskModelId)
//                .collect(Collectors.toList());
        List<String> reskModeIds = experimentRiskModelRsEntityMap.keySet().stream().toList();
        // 查询该风险模型对应的危险因素（防止要用到）
        List<ExperimentIndicatorExpressionRefRsEntity> eiers = experimentIndicatorExpressionRefRsService.lambdaQuery()
                .eq(ExperimentIndicatorExpressionRefRsEntity::getExperimentId, experimentInstanceId)
                .in(ExperimentIndicatorExpressionRefRsEntity::getReasonId, reskModeIds)
                .list();
        // 实验指标表达式ID
        List<String> experimentIndicatorExpressionIds = eiers.stream()
                .map(ExperimentIndicatorExpressionRefRsEntity::getIndicatorExpressionId)
                .collect(Collectors.toList());
        // 模型(死亡原因)ID->实验指标表达式ID
        Map<String, String> eierMapping = new LinkedHashMap<>();
        eiers.forEach(e -> eierMapping.put(e.getIndicatorExpressionId(), e.getReasonId()));

        // 查询experimentIndicatorExpressionRs指标表达式（防止要用到）
        List<ExperimentIndicatorExpressionRsEntity> indicatorExperssions = experimentIndicatorExpressionRsService.lambdaQuery()
                .eq(ExperimentIndicatorExpressionRsEntity::getExperimentId, experimentInstanceId)
                .in(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId, experimentIndicatorExpressionIds)
                .list();
        List<String> experimentIndicatorInstanceRsEntityIds = indicatorExperssions.stream()
                .map(ExperimentIndicatorExpressionRsEntity::getPrincipalId)
                .collect(Collectors.toList());
        // 实验指标表达式ID->主体ID[实验指标实例]
        Map<String, String> ieMapping = new LinkedHashMap<>();
        indicatorExperssions.forEach(ie -> ieMapping.put(ie.getPrincipalId(), ie.getExperimentIndicatorExpressionId()));

        // 实验指标实例（防止要用到）
        List<ExperimentIndicatorInstanceRsEntity> experimentIndicatorInstanceRsEntitys = experimentIndicatorInstanceRsService.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getExperimentId, experimentInstanceId)
                .in(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, experimentIndicatorInstanceRsEntityIds)
                .list();
        // 人物ID->实验指标实例ID
        //Map<String,String> eiiMapping= new LinkedHashMap<>();
        //experimentIndicatorInstanceRsEntitys.forEach(eii->eiiMapping.put(eii.getExperimentPersonId(),eii.getExperimentIndicatorInstanceId()));

        Map<String, List<RiskRelevanceBo>> stringListMap = new LinkedHashMap<>();
        // 按指标名进行分组
        experimentIndicatorInstanceRsEntitys.stream()
                .collect(Collectors.groupingBy(ExperimentIndicatorInstanceRsEntity::getIndicatorName))
                .forEach((k, v) -> {
                    List<RiskRelevanceBo> personRiskRelevanceBo = new ArrayList<>();
                    //一个指标名称可能对应对多个人物
                    for (ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity : v) {
                        // 获取该指标名称对应的人物ID
                        String experimentPersonId = experimentIndicatorInstanceRsEntity.getExperimentPersonId();

                        String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();

                        String getExperimentIndicatorExpressionId = ieMapping.get(experimentIndicatorInstanceId);
                        // 当前人物关联的 模型(死亡原因)ID
                        String getReasonId = eierMapping.get(getExperimentIndicatorExpressionId);

                        RiskRelevanceBo riskRelevanceBo = RiskRelevanceBo.builder()
                                .reasonId(getReasonId)
                                .experimentIndicatorExpressionId(experimentIndicatorInstanceId)
                                .experimentPersonId(experimentPersonId)
                                .experimentIndicatorInstanceId(experimentIndicatorInstanceId)
                                .build();
                        personRiskRelevanceBo.add(riskRelevanceBo);
                    }
                    stringListMap.put(k, personRiskRelevanceBo);
                });

        stringListMap.forEach((k, v) -> {
            //  实验风险模型
            ExperimentRiskModelRsEntity experimentRiskModelRsEntity = experimentRiskModelRsEntityMap.get(k);
            // 该风险模型下的相关的危险因素涉及到的关联数据
            for (RiskRelevanceBo riskRelevanceBo : v) {
                // todo 多线程计算

            }
        });


//        experimentRiskModelRsEntities.forEach(e -> {
//            // todo 构建对象
//            String riskName = e.getName();
//            // 死亡分数
//            Integer deathScore= e.getRiskDeathProbability();
//            // 人群ID
//            String crowdsCategoryId = e.getCrowdsCategoryId();
//
//            String experimentRiskModelId = e.getExperimentRiskModelId();
//
//
//        });

    }


    /**
     * @param dangerFactorIds
     */
    public void getDangerFactors(List<String> dangerFactorIds) {


    }


    /**
     * 获取危险因素
     */
    public void getDangerFactor(String experimentInstanceId, String experimentGroupId, String period) {

        //experimentIndicatorExpressionRsService

    }

}
