package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentRsCalculateAndCreateReportHealthScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculatePeriodsRequest;
import org.dows.hep.api.base.indicator.request.RsCalculatePersonRequestRs;
import org.dows.hep.api.base.indicator.request.RsExperimentCalculateFuncRequest;
import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.api.user.experiment.request.ExperimentPersonRequest;
import org.dows.hep.biz.dao.ExperimentEvalLogDao;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.event.PersonBasedEventTask;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.dows.hep.biz.user.person.PersonStatiscBiz;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.entity.ExperimentEvalLogContentEntity;
import org.dows.hep.entity.ExperimentEvalLogEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/9/7 17:40
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class EvalPersonBiz {

    private final ExperimentEvalLogDao experimentEvalLogDao;
    private final IdGenerator idGenerator;

    private final EvalPersonIndicatorBiz evalPersonIndicatorBiz;

    private final EvalHealthIndexBiz evalHealthIndexBiz;

    private final PersonStatiscBiz personStatiscBiz;

    private final ExperimentScoringBiz experimentScoringBiz;


    public boolean initEvalPersonLog(List<ExperimentIndicatorValRsEntity> src){
        String experimentId="";
        if(!src.isEmpty()){
            experimentId=src.get(0).getExperimentId();
        }
        try {
            final String APPId="3";
            final Date dtNow=new Date();
            Map<String,List<ExperimentIndicatorValRsEntity>> map=new HashMap<>();
            src.forEach(i->map.computeIfAbsent(i.getIndicatorInstance().getExperimentPersonId(), k->new ArrayList<>()).add(i));
            List<ExperimentEvalLogEntity> rowsEval=new ArrayList<>();
            List<ExperimentEvalLogContentEntity> rowsEvalContent=new ArrayList<>();
            map.forEach((k,v)->{
                ExperimentIndicatorValRsEntity first=v.get(0);
                ExperimentEvalLogEntity rowEval=new ExperimentEvalLogEntity()
                        .setAppId(APPId)
                        .setExperimentEvalLogId(idGenerator.nextIdStr())
                        .setExperimentInstanceId(first.getExperimentId())
                        .setExperimentPersonId(first.getIndicatorInstance().getExperimentPersonId())
                        .setEvalNo(0)
                        .setFuncType(0)
                        .setPeriods(1)
                        .setEvalDay(0)
                        .setEvalingTime(dtNow)
                        .setEvaledTime(dtNow)
                        .setLastEvalDay(0);
                List<EvalIndicatorValues> indicators=new ArrayList<>();
                v.forEach(item->{
                    if(EnumIndicatorType.MONEY.getType().equals(item.getIndicatorInstance().getType())){
                        rowEval.setMoney(item.getCurrentVal());
                    }else if(EnumIndicatorType.HEALTH_POINT.getType().equals(item.getIndicatorInstance().getType())){
                        rowEval.setHealthIndex(item.getCurrentVal());
                    }
                    indicators.add(new EvalIndicatorValues()
                            .setEvalNo(0)
                            .setIndicatorId(item.getIndicatorInstanceId())
                            .setIndicatorName(item.getIndicatorInstance().getIndicatorName())
                            .setCurVal(item.getIndicatorInstance().getDef())
                            .setPeriodInitVal(item.getIndicatorInstance().getDef())
                    );
                });
                ExperimentEvalLogContentEntity rowEvalContent=new ExperimentEvalLogContentEntity()
                        .setEvalNo(0)
                        .setExperimentEvalLogId(rowEval.getExperimentEvalLogId())
                        .setExperimentEvalLogContentId(idGenerator.nextIdStr())
                        .setAppId(APPId)
                        .setIndicatorContent(JacksonUtil.toJsonSilence(indicators, true));
                rowsEval.add(rowEval);
                rowsEvalContent.add(rowEvalContent);

            });
            return experimentEvalLogDao.tranSaveBatch(rowsEval, rowsEvalContent, false, true);

        }catch (Exception ex){
            log.error(String.format( "人物初始指标复制失败[id:%s]",experimentId),ex);
            throw ex;
        }

    }


    /**
     * 功能点结算
     * @param req
     */

    public void evalOrgFunc(RsExperimentCalculateFuncRequest req){
        String appId = req.getAppId();
        String experimentId = req.getExperimentId();
        Integer periods = req.getPeriods();
        Set<String> personIds=Set.of(req.getExperimentPersonId());

        evalPersonIndicatorBiz.evalPersonIndicator(RsCalculatePersonRequestRs
                .builder()
                .appId(appId)
                .experimentId(experimentId)
                .periods(periods)
                .personIdSet(null)
                .funcType(req.getFuncType())
                .build());
        evalHealthIndexBiz.evalPersonHealthIndex(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs
                .builder()
                .appId(appId)
                .experimentId(experimentId)
                .periods(periods)
                .funcType(req.getFuncType())
                .build());

        PersonBasedEventTask.runPersonBasedEventAsync(appId,experimentId);

    }
    /**
     * 期数翻转
     * @param req
     */
    public void evalPeriodEnd(RsCalculatePeriodsRequest req)  {
        final String appId = req.getAppId();
        final String experimentId = req.getExperimentId();
        final Integer periods = req.getPeriods();

        personStatiscBiz.refundFunds(ExperimentPersonRequest.builder()
                .experimentInstanceId(experimentId)
                .appId(appId)
                .periods(periods)
                .build());
        evalPersonIndicatorBiz.evalPersonIndicator(RsCalculatePersonRequestRs
                .builder()
                .appId(appId)
                .experimentId(experimentId)
                .periods(periods)
                .personIdSet(null)
                .funcType(EnumEvalFuncType.PERIODEnd)
                .build());
        evalHealthIndexBiz.evalPersonHealthIndex(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs
                .builder()
                .appId(appId)
                .experimentId(experimentId)
                .periods(periods)
                .funcType(EnumEvalFuncType.PERIODEnd)
                .build());

        experimentScoringBiz.saveOrUpd(experimentId, periods);

        PersonBasedEventTask.runPersonBasedEventAsync(appId,experimentId);

    }
}
