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

    private BigDecimal MINJudgeScore =BigDecimal.ZERO;
    private BigDecimal MAXJudgeScore =BigDecimal.valueOf(100L);

    private final int SCALEScore=2;

    private final RoundingMode ROUNDINGModeScore=RoundingMode.HALF_UP;

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
        Map<String, BigDecimal[]> rst = new HashMap<>();
        final SpelPersonContext context = new SpelPersonContext().setVariables(experimentPersonId, null, true);
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
                    .getValue();
            rst.put(judgeItemId, new BigDecimal[]{socre,singleScore,maxScore});
        });
        return rst;
    }
    //endregion

    //region 期数翻转小组得分
    public Map<String,BigDecimalOptional> evalJudgeScore4Period(String experimentId,Integer period) {
        Map<String, BigDecimalOptional> rst = new HashMap<>();
        ExperimentPersonCache.Instance().getGroups(experimentId).forEach(i->rst.put(i.getExperimentGroupId(),BigDecimalOptional.valueOf( MINJudgeScore)));
        List<ExperimentJudgeScoreLogEntity> rowsScoreLog = experimentJudgeScoreLogDao.getAllByPeriod(experimentId, period,
                ExperimentJudgeScoreLogEntity::getExperimentGroupId,
                ExperimentJudgeScoreLogEntity::getExperimentOrgId,
                ExperimentJudgeScoreLogEntity::getExperimentPersonId,
                ExperimentJudgeScoreLogEntity::getIndicatorFuncId,
                ExperimentJudgeScoreLogEntity::getScore
        );
        if(ShareUtil.XObject.isEmpty(rowsScoreLog)){
            return rst;
        }
        if(rst.size()==1){
            return evalJudgeScore4PeriodSingle(rowsScoreLog);
        }
        Map<String, Map<String, List<BigDecimal>>> mapGroupScore = new HashMap<>();
        rowsScoreLog.forEach(i -> {
            mapGroupScore.computeIfAbsent(i.getExperimentGroupId(), k -> new HashMap<>())
                    .computeIfAbsent(String.format("%s-%s-%s", i.getExperimentPersonId(), i.getExperimentOrgId(), i.getIndicatorFuncId()), k -> new ArrayList<>())
                    .add(ShareUtil.XObject.defaultIfNull(i.getScore(), BigDecimal.ZERO));
        });
        mapGroupScore.forEach((groupId, funcSocres) -> {
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

    public Map<String,BigDecimalOptional> evalJudgeScore4PeriodSingle(List<ExperimentJudgeScoreLogEntity> rowsScoreLog){
        Map<String,List<BigDecimal>>[] dst=new Map[2];
        dst[0]=new HashMap<>();
        dst[1]=new HashMap<>();
        int pos=0;
        for(ExperimentJudgeScoreLogEntity item:rowsScoreLog){
            dst[pos].computeIfAbsent(String.format("%s-%s-%s-%s", item.getExperimentGroupId(), item.getExperimentPersonId(), item.getExperimentOrgId(), item.getIndicatorFuncId()),
                            k -> new ArrayList<>())
                    .add(ShareUtil.XObject.defaultIfNull(item.getSingleScore(), BigDecimal.ZERO));
        }
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


    private BigDecimal getAvg(List<BigDecimal> scores){
        if(ShareUtil.XObject.isEmpty(scores)){
            return BigDecimal.ZERO;
        }
        if(scores.size()==1){
            return scores.get(0).setScale(SCALEScore, ROUNDINGModeScore);
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
            return MAXJudgeScore;
        }
        if(maxScore.compareTo(curSocre)<=0){
            return MAXJudgeScore;
        }
        if(curSocre.compareTo(minScore)<=0){
            return MINJudgeScore;
        }
        return BigDecimalOptional.valueOf(curSocre)
                .sub(minScore)
                .mul(MAXJudgeScore.subtract(MINJudgeScore))
                .div(maxScore.subtract(minScore), SCALEScore)
                .add(MINJudgeScore)
                .getValue(SCALEScore);

    }
    //endregion
}
