package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.RsCalculatePersonRequestRs;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.base.indicator.RsExperimentIndicatorExpressionBiz;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.eval.data.EvalPersonSyncRequest;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.spel.SpelEngine;
import org.dows.hep.biz.spel.SpelPersonContext;
import org.dows.hep.biz.spel.meta.SpelEvalResult;
import org.dows.hep.biz.spel.meta.SpelEvalSumResult;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author : wuzl
 * @date : 2023/9/7 19:37
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EvalPersonIndicatorAdvBiz {
    private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;

    private final ExperimentPersonCache experimentPersonCache;
    private final PersonIndicatorIdCache personIndicatorIdCache;

    private final EvalPersonCache evalPersonCache;


    @SneakyThrows
    public void evalPersonIndicator(RsCalculatePersonRequestRs req)  {
        StringBuilder sb=new StringBuilder();
        long ts=logCostTime(sb,"EVALTRACE--evalIndicator--");
        try {
            final String experimentId = req.getExperimentId();
            final Set<String> experimentPersonIdSet = experimentPersonCache.getPersondIdSet(experimentId, req.getPersonIdSet());
            if (ShareUtil.XObject.isEmpty(experimentPersonIdSet)) {
                return;
            }
            ts=logCostTime(sb,"1-personIds", ts);

            ExperimentTimePoint timePoint = ExperimentSettingCache.Instance().getTimePointByRealTimeSilence(ExperimentCacheKey.create(req.getAppId(), req.getExperimentId()), LocalDateTime.now(), true);
            EvalPersonSyncRequest evalReq = new EvalPersonSyncRequest()
                    .setFuncType(req.getFuncType())
                    .setTimePoint(timePoint);
            ts=logCostTime(sb,"2-timepoint", ts);

            final int CONCURRENTNum = 5;
            if (CONCURRENTNum<=1||experimentPersonIdSet.size() < CONCURRENTNum) {
                evalPersonIndicator(experimentId, experimentPersonIdSet, evalReq);
            } else {
                List<List<String>> groups = ShareUtil.XCollection.split(List.copyOf(experimentPersonIdSet), CONCURRENTNum);
                CompletableFuture[] futures = new CompletableFuture[groups.size()];
                int pos = 0;
                for (List<String> personIds : groups) {
                    futures[pos++] = CompletableFuture.runAsync(() -> evalPersonIndicator(experimentId, personIds, evalReq),
                            EvalPersonExecutor.Instance().getThreadPool());
                }
                CompletableFuture.allOf(futures).join();
            }
            ts=logCostTime(sb,"3-eval", ts);
        }finally {
            log.error(sb.toString());
            log.info(sb.toString());
            sb.setLength(0);
        }

    }
    private void evalPersonIndicator(String experimentId, Collection<String> personIds, EvalPersonSyncRequest evalReq) {
        for (String personId : personIds) {
            evalPersonIndicator(experimentId, personId, evalReq);
        }
    }


    private void evalPersonIndicator(String experimentId, String experimentPersonId, EvalPersonSyncRequest evalReq) {

        List<ExperimentIndicatorInstanceRsEntity> indicators = personIndicatorIdCache.getSortedIndicators(experimentPersonId);
        if (ShareUtil.XObject.isEmpty(indicators)) {
            return;
        }
        final EvalPersonPointer evalPointer = evalPersonCache.getPointer(experimentId, experimentPersonId);
        final EvalPersonOnceHolder evalHolder = evalPointer.getCurHolder();
        evalPointer.startSync(evalReq);
        final Integer source=EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource();
        SpelPersonContext context = new SpelPersonContext().setVariables(experimentPersonId, null);
        Map<String, SpelEvalSumResult> mapSum=new HashMap<>();
        for (ExperimentIndicatorInstanceRsEntity item : indicators) {
            final String indicatorInstanceId = item.getExperimentIndicatorInstanceId();
            mapSum.clear();
            EvalIndicatorValues values = evalHolder.getIndicator(indicatorInstanceId);
            if (null == values) {
                continue;
            }
            SpelEvalResult evalRst = SpelEngine.Instance().loadFromSpelCache().withReasonId(experimentId, experimentPersonId, item.getCaseIndicatorInstanceId(), source)
                    .evalSum(context, mapSum);
            if (ShareUtil.XObject.allNotEmpty(evalRst, () -> evalRst.getVal())) {
                values.setCurVal(evalRst.getValString());
            }
            evalHolder.syncIndicator(values);
            context.setCurVal(experimentPersonId, indicatorInstanceId, values.getCurVal());
        }
    }

    private long logCostTime(StringBuilder sb,String start){
        sb.append(start);
        return System.currentTimeMillis();
    }
    private long logCostTime(StringBuilder sb,String func,long ts){
        long newTs=System.currentTimeMillis();
        sb.append(" ").append(func).append(":").append((newTs - ts));
        return newTs;
    }
}
