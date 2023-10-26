package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BaseException;
import org.dows.hep.api.base.indicator.request.RsCopyPersonIndicatorRequestRs;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/9/14 19:13
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CopyPersonBiz {

    private final ExperimentPersonService experimentPersonService;

    private final CasePersonService casePersonService;

    private final CaseIndicatorInstanceService caseIndicatorInstanceService;

    private final CaseIndicatorRuleService caseIndicatorRuleService;

    private final IdGenerator idGenerator;

    private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
    private final ExperimentIndicatorValRsService experimentIndicatorValRsService;

    private final EvalPersonBiz evalPersonBiz;


    @Transactional(rollbackFor = Exception.class)
    public void rsCopyPersonIndicator(RsCopyPersonIndicatorRequestRs rsCopyPersonIndicatorRequestRs)  {
        Map<String, Integer> kCaseIndicatorInstanceIdVSeqMap = new HashMap<>();
        String caseInstanceId = rsCopyPersonIndicatorRequestRs.getCaseInstanceId();

        List<ExperimentIndicatorInstanceRsEntity> experimentIndicatorInstanceRsEntityList = new ArrayList<>();
        List<ExperimentIndicatorValRsEntity> experimentIndicatorValRsEntityList = new ArrayList<>();
        Map<String, Map<String, String>> kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
        Map<String, String> kExperimentIndicatorInstanceIdVExperimentPersonIdMap = new HashMap<>();
        Map<String, CaseIndicatorExpressionItemEntity> kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap = new HashMap<>();
        String experimentInstanceId = rsCopyPersonIndicatorRequestRs.getExperimentInstanceId();
        String appId = rsCopyPersonIndicatorRequestRs.getAppId();
        Set<String> experimentPersonIdSet = new HashSet<>();
        Set<String> casePersonIdSet = new HashSet<>();
        Set<String> principalIdSet = new HashSet<>();
        Map<String, ExperimentPersonEntity> kExperimentPersonIdVExperimentPersonEntityMap = new HashMap<>();
        Map<String, List<CaseIndicatorInstanceEntity>> kPrincipalIdVCaseIndicatorInstanceEntityListMap = new HashMap<>();
        Set<String> caseIndicatorInstanceIdSet = new HashSet<>();
        Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap = new HashMap<>();
        Map<String, String> kCasePersonIdVAccountIdMap = new HashMap<>();
        Map<String, List<String>> kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdListMap = new HashMap<>();
        Integer maxPeriods = rsCopyPersonIndicatorRequestRs.getPeriods();
        experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getAppId, appId)
                .eq(ExperimentPersonEntity::getExperimentInstanceId, experimentInstanceId)
                .list()
                .forEach(experimentPersonEntity -> {
                    casePersonIdSet.add(experimentPersonEntity.getCasePersonId());
                    experimentPersonIdSet.add(experimentPersonEntity.getExperimentPersonId());
                    kExperimentPersonIdVExperimentPersonEntityMap.put(experimentPersonEntity.getExperimentPersonId(), experimentPersonEntity);
                });
        if (experimentPersonIdSet.isEmpty() || casePersonIdSet.isEmpty()) {
            log.warn("method rsCopyPersonIndicator experimentInstanceId:{} has no person", experimentInstanceId);
            throw new BaseException("案例人物列表为空");

        }
        casePersonService.lambdaQuery()
                .in(CasePersonEntity::getCasePersonId, casePersonIdSet)
                .list()
                .forEach(casePersonEntity -> {
                    kCasePersonIdVAccountIdMap.put(casePersonEntity.getCasePersonId(), casePersonEntity.getAccountId());
                    principalIdSet.add(casePersonEntity.getAccountId());
                });
        if (principalIdSet.isEmpty()) {
            log.warn("method rsCopyPersonIndicator principalIdSet:is empty");
            throw new BaseException("uim人物列表为空");
        }
        /* runsix:defined by yourself */
        caseIndicatorInstanceService.lambdaQuery()
                .eq(CaseIndicatorInstanceEntity::getAppId, appId)
                .in(CaseIndicatorInstanceEntity::getPrincipalId, principalIdSet)
                .isNotNull(CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId)
                .list()
                .forEach(caseIndicatorInstanceEntity -> {
                    caseIndicatorInstanceIdSet.add(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId());
                    List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList = kPrincipalIdVCaseIndicatorInstanceEntityListMap.get(caseIndicatorInstanceEntity.getPrincipalId());
                    if (Objects.isNull(caseIndicatorInstanceEntityList)) {
                        caseIndicatorInstanceEntityList = new ArrayList<>();
                    }
                    caseIndicatorInstanceEntityList.add(caseIndicatorInstanceEntity);
                    kPrincipalIdVCaseIndicatorInstanceEntityListMap.put(caseIndicatorInstanceEntity.getPrincipalId(), caseIndicatorInstanceEntityList);
                });
        if (caseIndicatorInstanceIdSet.isEmpty()) {
            log.warn("method rsCopyPersonIndicator caseInstanceId:{} has no indicator", caseInstanceId);
            throw new BaseException("案例指标列表为空");
        }
        caseIndicatorRuleService.lambdaQuery()
                .eq(CaseIndicatorRuleEntity::getAppId, appId)
                .in(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceIdSet)
                .list()
                .forEach(caseIndicatorRuleEntity -> {
                    kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.put(caseIndicatorRuleEntity.getVariableId(), caseIndicatorRuleEntity);
                });
        kExperimentPersonIdVExperimentPersonEntityMap.forEach((experimentPersonId, experimentPersonEntity) -> {
            String casePersonId = experimentPersonEntity.getCasePersonId();
            String accountId = kCasePersonIdVAccountIdMap.get(casePersonId);
            List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList = kPrincipalIdVCaseIndicatorInstanceEntityListMap.get(accountId);
            if (Objects.isNull(caseIndicatorInstanceEntityList)) {
                log.warn("method rsCopyPersonIndicator casePersonId:{}, accountId:{} has no indicator", casePersonId, accountId);
                throw new BaseException(String.format("案例人物[%s]指标列表为空", casePersonId));
            }
            caseIndicatorInstanceEntityList.forEach(caseIndicatorInstanceEntity -> {
                String caseIndicatorInstanceId = caseIndicatorInstanceEntity.getCaseIndicatorInstanceId();
                String min = null;
                String max = null;
                String def = null;
                CaseIndicatorRuleEntity caseIndicatorRuleEntity = kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId());
                if (Objects.nonNull(caseIndicatorRuleEntity)) {
                    min = caseIndicatorRuleEntity.getMin();
                    max = caseIndicatorRuleEntity.getMax();
                    def = caseIndicatorRuleEntity.getDef();
                }
                String experimentIndicatorInstanceId = idGenerator.nextIdStr();
                List<String> experimentIndicatorInstanceIdList = kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdListMap.get(caseIndicatorInstanceId);
                if (Objects.isNull(experimentIndicatorInstanceIdList)) {
                    experimentIndicatorInstanceIdList = new ArrayList<>();
                }
                experimentIndicatorInstanceIdList.add(experimentIndicatorInstanceId);
                kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdListMap.put(caseIndicatorInstanceId, experimentIndicatorInstanceIdList);
                Map<String, String> kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(experimentPersonId);
                if (Objects.isNull(kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap)) {
                    kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
                }
                kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.put(caseIndicatorInstanceId, experimentIndicatorInstanceId);
                kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.put(experimentPersonId, kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap);
                kExperimentIndicatorInstanceIdVExperimentPersonIdMap.put(experimentIndicatorInstanceId, experimentPersonId);
                Integer recalculateSeq = kCaseIndicatorInstanceIdVSeqMap.get(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId());
                experimentIndicatorInstanceRsEntityList.add(
                        ExperimentIndicatorInstanceRsEntity
                                .builder()
                                .experimentIndicatorInstanceId(experimentIndicatorInstanceId)
                                .caseIndicatorInstanceId(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId())
                                .indicatorInstanceId(caseIndicatorInstanceEntity.getIndicatorInstanceId())
                                .experimentId(experimentInstanceId)
                                .caseId(caseInstanceId)
                                .experimentPersonId(experimentPersonId)
                                .indicatorName(caseIndicatorInstanceEntity.getIndicatorName())
                                .displayByPercent(caseIndicatorInstanceEntity.getDisplayByPercent())
                                .unit(caseIndicatorInstanceEntity.getUnit())
                                .core(caseIndicatorInstanceEntity.getCore())
                                .food(caseIndicatorInstanceEntity.getFood())
                                .type(caseIndicatorInstanceEntity.getType())
                                .descr(caseIndicatorInstanceEntity.getDescr())
                                .valueType(caseIndicatorInstanceEntity.getValueType())
                                .min(min)
                                .max(max)
                                .def(def)
                                .recalculateSeq(recalculateSeq)
                                .build()
                );
            });
        });
        experimentIndicatorInstanceRsEntityList.forEach(experimentIndicatorInstanceRsEntity -> {
            if(ConfigExperimentFlow.SWITCH2EvalCache){
                experimentIndicatorValRsEntityList.add(
                        ExperimentIndicatorValRsEntity
                                .builder()
                                .experimentIndicatorValId(idGenerator.nextIdStr())
                                .experimentId(experimentInstanceId)
                                .caseId(caseInstanceId)
                                .indicatorInstanceId(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId())
                                .currentVal(experimentIndicatorInstanceRsEntity.getDef())
                                .initVal(experimentIndicatorInstanceRsEntity.getDef())
                                .periods(1)
                                .min(experimentIndicatorInstanceRsEntity.getMin())
                                .max(experimentIndicatorInstanceRsEntity.getMax())
                                .descr(experimentIndicatorInstanceRsEntity.getDescr())
                                .indicatorInstance(experimentIndicatorInstanceRsEntity)
                                .build());
            }else {
                for (int i = 1; i <= maxPeriods; i++) {
                    experimentIndicatorValRsEntityList.add(
                            ExperimentIndicatorValRsEntity
                                    .builder()
                                    .experimentIndicatorValId(idGenerator.nextIdStr())
                                    .experimentId(experimentInstanceId)
                                    .caseId(caseInstanceId)
                                    .indicatorInstanceId(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId())
                                    .currentVal(experimentIndicatorInstanceRsEntity.getDef())
                                    .initVal(experimentIndicatorInstanceRsEntity.getDef())
                                    .periods(i)
                                    .min(experimentIndicatorInstanceRsEntity.getMin())
                                    .max(experimentIndicatorInstanceRsEntity.getMax())
                                    .descr(experimentIndicatorInstanceRsEntity.getDescr())
                                    .build()
                    );
                }
            }
        });

        experimentIndicatorInstanceRsService.saveOrUpdateBatch(experimentIndicatorInstanceRsEntityList);
        if(ConfigExperimentFlow.SWITCH2EvalCache){
            evalPersonBiz.initEvalPersonLog(experimentIndicatorValRsEntityList);
            //experimentIndicatorValRsService.saveOrUpdateBatch(experimentIndicatorValRsEntityList);
        }else {
            experimentIndicatorValRsService.saveOrUpdateBatch(experimentIndicatorValRsEntityList);
        }

    }

}
