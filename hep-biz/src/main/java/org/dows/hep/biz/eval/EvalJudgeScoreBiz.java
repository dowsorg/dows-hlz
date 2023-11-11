package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.dao.ExperimentJudgeScoreLogDao;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.spel.SpelEngine;
import org.dows.hep.biz.spel.SpelPersonContext;
import org.dows.hep.biz.spel.meta.SpelEvalResult;
import org.dows.hep.biz.spel.meta.SpelInput;
import org.dows.hep.biz.util.*;
import org.dows.hep.entity.ExperimentJudgeScoreLogEntity;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.properties.ScoreSettingsProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/10/22 0:11
 */
@Component
@RequiredArgsConstructor
public class EvalJudgeScoreBiz {

    private final ExperimentJudgeScoreLogDao experimentJudgeScoreLogDao;

    private final SpelEngine spelEngine;

    private final ScoreSettingsProperties scoreSettingsProperties;
    private BigDecimal cfgMinScore(){
        BigDecimal dft=BigDecimal.ZERO;
        return Optional.ofNullable(scoreSettingsProperties)
                .map(i->BigDecimalUtil.tryParseDecimal(i.getJudgeScoreMin(),dft))
                .orElse(dft);
    }
    private BigDecimal cfgMaxScore(){
        BigDecimal dft=BigDecimalUtil.ONEHundred;
        return Optional.ofNullable(scoreSettingsProperties)
                .map(i->BigDecimalUtil.tryParseDecimal(i.getJudgeScoreMax(),dft))
                .orElse(dft);
    }

    private final int SCALEScore=2;

    private final RoundingMode ROUNDINGModeScore=RoundingMode.HALF_UP;

    private final EvalPersonCache evalPersonCache;

    //region 保存判断操作得分
    public boolean saveJudgeScore4Func(ExptRequestValidator validator, String operateFlowId, ExperimentTimePoint timePoint,
                                  Map<String,BigDecimal> judgeItems,EnumIndicatorExpressionSource expressionSource){
        if(ShareUtil.XObject.anyEmpty(judgeItems,timePoint)){
            return true;
        }
        Map<String, BigDecimal[]> mapJudgeScores=evalJudgeScore4Func(validator.getExperimentInstanceId(),validator.getExperimentPersonId(),
                judgeItems,expressionSource);
        return saveJudgeScore4Func(validator,operateFlowId,timePoint,mapJudgeScores);

    }

    public boolean saveJudgeScore4Func(ExptRequestValidator validator, String operateFlowId, ExperimentTimePoint timePoint,
                                       Map<String,BigDecimal[]> mapJudgeScores){
        if(ShareUtil.XObject.anyEmpty(mapJudgeScores,timePoint)){
            return true;
        }
        validator.checkExperimentPerson()
                .checkIndicatorFuncId();
        BigDecimalOptional totalScore=BigDecimalOptional.zero();
        BigDecimalOptional totalSingleScore=BigDecimalOptional.zero();
        for(BigDecimal[] item:mapJudgeScores.values()){
            totalScore.add(item[0]);
            totalSingleScore.add(item[1]);
        }
        totalSingleScore.div(BigDecimal.valueOf(mapJudgeScores.size()),SCALEScore);

        ExperimentJudgeScoreLogEntity rowScoreLog=new ExperimentJudgeScoreLogEntity()
                .setAppId(validator.getAppId())
                .setExperimentInstanceId(validator.getExperimentInstanceId())
                .setExperimentGroupId(validator.getExperimentGroupId())
                .setExperimentPersonId(validator.getExperimentPersonId())
                .setExperimentOrgId(validator.getExperimentOrgId())
                .setIndicatorFuncId(validator.getIndicatorFuncId())
                .setPeriod(timePoint.getPeriod())
                .setOperateFlowId(operateFlowId)
                .setOperateTime(new Date())
                .setOperateGameDay(Optional.ofNullable(timePoint.getGameDay()).orElse(null))
                .setScore(totalScore.getValue(SCALEScore))
                .setSingleScore(totalSingleScore.getValue(SCALEScore))
                .setScoreJson(Optional.ofNullable( JacksonUtil.toJsonSilence(mapJudgeScores,true))
                        .map(i->i.substring(0,Math.min(4000,i.length()))).orElse(""));
        return experimentJudgeScoreLogDao.saveOrUpdate(rowScoreLog);

    }

