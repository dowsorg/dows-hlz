package org.dows.hep.biz.user.experiment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.annotation.CalcCode;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.biz.calc.Calculatable;
import org.dows.hep.service.ExperimentScoringService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 实验计分BIZ
 * todo
 * post/healthIndexScoring/健康指数分数计算
 * post/knowledgeScoring/知识考点分数得分
 * post/treatmentPercentScoring/医疗占比得分
 * post/operateRightScoring/操作准确度得分
 * post//竞争性得分
 * post/totalScoring/总分
 * 此处先计算分数，独立的功能点，供其他bean调用，并将生成的数据保存，用于排行和出报告
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentScoringBiz {


    private final ExperimentSettingBiz experimentSettingBiz;

    private final ExperimentScoringService experimentScoringService;

    private final Map<String, Calculatable> calculatableMap;


    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {
        List<EnumCalcCode> enumCalcCodes = experimentScoreCalcRequest.getEnumCalcCodes();
        if (enumCalcCodes == null || enumCalcCodes.size() == 0) {
            throw new ExperimentException("实验分数计算器集合为空");
        }
        for (EnumCalcCode enumCalcCode : enumCalcCodes) {
            Calculatable calculatable = calculatableMap.get(enumCalcCode);
            if(calculatable != null){
                calculatable.calc(experimentScoreCalcRequest);
            }
        }
    }

    /**
     * 健康指数分数计算，并存表
     */
    @CalcCode(code = EnumCalcCode.hepHealthIndexCalculator)
    public void hepHealthIndexScoring(String experimentInstanceId, String peroid) {
        //experimentSettingBiz.getCalcRule(experimentInstanceId);
        //


    }

    /**
     * 知识考点分数得分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepKnowledgeCalculator)
    public void hepKnowledgeScoring() {
        //experimentSettingBiz.getCalcRule(experimentInstanceId);
        //todo logic clac

    }

    /**
     * 医疗占比得分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepTreatmentPercentCalculator)
    public void hepTreatmentPercentScoring() {
        //experimentSettingBiz.getCalcRule(experimentInstanceId);
    }

    /**
     * 操作准确度得分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepOperateRightCalculator)
    public void hepOperateRightScoring() {
        //experimentSettingBiz.getCalcRule(experimentInstanceId);

    }

    /**
     * 总分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepTotalScoreCalculator)
    public void hepTotalScoring() {

    }


}
