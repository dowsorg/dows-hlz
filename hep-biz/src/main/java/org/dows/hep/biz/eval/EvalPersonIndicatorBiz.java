package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : wuzl
 * @date : 2023/9/7 19:37
 */
@Component
@RequiredArgsConstructor
public class EvalPersonIndicatorBiz {
    private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;

    private final ExperimentPersonCache experimentPersonCache;
    private final PersonIndicatorIdCache personIndicatorIdCache;

    private final EvalPersonCache evalPersonCache;


    @SneakyThrows
    public void evalPersonIndicator(RsCalculatePersonRequestRs req)  {
        final String experimentId = req.getExperimentId();
        final Set<String> experimentPersonIdSet =experimentPersonCache.getPersondIdSet(experimentId,req.getPersonIdSet());
        if(ShareUtil.XObject.isEmpty(experimentPersonIdSet)){
            return;
        }
        Set<String> reasonIdSet = new HashSet<>();
        Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap = new HashMap<>();
        Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
        Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();
        experimentPersonIdSet.forEach(i->reasonIdSet.addAll(personIndicatorIdCache.getIndicatorIds(i)));
        rsExperimentIndicatorExpressionBiz.populateParseParam(
                reasonIdSet,
                kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap
        );
        final Map<String, ExperimentIndicatorValRsEntity> mapCurVal=new HashMap<>();
        final Map<String, ExperimentIndicatorValRsEntity> mapLastVal=new HashMap<>();
        ExperimentTimePoint timePoint= ExperimentSettingCache.Instance().getTimePointByRealTimeSilence(ExperimentCacheKey.create(req.getAppId(),req.getExperimentId()), LocalDateTime.now(), true);
        EvalPersonSyncRequest evalReq=new EvalPersonSyncRequest()
                .setFuncType(req.getFuncType())
                .setTimePoint(timePoint);
        for(String personId:experimentPersonIdSet) {
            evalPersonIndicator(experimentId, personId, evalReq, mapCurVal, mapLastVal,
                    kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                    kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                    kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap
            );
        }
    }

    private void evalPersonIndicator(String experimentId, String experimentPersonId, EvalPersonSyncRequest evalReq,
                                    Map<String, ExperimentIndicatorValRsEntity> mapCurVal,
                                    Map<String, ExperimentIndicatorValRsEntity> mapLastVal,
                                    Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                                    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                    Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap) {

        List<ExperimentIndicatorInstanceRsEntity> indicators= personIndicatorIdCache.getSortedIndicators(experimentPersonId);
        if(ShareUtil.XObject.isEmpty(indicators)){
            return;
        }
        final EvalPersonPointer evalPointer=evalPersonCache.getPointer(experimentId,experimentPersonId);
        final EvalPersonOnceHolder evalHolder=evalPointer.getCurHolder();
        evalPointer.startSync(evalReq);
        evalHolder.fillCastMapCur(mapCurVal);
        evalHolder.fillCastMapLast(mapLastVal);
        for(ExperimentIndicatorInstanceRsEntity item:indicators){
            evalPersonIndicator(evalHolder,item,mapCurVal,mapLastVal,
                    kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                    kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                    kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap);
        }
        //evalPointer.sync(evalReq);
    }
    private void evalPersonIndicator(EvalPersonOnceHolder evalHolder,
                                    ExperimentIndicatorInstanceRsEntity indicator,
                                    Map<String, ExperimentIndicatorValRsEntity> mapCurVal,
                                    Map<String, ExperimentIndicatorValRsEntity> mapLastVal,
                                    Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
                                    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                    Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap) {

        ExperimentIndicatorValRsEntity indicatorOld = mapCurVal.get(indicator.getExperimentIndicatorInstanceId());
        EvalIndicatorValues  indicatorNew= evalHolder.getIndicator(indicator.getIndicatorInstanceId());
        if (ShareUtil.XObject.anyEmpty(indicatorOld,indicatorNew)) {
            return;
        }

        List<ExperimentIndicatorExpressionRsEntity> experimentIndicatorExpressionRsEntityList = kReasonIdVExperimentIndicatorExpressionRsEntityListMap.get(indicator.getExperimentIndicatorInstanceId());
        if (ShareUtil.XObject.isEmpty(experimentIndicatorExpressionRsEntityList)) {
            return;
        }
        ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = experimentIndicatorExpressionRsEntityList.get(0);
        String experimentIndicatorExpressionId = experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
        List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
        if (ShareUtil.XObject.isEmpty(experimentIndicatorExpressionItemRsEntityList)) {
            return;
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
        String curVal = curValRef.get();
        if (ShareUtil.XObject.notEmpty(curVal)) {
            indicatorNew.setCurVal(curVal);
        }
        evalHolder.syncIndicator(indicatorNew);
        indicatorOld.setCurrentVal(indicatorNew.getCurVal());

    }
}