    public Map<String,  BigDecimal[]> evalJudgeScore4Func(String experimentId, String experimentPersonId, Map<String,BigDecimal> judgeItems, EnumIndicatorExpressionSource expressionSource) {
        if (ShareUtil.XObject.isEmpty(judgeItems)) {
            return Collections.emptyMap();
        }
        return evalJudgeScore4Func(getJudgeScoreContext(experimentPersonId), experimentId, experimentPersonId, judgeItems, expressionSource);
    }
    private SpelPersonContext getJudgeScoreContext(String experimentPersonId){
        return new SpelPersonContext().setVariables(experimentPersonId, null, true);
    }
    public Map<String,  BigDecimal[]> evalJudgeScore4Func(SpelPersonContext context, String experimentId, String experimentPersonId, Map<String,BigDecimal> judgeItems, EnumIndicatorExpressionSource expressionSource) {
        if (ShareUtil.XObject.isEmpty(judgeItems)) {
            return Collections.emptyMap();
        }
        Map<String, BigDecimal[]> rst = new HashMap<>();
        List<SpelInput> inputs = spelEngine.loadFromSpelCache()
                .withReasonId(experimentId, experimentPersonId, judgeItems.keySet(), expressionSource.getSource(), EnumIndicatorExpressionSource.INDICATOR_JUDGE_CHECKRULE.getSource())
                .getInput();
        inputs.forEach(input -> {
            final String judgeItemId = input.getReasonId();
            context.setVariable(EnumString.INPUT_GOAL.getStr(), judgeItems.getOrDefault(judgeItemId, BigDecimal.ONE));
            SpelEvalResult evalRst = spelEngine.loadWith(input).eval(context);
            if (ShareUtil.XObject.anyEmpty(evalRst, () -> evalRst.getValNumber())) {
                return;
            }
            BigDecimal socre = evalRst.getValNumber();
            BigDecimal maxScore = BigDecimalUtil.ONEHundred;
            if (ShareUtil.XObject.notEmpty(input.getMax()) && input.getMax().compareTo(maxScore) < 0) {
                maxScore = input.getMax();
            }
            BigDecimal singleScore = BigDecimalOptional.valueOf(socre)
                    .mul(BigDecimalUtil.ONEHundred)
                    .div(maxScore, SCALEScore)
                    .max(BigDecimalUtil.ONEHundred)
                    .getValue();
            rst.put(judgeItemId, new BigDecimal[]{socre,singleScore,maxScore});
        });
        return rst;
    }
    //endregion

    //region 期数翻转小组得分
    public Map<String,BigDecimalOptional> evalJudgeScore4Period(String experimentId,Integer period) {
        Map<String, BigDecimalOptional> rst = new HashMap<>();
        ExperimentPersonCache.Instance().getGroups(experimentId).forEach(i->rst.put(i.getExperimentGroupId(),BigDecimalOptional.valueOf(cfgMinScore())));
        List<ExperimentJudgeScoreLogEntity> rowsScoreLog = experimentJudgeScoreLogDao.getAllByPeriod(experimentId, period,
                ExperimentJudgeScoreLogEntity::getExperimentGroupId,
                ExperimentJudgeScoreLogEntity::getExperimentOrgId,
                ExperimentJudgeScoreLogEntity::getExperimentPersonId,
                ExperimentJudgeScoreLogEntity::getIndicatorFuncId,
                ExperimentJudgeScoreLogEntity::getScore,
                ExperimentJudgeScoreLogEntity::getSingleScore
        );

        if(rst.size()==1){
            return evalJudgeScore4PeriodSingle(experimentId,rowsScoreLog);
        }
        Map<String, Map<String, List<BigDecimal>>> mapGroupScore = new HashMap<>();
        rowsScoreLog.forEach(i -> {
            mapGroupScore.computeIfAbsent(i.getExperimentGroupId(), k -> new HashMap<>())
                    .computeIfAbsent(String.format("%s-%s-%s-%s",i.getExperimentGroupId(), i.getExperimentPersonId(), i.getExperimentOrgId(), i.getIndicatorFuncId()), k -> new ArrayList<>())
                    .add(ShareUtil.XObject.defaultIfNull(i.getScore(), BigDecimal.ZERO));
        });
        rst.keySet().forEach(groupId->mapGroupScore.computeIfAbsent(groupId, k->new HashMap<>()));

        mapGroupScore.forEach((groupId, funcSocres) -> {
            autoFillScores(funcSocres, experimentId, groupId, false);
            BigDecimalOptional total = BigDecimalOptional.zero();
            funcSocres.values().forEach(scores -> total.add(getAvg(scores)));
            rst.put(groupId, total);
        });
        BigDecimal minScore=null;
        BigDecimal maxScore=null;
        for(BigDecimalOptional optScore:rst.values()){
            if(null==minScore||optScore.getValue().compareTo(minScore)<0){
                minScore=optScore.getValue();
            }
            if(null==maxScore||optScore.getValue().compareTo(maxScore)>0){
                maxScore=optScore.getValue();
            }
        }
        for(BigDecimalOptional optScore:rst.values()){
            optScore.setValue(calcScore(optScore.getValue(),minScore,maxScore));
        }
        return rst;
    }

