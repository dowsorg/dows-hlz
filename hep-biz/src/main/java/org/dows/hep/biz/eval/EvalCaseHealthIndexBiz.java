package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.CaseRsCalculateHealthScoreRequestRs;
import org.dows.hep.api.enums.EnumStatus;
import org.dows.hep.biz.calc.RiskFactorScoreVO;
import org.dows.hep.biz.calc.RiskModelHealthIndexVO;
import org.dows.hep.biz.dao.CaseIndicatorRuleDao;
import org.dows.hep.biz.dao.CrowdsInstanceDao;
import org.dows.hep.biz.dao.RiskModelDao;
import org.dows.hep.biz.spel.SpelCasePersonContext;
import org.dows.hep.biz.spel.SpelEngine;
import org.dows.hep.biz.spel.loaders.FromDatabaseLoader;
import org.dows.hep.biz.spel.meta.SpelEvalResult;
import org.dows.hep.biz.spel.meta.SpelInput;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CrowdsInstanceEntity;
import org.dows.hep.entity.RiskModelEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 计算案例人物健康指数
 *
 * @author : wuzl
 * @date : 2023/10/16 17:59
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class EvalCaseHealthIndexBiz {

    private final CrowdsInstanceDao crowdsInstanceDao;

    private final RiskModelDao riskModelDao;

    private final SpelEngine spelEngine;

    private final FromDatabaseLoader fromDatabaseLoader;

    private final CaseIndicatorRuleDao caseIndicatorRuleDao;
    public void evalCasePersonHealthIndex(CaseRsCalculateHealthScoreRequestRs req) {
        SpelCasePersonContext context = new SpelCasePersonContext().setVariables(req.getAccountId(), true);
        final String indicatorIdHealthIndex=context.getIndicatorId4HealthIndex();
        if(ShareUtil.XObject.isEmpty(indicatorIdHealthIndex)){
            return;
        }

        final Map<String, CrowdsInstanceEntity> mapCrowds = ShareUtil.XCollection.toMap(crowdsInstanceDao.getAll(req.getAppId(), true,
                CrowdsInstanceEntity::getCrowdsId,
                CrowdsInstanceEntity::getName,
                CrowdsInstanceEntity::getDeathProbability), CrowdsInstanceEntity::getCrowdsId);
        final Map<String, RiskModelEntity> mapRisks = new HashMap<>();
        final Map<String, List<RiskModelEntity>> mapCrowdXRiskModels = new HashMap<>();
        List<RiskModelEntity> rowsRisk = riskModelDao.getAll(req.getAppId(), EnumStatus.ENABLE.getCode(),
                RiskModelEntity::getRiskModelId,
                RiskModelEntity::getName,
                RiskModelEntity::getRiskDeathProbability,
                RiskModelEntity::getCrowdsCategoryId,
                RiskModelEntity::getStatus);
        rowsRisk.forEach(i -> {
            mapRisks.put(i.getRiskModelId(), i);
            mapCrowdXRiskModels.computeIfAbsent(i.getCrowdsCategoryId(), k -> new ArrayList<>()).add(i);
        });
        final List<RiskModelHealthIndexVO> vosHealthIndex = new ArrayList<>();
        //final List<EvalRiskValues> voRisks = new ArrayList<>();


        final Set<String> reasonIds=new HashSet<>(mapCrowds.keySet());
        reasonIds.addAll(mapRisks.keySet());
        Map<String,List<SpelInput>> mapInputs= fromDatabaseLoader.withReasonId(reasonIds);
        mapCrowds.values().forEach(hitCrowd -> {
            final String crowdId = hitCrowd.getCrowdsId();
            List<SpelInput> crowdInputs=mapInputs.get(crowdId);
            if(ShareUtil.XObject.isEmpty(crowdInputs)){
                return;
            }
            if (!spelEngine.loadWith(crowdInputs.get(0)).check(context)) {
                return;
            }
            List<RiskModelEntity> hitRiskModels = mapCrowdXRiskModels.get(crowdId);
            if (ShareUtil.XObject.isEmpty(hitRiskModels)) {
                return;
            }
            hitRiskModels.forEach(hitRisk -> {
                List<SpelInput> riskInputs = mapInputs.get(hitRisk.getRiskModelId());
                if (ShareUtil.XObject.isEmpty(riskInputs)) {
                    return;
                }
                final List<RiskFactorScoreVO> vosFactorScore = new ArrayList<>();
                riskInputs.forEach(input -> {
                    SpelEvalResult evalRst = spelEngine.loadWith(input).eval(context);
                    if (ShareUtil.XObject.anyEmpty(evalRst, () -> evalRst.getValNumber())) {
                        return;
                    }
                    final BigDecimal riskScore = evalRst.getValNumber();
                    vosFactorScore.add(new RiskFactorScoreVO(input.getIndicatorId(), input.getExpressionId(), riskScore,
                            input.getMin(), input.getMax())
                            .setRiskFactorName(""));
                });
                final RiskModelHealthIndexVO voRiskModel = new RiskModelHealthIndexVO()
                        .setCrowdsId(hitCrowd.getCrowdsId())
                        .setRiskModelId(hitRisk.getRiskModelId())
                        .setRiskModelName(hitRisk.getName())
                        .setCrowdsDeathRate(hitCrowd.getDeathProbability())
                        .setRiskModelDeathRate(hitRisk.getRiskDeathProbability())
                        .setRiskFactors(vosFactorScore);
                vosHealthIndex.add(EvalHealthIndexUtil.evalRiskModelHealthIndex(voRiskModel));
                /*voRisks.add(new EvalRiskValues()
                        .setCrowdId(crowdId)
                        .setRiskId(hitRisk.getRiskModelId())
                        .setRiskName(hitRisk.getName())
                );*/

            });
        });

        BigDecimal personHealthIndex = EvalHealthIndexUtil.evalHealthIndex(vosHealthIndex, false);
        String healthIndexStr=BigDecimalUtil.formatRoundDecimal(personHealthIndex, 2);

        caseIndicatorRuleDao.updateIndicatorDef(indicatorIdHealthIndex,healthIndexStr);
    }


}
