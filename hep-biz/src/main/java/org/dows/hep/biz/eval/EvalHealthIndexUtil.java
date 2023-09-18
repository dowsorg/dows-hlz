package org.dows.hep.biz.eval;

import org.dows.hep.biz.calc.RiskFactorScoreVO;
import org.dows.hep.biz.calc.RiskModelHealthIndexVO;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author : wuzl
 * @date : 2023/8/18 11:01
 */
public class EvalHealthIndexUtil {
    final static BigDecimal EMPTYScore=BigDecimal.ZERO;
    final static BigDecimal MINScore=BigDecimal.ZERO;
    final static BigDecimal MAXScore =BigDecimal.valueOf(100);

    final static BigDecimal MINHeathIndex=BigDecimal.ONE;
    final static BigDecimal MAXHealthIndex=BigDecimal.valueOf(100);

    final static int SCALE =2;
    final static RoundingMode ROUNDINGMode =RoundingMode.HALF_UP;

    /**
     * 计算人物健康指数
     * @param src
     * @param calcItemFlag
     * @return
     */
    public static BigDecimal evalHealthIndex(List<RiskModelHealthIndexVO> src, boolean calcItemFlag){
        if(ShareUtil.XObject.isEmpty(src)){
            return MINHeathIndex;
        }
        BigDecimalOptional healthIndex=BigDecimalOptional.zero();
        BigDecimalOptional totalDealthRate=BigDecimalOptional.create();
        Set<String> crowds=new HashSet<>();
        src.forEach(i->{
            if(ShareUtil.XObject.isEmpty(i.getCrowdsDeathRate(), true)
                    ||crowds.contains(i.getCrowdsId())){
                return;
            }
            totalDealthRate.add(BigDecimalUtil.valueOf(i.getCrowdsDeathRate()));
            crowds.add(i.getCrowdsId());
        });
        if(ShareUtil.XObject.isEmpty(crowds)){
            return MINHeathIndex;
        }

        for(RiskModelHealthIndexVO item:src){
            if(calcItemFlag){
                evalRiskModelHealthIndex(item);
            }
            healthIndex.add(BigDecimalUtil.mul(item.getHealthIndex(),item.getDeathRateWeight(totalDealthRate.getValue())));
        }
        return healthIndex.min(MINHeathIndex)
                .max(MAXHealthIndex)
                .getValue(SCALE,ROUNDINGMode);

    }

    /**
     * 计算死亡原因-健康指数
     * @param src
     * @return
     */
    public static RiskModelHealthIndexVO evalRiskModelHealthIndex(RiskModelHealthIndexVO src){
        final List<RiskFactorScoreVO> scores=src.getRiskFactors();
        if(ShareUtil.XObject.isEmpty(scores)){
            return src.setHealthIndex(BigDecimal.ZERO);
        }
        BigDecimal score= evalRiskModelScore(scores);
        BigDecimal minScore= evalRiskModelScore(scores, RiskFactorScoreVO::getMinScore);
        BigDecimal maxScore= evalRiskModelScore(scores, RiskFactorScoreVO::getMaxScore);
        src.setScore(score).setMinScore(minScore).setMaxScore(maxScore);
        if(null==minScore||score.compareTo(minScore)<0){
            minScore=score;
        }
        if(null==maxScore||maxScore.compareTo(score)<0){
            maxScore=score;
        }
        if(score.compareTo(minScore)==0||minScore.compareTo(maxScore)==0){
            return src.setHealthIndex(MAXHealthIndex);
        }
        if(score.compareTo(maxScore)==0){
            return src.setHealthIndex(BigDecimal.ZERO);
        }
        return src.setHealthIndex(BigDecimalOptional.valueOf(score.subtract(maxScore)).mul(MAXScore)
                .div(minScore.subtract(maxScore), 2, ROUNDINGMode)
                .getValue());
    }

    /**
     * 计算死亡原因-组合危险分数
     * @param src
     * @return
     */
    public static BigDecimal evalRiskModelScore(List<RiskFactorScoreVO> src){
        return evalRiskModelScore(src, RiskFactorScoreVO::getScore);
    }
    public static BigDecimal evalRiskModelScore(List<RiskFactorScoreVO> src, Function<RiskFactorScoreVO,BigDecimal> scoreFunc) {
        if (ShareUtil.XObject.isEmpty(src)) {
            return EMPTYScore;
        }
        BigDecimalOptional total = BigDecimalOptional.zero();
        BigDecimalOptional multi = BigDecimalOptional.create();
        for (RiskFactorScoreVO item : src) {
            BigDecimal score = scoreFunc.apply(item);
            if (null == score) {
                continue;
            }
            if (score.compareTo(BigDecimal.ONE) > 0) {
                total.add(score.subtract(BigDecimal.ONE));
            } else {
                if (multi.isEmpty()) {
                    multi.setValue(score);
                } else {
                    multi.mul(score);
                }
            }
        }
        return total.add(multi.getValue()).getValue(SCALE, ROUNDINGMode);
    }
}