    public Map<String,BigDecimalOptional> evalJudgeScore4PeriodSingle(String experimentId, List<ExperimentJudgeScoreLogEntity> rowsScoreLog){
        Map<String,List<BigDecimal>>[] dst=new Map[2];
        dst[0]=new HashMap<>();
        dst[1]=new HashMap<>();
        int pos=0;
        for(ExperimentJudgeScoreLogEntity item:rowsScoreLog) {
            dst[pos].computeIfAbsent(String.format("%s-%s-%s-%s", item.getExperimentGroupId(), item.getExperimentPersonId(), item.getExperimentOrgId(), item.getIndicatorFuncId()),
                            k -> new ArrayList<>())
                    .add(BigDecimalOptional.valueOf(item.getSingleScore())
                            .min(BigDecimal.ZERO)
                            .getValue(SCALEScore));
        }
        autoFillScores(dst[pos],experimentId,null,true);
        int loopNum=3;
        while (loopNum-->0){
            Map<String,List<BigDecimal>> vDst=dst[1-pos];
            vDst.clear();
            for(Map.Entry<String,List<BigDecimal>> item:dst[pos].entrySet()){
                vDst.computeIfAbsent(item.getKey().substring(0,item.getKey().lastIndexOf("-")),x->new ArrayList<>())
                        .add(getAvg(item.getValue()));
            }
            pos=1-pos;
        }
        Map<String,BigDecimalOptional> rst=new HashMap<>();
        dst[pos].forEach((k,v)->rst.put(k, BigDecimalOptional.valueOf( getAvg(v))));
        dst[0].clear();
        dst[1].clear();
        return rst;
    }


    private void autoFillScores(Map<String,List<BigDecimal>> mapGroupScores,String experimentId,String experimentGroupId, boolean singleFlag) {
        Collection<ExperimentPersonEntity> persons = ShareUtil.XObject.notEmpty(experimentGroupId)
                ? ExperimentPersonCache.Instance().getPersonsByGroupId(experimentId, experimentGroupId)
                : ExperimentPersonCache.Instance().getMapPersons(experimentId).values();
        final Map<String, Map<String, BigDecimal>> mapTodo = new HashMap<>();
        persons.forEach(person -> {
            mapTodo.clear();
            evalPersonCache.getCurHolder(experimentId, person.getExperimentPersonId()).getJudgeItems()
                    .forEach((key, items) -> {
                        if (mapGroupScores.containsKey(key)) {
                            return;
                        }
                        mapTodo.put(key, items);
                    });
            if (mapTodo.size() == 0) {
                return;
            }
            SpelPersonContext context = getJudgeScoreContext(person.getExperimentPersonId());
            mapTodo.forEach((k, v) -> {
                Map<String, BigDecimal[]> mapJudgeScores = evalJudgeScore4Func(context, experimentId, person.getExperimentPersonId(),
                        v, EnumIndicatorExpressionSource.INDICATOR_JUDGE_CHECKRULE);
                BigDecimalOptional totalScore = BigDecimalOptional.zero();
                for (BigDecimal[] item : mapJudgeScores.values()) {
                    if (singleFlag) {
                        totalScore.add(item[1]);
                    } else {
                        totalScore.add(item[0]);
                    }
                }
                if (singleFlag&&mapJudgeScores.size()>0) {
                    totalScore.div(BigDecimal.valueOf(mapJudgeScores.size()), SCALEScore)
                            .min(BigDecimal.ZERO);
                }
                mapGroupScores.computeIfAbsent(k, x -> new ArrayList<>())
                        .add(totalScore.getValue(SCALEScore));
            });
        });
    }


    private BigDecimal getAvg(List<BigDecimal> scores){
        if(ShareUtil.XObject.isEmpty(scores)){
            return BigDecimal.ZERO;
        }
        if(scores.size()==1){
            return Optional.ofNullable( scores.get(0))
                    .orElse(BigDecimal.ZERO)
                    .setScale(SCALEScore, ROUNDINGModeScore);
        }
        return scores.stream().reduce(BigDecimal.ZERO,BigDecimalUtil::add)
                .divide(BigDecimalUtil.valueOf(scores.size()),SCALEScore,ROUNDINGModeScore);
    }
    private BigDecimal calcScore(BigDecimal curSocre, BigDecimal minScore, BigDecimal maxScore){
        if(null==minScore){
            minScore=curSocre;
        }
        if(null==maxScore){
            maxScore=curSocre;
        }
        if(minScore.compareTo(maxScore)==0){
            return cfgMinScore();
        }
        if(maxScore.compareTo(curSocre)<=0){
            return cfgMaxScore();
        }
        if(curSocre.compareTo(minScore)<=0){
            return cfgMinScore();
        }
        return BigDecimalOptional.valueOf(curSocre)
                .sub(minScore)
                .mul(cfgMaxScore().subtract(cfgMinScore()))
                .div(maxScore.subtract(minScore), SCALEScore)
                .add(cfgMinScore())
                .getValue(SCALEScore);

    }
    //endregion
}
