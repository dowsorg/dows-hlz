package org.dows.hep.biz.risk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.RsAgeRequest;
import org.dows.hep.api.base.indicator.request.RsSexRequest;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.biz.base.risk.CrowdsInstanceBiz;
import org.dows.hep.biz.base.risk.RiskModelBiz;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.hep.vo.report.PersonRiskFactor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RiskBiz {

    // 实验风险模型
    private final ExperimentRiskModelRsService experimentRiskModelRsService;

    // 实验指标表达式
    private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;

    // 实验指标表达式
    private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;
    private final ExperimentPersonService experimentPersonService;

    // 实验指标实例
    private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
    private final ExperimentPersonRiskModelRsService experimentPersonRiskModelRsService;
    private final ExperimentPersonHealthRiskFactorRsService experimentPersonHealthRiskFactorRsService;
    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;

    /**
     * runsix method process
     * 根据实验id、小组id获取此小组所有人 每一期的数据，如果只要干预前后，那就取每个人第一个和最后一个
     */
    public List<PersonRiskFactor> get(String experimentInstanceId, String experimentGroupId, Integer period) {
        List<PersonRiskFactor> personRiskFactorList = new ArrayList<>();

        /* runsix:获取小组所有实验人物 */
        Map<String, ExperimentPersonEntity> kExperimentPersonIdVExperimentPersonEntityMap = new HashMap<>();
        experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentGroupId, experimentGroupId)
                .list()
                .forEach(experimentPersonEntity -> {
                    kExperimentPersonIdVExperimentPersonEntityMap.put(experimentPersonEntity.getExperimentPersonId(), experimentPersonEntity);
                });
        if (kExperimentPersonIdVExperimentPersonEntityMap.isEmpty()) {
            return personRiskFactorList;
        }

        /* runsix:此小组所有实验人物id */
        Set<String> experimentPersonIdSet = kExperimentPersonIdVExperimentPersonEntityMap.keySet();
        Map<String, String> kExperimentPersonIdVAgeMap = experimentIndicatorInstanceRsBiz.getAgeByPeriods(RsAgeRequest
            .builder()
            .periods(period)
            .experimentPersonIdSet(experimentPersonIdSet)
            .build());
        Map<String, String> kExperimentPersonIdVSexMap = experimentIndicatorInstanceRsBiz.getSexByPeriods(RsSexRequest
            .builder()
            .periods(period)
            .experimentPersonIdSet(experimentPersonIdSet)
            .build());

        Map<String, List<ExperimentPersonRiskModelRsEntity>> kExperimentPersonIdVExperimentPersonRiskModelRsEntityListMap = new HashMap<>();
        experimentPersonRiskModelRsService.lambdaQuery()
                .eq(ExperimentPersonRiskModelRsEntity::getExperimentId, experimentInstanceId)
                .in(ExperimentPersonRiskModelRsEntity::getExperimentPersonId, experimentPersonIdSet)
                .list()
                .forEach(experimentPersonRiskModelRsEntity -> {
                    String experimentPersonId = experimentPersonRiskModelRsEntity.getExperimentPersonId();
                    List<ExperimentPersonRiskModelRsEntity> experimentPersonRiskModelRsEntityList =
                            kExperimentPersonIdVExperimentPersonRiskModelRsEntityListMap.get(experimentPersonId);
                    if (Objects.isNull(experimentPersonRiskModelRsEntityList)) {
                        experimentPersonRiskModelRsEntityList = new ArrayList<>();
                    }
                    experimentPersonRiskModelRsEntityList.add(experimentPersonRiskModelRsEntity);
                    kExperimentPersonIdVExperimentPersonRiskModelRsEntityListMap.put(experimentPersonId, experimentPersonRiskModelRsEntityList);
                });
        if (kExperimentPersonIdVExperimentPersonRiskModelRsEntityListMap.isEmpty()) {
            return personRiskFactorList;
        }
        Set<String> experimentPersonRiskModelIdSet = new HashSet<>();
        kExperimentPersonIdVExperimentPersonRiskModelRsEntityListMap.values().forEach(experimentPersonRiskModelRsEntityList -> {
            experimentPersonRiskModelIdSet.addAll(experimentPersonRiskModelRsEntityList.stream()
                    .map(ExperimentPersonRiskModelRsEntity::getExperimentPersonRiskModelId).collect(Collectors.toSet()));
        });
        if (experimentPersonRiskModelIdSet.isEmpty()) {
            return personRiskFactorList;
        }
        Map<String, List<ExperimentPersonHealthRiskFactorRsEntity>> kExperimentPersonRiskModelIdVExperimentPersonHealthRiskFactorRsEntityListMap = new HashMap<>();
        experimentPersonHealthRiskFactorRsService.lambdaQuery()
                .in(ExperimentPersonHealthRiskFactorRsEntity::getExperimentPersonRiskModelId, experimentPersonRiskModelIdSet)
                .list()
                .forEach(experimentPersonHealthRiskFactorRsEntity -> {
                    String experimentPersonRiskModelId = experimentPersonHealthRiskFactorRsEntity.getExperimentPersonRiskModelId();
                    List<ExperimentPersonHealthRiskFactorRsEntity> experimentPersonHealthRiskFactorRsEntityList =
                            kExperimentPersonRiskModelIdVExperimentPersonHealthRiskFactorRsEntityListMap.get(experimentPersonRiskModelId);
                    if (Objects.isNull(experimentPersonHealthRiskFactorRsEntityList)) {
                        experimentPersonHealthRiskFactorRsEntityList = new ArrayList<>();
                    }
                    experimentPersonHealthRiskFactorRsEntityList.add(experimentPersonHealthRiskFactorRsEntity);
                    kExperimentPersonRiskModelIdVExperimentPersonHealthRiskFactorRsEntityListMap.put(experimentPersonRiskModelId,
                            experimentPersonHealthRiskFactorRsEntityList);
                });

        /* runsix:组合数据 */
        kExperimentPersonIdVExperimentPersonRiskModelRsEntityListMap.forEach((experimentPersonId, experimentPersonRiskModelRsEntityList) -> {
            ExperimentPersonEntity experimentPersonEntity = kExperimentPersonIdVExperimentPersonEntityMap.get(experimentPersonId);
            if (Objects.isNull(experimentPersonEntity)) {
                return;
            }
            PersonRiskFactor personRiskFactor = PersonRiskFactor
                    .builder()
                    .personId(experimentPersonEntity.getExperimentPersonId())
                    .personName(experimentPersonEntity.getUserName())
                    .sex(kExperimentPersonIdVSexMap.get(experimentPersonEntity.getExperimentPersonId()))
                    .age(Integer.valueOf(kExperimentPersonIdVAgeMap.get(experimentPersonEntity.getExperimentPersonId())))
                    .build();
            Map<Integer, List<ExperimentPersonRiskModelRsEntity>> kPeriodsVExperimentPersonRiskModelRsEntityListMap
                    = experimentPersonRiskModelRsEntityList.stream().collect(Collectors.groupingBy(ExperimentPersonRiskModelRsEntity::getPeriods));
            kPeriodsVExperimentPersonRiskModelRsEntityListMap.forEach((periods, periodsExperimentPersonRiskModelRsEntityList) -> {
                personRiskFactor.setPeriod(periods);
                List<PersonRiskFactor.RiskFactor> riskFactors = new ArrayList<>();
                periodsExperimentPersonRiskModelRsEntityList.forEach(periodsExperimentPersonRiskModelRsEntity -> {
                    PersonRiskFactor.RiskFactor riskFactor = new PersonRiskFactor.RiskFactor();
                    List<PersonRiskFactor.RiskItem> riskItems = new ArrayList<>();
                    riskFactor.setRiskName(periodsExperimentPersonRiskModelRsEntity.getName());
                    riskFactor.setRiskDeathProbability(periodsExperimentPersonRiskModelRsEntity.getRiskDeathProbability());
                    riskFactor.setRiskScore(periodsExperimentPersonRiskModelRsEntity.getComposeRiskScore().toString());
                    riskFactor.setDeathRiskScore(periodsExperimentPersonRiskModelRsEntity.getExistDeathRiskScore().toString());
                    String experimentPersonRiskModelId = periodsExperimentPersonRiskModelRsEntity.getExperimentPersonRiskModelId();
                    List<ExperimentPersonHealthRiskFactorRsEntity> experimentPersonHealthRiskFactorRsEntityList =
                            kExperimentPersonRiskModelIdVExperimentPersonHealthRiskFactorRsEntityListMap.get(experimentPersonRiskModelId);
                    if (Objects.nonNull(experimentPersonHealthRiskFactorRsEntityList) && !experimentPersonHealthRiskFactorRsEntityList.isEmpty()) {
                        experimentPersonHealthRiskFactorRsEntityList.forEach(experimentPersonHealthRiskFactorRsEntity -> {
                            PersonRiskFactor.RiskItem riskItem = new PersonRiskFactor.RiskItem();
                            riskItem.setItemName(experimentPersonHealthRiskFactorRsEntity.getName());
                            riskItem.setItemValue(experimentPersonHealthRiskFactorRsEntity.getVal());
                            riskItem.setRiskScore(experimentPersonHealthRiskFactorRsEntity.getRiskScore().toString());
                            riskItems.add(riskItem);
                        });
                    }
                    riskFactor.setRiskItems(riskItems);
                    riskFactors.add(riskFactor);
                });
                personRiskFactor.setRiskFactors(riskFactors);
            });
            personRiskFactorList.add(personRiskFactor);
        });
        return personRiskFactorList;
    }


    /**
     * 获取死亡原因
     */
    public void getDeadReason(String experimentInstanceId, String experimentGroupId, String period) {

//        List<ExperimentRiskModelRsEntity> experimentRiskModelRsEntities = experimentRiskModelRsService.lambdaQuery()
//                .eq(ExperimentRiskModelRsEntity::getExperimentId, experimentInstanceId)
//                .list();
//        Map<String, ExperimentRiskModelRsEntity> experimentRiskModelRsEntityMap = experimentRiskModelRsEntities.stream()
//                .collect(Collectors.toMap(ExperimentRiskModelRsEntity::getExperimentRiskModelId, Function.identity()));
//
//        List<String> reskModeIds = experimentRiskModelRsEntityMap.keySet().stream().toList();
//        // 查询该风险模型对应的危险因素（防止要用到）
//        List<ExperimentIndicatorExpressionRefRsEntity> eiers = experimentIndicatorExpressionRefRsService.lambdaQuery()
//                .eq(ExperimentIndicatorExpressionRefRsEntity::getExperimentId, experimentInstanceId)
//                .in(ExperimentIndicatorExpressionRefRsEntity::getReasonId, reskModeIds)
//                .list();
//        // 实验指标表达式ID
//        List<String> experimentIndicatorExpressionIds = eiers.stream()
//                .map(ExperimentIndicatorExpressionRefRsEntity::getIndicatorExpressionId)
//                .collect(Collectors.toList());
//        // 模型(死亡原因)ID->实验指标表达式ID
//        Map<String, String> eierMapping = new LinkedHashMap<>();
//        eiers.forEach(e -> eierMapping.put(e.getIndicatorExpressionId(), e.getReasonId()));
//
//        // 查询experimentIndicatorExpressionRs指标表达式（防止要用到）
//        List<ExperimentIndicatorExpressionRsEntity> indicatorExperssions = experimentIndicatorExpressionRsService.lambdaQuery()
//                .eq(ExperimentIndicatorExpressionRsEntity::getExperimentId, experimentInstanceId)
//                .in(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId, experimentIndicatorExpressionIds)
//                .list();
//        List<String> experimentIndicatorInstanceRsEntityIds = indicatorExperssions.stream()
//                .map(ExperimentIndicatorExpressionRsEntity::getPrincipalId)
//                .collect(Collectors.toList());
//        // 实验指标表达式ID->主体ID[实验指标实例]
//        Map<String, String> ieMapping = new LinkedHashMap<>();
//        indicatorExperssions.forEach(ie -> ieMapping.put(ie.getPrincipalId(), ie.getExperimentIndicatorExpressionId()));
//
//        // 实验指标实例（防止要用到）
//        List<ExperimentIndicatorInstanceRsEntity> experimentIndicatorInstanceRsEntitys = experimentIndicatorInstanceRsService.lambdaQuery()
//                .eq(ExperimentIndicatorInstanceRsEntity::getExperimentId, experimentInstanceId)
//                .in(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, experimentIndicatorInstanceRsEntityIds)
//                .list();
//
//        Map<String, List<RiskRelevanceBo>> stringListMap = new LinkedHashMap<>();
//        // 按指标名进行分组
//        experimentIndicatorInstanceRsEntitys.stream()
//                .collect(Collectors.groupingBy(ExperimentIndicatorInstanceRsEntity::getIndicatorName))
//                .forEach((k, v) -> {
//                    List<RiskRelevanceBo> personRiskRelevanceBo = new ArrayList<>();
//                    //一个指标名称可能对应对多个人物
//                    for (ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity : v) {
//                        // 获取该指标名称对应的人物ID
//                        String experimentPersonId = experimentIndicatorInstanceRsEntity.getExperimentPersonId();
//
//                        String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
//
//                        String getExperimentIndicatorExpressionId = ieMapping.get(experimentIndicatorInstanceId);
//                        // 当前人物关联的 模型(死亡原因)ID
//                        String getReasonId = eierMapping.get(getExperimentIndicatorExpressionId);
//
//                        RiskRelevanceBo riskRelevanceBo = RiskRelevanceBo.builder()
//                                .reasonId(getReasonId)
//                                .experimentIndicatorExpressionId(experimentIndicatorInstanceId)
//                                .experimentPersonId(experimentPersonId)
//                                .experimentIndicatorInstanceId(experimentIndicatorInstanceId)
//                                .build();
//                        personRiskRelevanceBo.add(riskRelevanceBo);
//                    }
//                    stringListMap.put(k, personRiskRelevanceBo);
//                });
//
//        stringListMap.forEach((k, v) -> {
//            //  实验风险模型
//            ExperimentRiskModelRsEntity experimentRiskModelRsEntity = experimentRiskModelRsEntityMap.get(k);
//            // 该风险模型下的相关的危险因素涉及到的关联数据
//            for (RiskRelevanceBo riskRelevanceBo : v) {
//                // todo 多线程计算
//
//            }
//        });


        // 获取该实验所有的人物&指标实例
        List<ExperimentIndicatorInstanceRsEntity> eies = experimentIndicatorInstanceRsService.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getExperimentId, experimentInstanceId)
                .list();
        //人物ID->人物指标实例
        Map<String, List<ExperimentIndicatorInstanceRsEntity>> personIndicator = eies.stream()
                .collect(Collectors.groupingBy(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId));

        Map<String, ExperimentIndicatorInstanceRsEntity> experimentIndicatorInstanceRsEntityMap = eies.stream()
                .collect(Collectors.toMap(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, Function.identity()));

        // 一个实验有多少个NPC人物?
        personIndicator.forEach((k, v) -> {
            // 一个人物有多少相关的指标表达式
            List<String> ids = v.stream()
                    .map(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId)
                    .collect(Collectors.toList());

            /**
             * 人物相关的指标表达式对象
             */
            Map<String, ExperimentIndicatorExpressionRsEntity> experimentIndicatorExpressionRsEntityMap =
                    experimentIndicatorExpressionRsService.lambdaQuery()
                            .eq(ExperimentIndicatorExpressionRsEntity::getExperimentId, experimentInstanceId)
                            .eq(ExperimentIndicatorExpressionRsEntity::getSource, EnumIndicatorExpressionSource.RISK_MODEL.getSource())
                            .in(ExperimentIndicatorExpressionRsEntity::getPrincipalId, ids)
                            .list()
                            .stream()
                            .collect(Collectors.toMap(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId, Function.identity()));
            // 人物相关的指标表达式对象ID
            List<String> experimentIndicatorExpressionRsEntityIds = experimentIndicatorExpressionRsEntityMap.keySet().stream().toList();


            /**
             * 表达式引用，用于找出resonId 即死亡原因
             */
            List<ExperimentIndicatorExpressionRefRsEntity> list = experimentIndicatorExpressionRefRsService.lambdaQuery()
                    .eq(ExperimentIndicatorExpressionRefRsEntity::getExperimentId, experimentInstanceId)
                    .in(ExperimentIndicatorExpressionRefRsEntity::getIndicatorExpressionId, experimentIndicatorExpressionRsEntityIds)
                    .list();
            // 按原因ID分组，一个原因可能有多个危险因素
            Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> eierMapList = list.stream()
                    .collect(Collectors.groupingBy(ExperimentIndicatorExpressionRefRsEntity::getReasonId));
            // 所有死亡原因id集合
            List<String> resonIds = eierMapList.keySet().stream().toList();
            /**
             * 按原因ID进行k-v映射
             * Map<String, ExperimentIndicatorExpressionRefRsEntity> experimentIndicatorExpressionRefRsEntityMap = list.stream()
             *                     .collect(Collectors.toMap(ExperimentIndicatorExpressionRefRsEntity::getExperimentIndicatorExpressionRefId, Function.identity()));
             */

            /**
             * 人物死亡原因对象集合
             */
            List<ExperimentRiskModelRsEntity> experimentRiskModelRsEntityList = experimentRiskModelRsService.lambdaQuery()
                    .eq(ExperimentRiskModelRsEntity::getExperimentId, experimentInstanceId)
                    .in(ExperimentRiskModelRsEntity::getExperimentRiskModelId, resonIds)
                    .list();
            // 人群id->
            Map<String, List<ExperimentRiskModelRsEntity>> experimentRiskModelRsEntityMap = experimentRiskModelRsEntityList.stream()
                    .collect(Collectors.groupingBy(ExperimentRiskModelRsEntity::getCrowdsCategoryId));

            /**
             * 人群
             *
             * todo 所属人群，判断当前用户那一类人，报告暂时不需要，标签需要
             * List<ExperimentCrowdsInstanceRsEntity> experimentCrowdsInstanceRsEntityList = experimentCrowdsInstanceRsService.lambdaQuery()
             *                     .eq(ExperimentCrowdsInstanceRsEntity::getExperimentId, experimentInstanceId)
             *                     .in(ExperimentCrowdsInstanceRsEntity::getExperimentCrowdsId, experimentRiskModelRsEntityMap.keySet())
             *                     .list();
             */

            PersonRiskFactor personRiskFactor = new PersonRiskFactor();
            personRiskFactor.setPersonId(k);
            personRiskFactor.setPersonName("" + k);
            //组装数据
            for (ExperimentRiskModelRsEntity experimentRiskModelRsEntity : experimentRiskModelRsEntityList) {
                // 风险名称
                String riskName = experimentRiskModelRsEntity.getName();
                //死亡率
                Integer riskDeathProbability = experimentRiskModelRsEntity.getRiskDeathProbability();

                PersonRiskFactor.RiskFactor riskFactor = new PersonRiskFactor.RiskFactor();
                riskFactor.setRiskName(riskName);
                riskFactor.setRiskDeathProbability(riskDeathProbability);

                riskFactor.setRiskScore("");
                riskFactor.setDeathRiskScore("");

                //personRiskFactor.setRiskFactor(riskFactor);
                List<PersonRiskFactor.RiskItem> riskItems = new ArrayList<>();
                riskFactor.setRiskItems(riskItems);
                /**
                 * 根据ID获取危险因素,组装死亡原因对应的危险因素集合
                 */
                String experimentRiskModelId = experimentRiskModelRsEntity.getExperimentRiskModelId();
                List<ExperimentIndicatorExpressionRefRsEntity> entities = eierMapList.get(experimentRiskModelId);
                for (ExperimentIndicatorExpressionRefRsEntity entity : entities) {


                    /**
                     * 通过获取指标表达式对应的ID获取表达式对象
                     */
                    ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity =
                            experimentIndicatorExpressionRsEntityMap.get(entity.getIndicatorExpressionId());
                    /**
                     * 根据获取对应的指标实例ID获取对应的指标实例对象
                     */
                    ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity =
                            experimentIndicatorInstanceRsEntityMap.get(experimentIndicatorExpressionRsEntity.getPrincipalId());


                    // 组装Item
                    PersonRiskFactor.RiskItem riskItem = new PersonRiskFactor.RiskItem();
                    riskItem.setItemName(experimentIndicatorInstanceRsEntity.getIndicatorName());

                    //riskItem.setItemValue();

                    //riskItem.setRiskScore();
                    riskItems.add(riskItem);
                }
            }
        });
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
