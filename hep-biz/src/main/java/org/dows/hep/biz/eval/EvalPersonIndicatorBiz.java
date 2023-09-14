package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.RsCalculatePersonRequestRs;
import org.dows.hep.api.enums.EnumIndicatorExpressionField;
import org.dows.hep.api.enums.EnumIndicatorExpressionScene;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.base.indicator.RsExperimentIndicatorExpressionBiz;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.eval.data.EvalPersonSyncRequest;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.request.ExperimentCalIndicatorExpressionRequest;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorExpressionItemRsEntity;
import org.dows.hep.entity.ExperimentIndicatorExpressionRsEntity;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : wuzl
 * @date : 2023/9/7 19:37
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EvalPersonIndicatorBiz {
    private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;

    private final ExperimentPersonCache experimentPersonCache;
    private final PersonIndicatorIdCache personIndicatorIdCache;

    private final EvalPersonCache evalPersonCache;


    @SneakyThrows
    public void evalPersonIndicator(RsCalculatePersonRequestRs req)  {
        StringBuilder sb=new StringBuilder("EVALTRACE--evalIndicator--");
        try {
            long ts=System.currentTimeMillis();
            final String experimentId = req.getExperimentId();
            final Set<String> experimentPersonIdSet = experimentPersonCache.getPersondIdSet(experimentId, req.getPersonIdSet());
            if (ShareUtil.XObject.isEmpty(experimentPersonIdSet)) {
                return;
            }
            ts=logCostTime(sb,"1-personid", ts);
            Set<String> reasonIdSet = new HashSet<>();
            Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap = new HashMap<>();
            Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
            Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();
            experimentPersonIdSet.forEach(i -> reasonIdSet.addAll(personIndicatorIdCache.getIndicatorIds(i)));
            rsExperimentIndicatorExpressionBiz.populateParseParam(
                    reasonIdSet,
                    kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                    kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                    kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap
            );
            ts=logCostTime(sb,"2-spel", ts);

            ExperimentTimePoint timePoint = ExperimentSettingCache.Instance().getTimePointByRealTimeSilence(ExperimentCacheKey.create(req.getAppId(), req.getExperimentId()), LocalDateTime.now(), true);
            EvalPersonSyncRequest evalReq = new EvalPersonSyncRequest()
                    .setFuncType(req.getFuncType())
                    .setTimePoint(timePoint);
            ts=logCostTime(sb,"3-timepoint", ts);

            final int CONCURRENTNum = 4;
            if (experimentPersonIdSet.size() < CONCURRENTNum) {
                evalPersonIndicator(experimentId, experimentPersonIdSet, evalReq,
                        kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                        kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                        kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap);
            } else {
                List<List<String>> groups = ShareUtil.XCollection.split(List.copyOf(experimentPersonIdSet), CONCURRENTNum);
                CompletableFuture[] futures = new CompletableFuture[groups.size()];
                int pos = 0;
                for (List<String> personIds : groups) {
                    futures[pos++] = CompletableFuture.runAsync(() -> evalPersonIndicator(experimentId, personIds, evalReq,
                                    kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                                    kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                    kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap),
                            EvalPersonExecutor.Instance().getThreadPool());
                }
                CompletableFuture.allOf(futures).join();
            }
            ts=logCostTime(sb,"4-eval", ts);
        }finally {
            log.error(sb.toString());
            log.info(sb.toString());
            sb.setLength(0);
        }

    }
    private void evalPersonIndicator(String experimentId, Collection<String> personIds, EvalPersonSyncRequest evalReq,
                                     Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                                     Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                     Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap) {
        for (String personId : personIds) {
            evalPersonIndicator(experimentId, personId, evalReq,
                    kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                    kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                    kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap
            );
        }
    }


    private void evalPersonIndicator(String experimentId, String experimentPersonId, EvalPersonSyncRequest evalReq,
                                Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                                Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap) {

        List<ExperimentIndicatorInstanceRsEntity> indicators = personIndicatorIdCache.getSortedIndicators(experimentPersonId);
        if (ShareUtil.XObject.isEmpty(indicators)) {
            return;
        }
        final EvalPersonPointer evalPointer = evalPersonCache.getPointer(experimentId, experimentPersonId);
        final EvalPersonOnceHolder evalHolder = evalPointer.getCurHolder();
        evalPointer.startSync(evalReq);
        Map<String, ExperimentIndicatorValRsEntity> mapCurVal = evalHolder.get().getOldMap(true);
        Map<String, ExperimentIndicatorValRsEntity> mapLastVal = evalHolder.getLastHolder().get().getOldMap(true);
        for (ExperimentIndicatorInstanceRsEntity item : indicators) {
            ExperimentIndicatorValRsEntity indicatorOld = mapCurVal.get(item.getExperimentIndicatorInstanceId());
            EvalIndicatorValues indicatorNew = evalHolder.getIndicator(item.getExperimentIndicatorInstanceId());
            if (ShareUtil.XObject.anyEmpty(indicatorOld, indicatorNew)) {
                continue;
            }
            String evalVal = evalPersonIndicator(item.getExperimentIndicatorInstanceId(), mapCurVal, mapLastVal,
                    kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                    kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap);
            if (ShareUtil.XObject.notEmpty(evalVal)) {
                indicatorNew.setCurVal(evalVal);
            }
            evalHolder.syncIndicator(indicatorNew);
            indicatorOld.setCurrentVal(indicatorNew.getCurVal());

        }


    }
    private String evalPersonIndicator(String indicatorId,
                                    Map<String, ExperimentIndicatorValRsEntity> mapCurVal,
                                    Map<String, ExperimentIndicatorValRsEntity> mapLastVal,
                                    Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                                    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap
                                    ) {



        List<ExperimentIndicatorExpressionRsEntity> experimentIndicatorExpressionRsEntityList = kReasonIdVExperimentIndicatorExpressionRsEntityListMap.get(indicatorId);
        if (ShareUtil.XObject.isEmpty(experimentIndicatorExpressionRsEntityList)) {
            return null;
        }
        ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = experimentIndicatorExpressionRsEntityList.get(0);
        String experimentIndicatorExpressionId = experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
        List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
        if (ShareUtil.XObject.isEmpty(experimentIndicatorExpressionItemRsEntityList)) {
            return null;
        }
        AtomicReference<String> curValRef = new AtomicReference<>();
        rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
                EnumIndicatorExpressionField.EXPERIMENT.getField(),
                EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource(),
                EnumIndicatorExpressionScene.EXPERIMENT_RE_CALCULATE.getScene(),
                curValRef,
                null,
                null,
                null,
                ExperimentCalIndicatorExpressionRequest
                        .builder()
                        .kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(mapCurVal)
                        .lastKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(mapLastVal)
                        .experimentIndicatorExpressionRsEntity(experimentIndicatorExpressionRsEntity)
                        .experimentIndicatorExpressionItemRsEntityList(experimentIndicatorExpressionItemRsEntityList)
                        .minExperimentIndicatorExpressionItemRsEntity(null)
                        .maxExperimentIndicatorExpressionItemRsEntity(null)
                        .build()
        );
        return curValRef.get();


    }

    long logCostTime(StringBuilder sb,String func,long ts){
        long newTs=System.currentTimeMillis();
        sb.append(" ").append(func).append(":").append((newTs-ts));
        return newTs;
    }
}
