package org.dows.hep.api.enums;

import lombok.Getter;

/**
 * 实验计分BIZ
 *  todo
 *  post/healthIndexScoring/健康指数分数计算
 *  post/knowledgeScoring/知识考点分数得分
 *  post/treatmentPercentScoring/医疗占比得分
 *  post/operateRightScoring/操作准确度得分
 *  post//竞争性得分
 *  post/totalScoring/总分
 *  此处先计算分数，独立的功能点，供其他bean调用，并将生成的数据保存，用于排行和出报告
 */
public enum EnumCalcCode {

    hepHealthIndexCalculator(0, "健康指数计算"),
    hepKnowledgeCalculator(1, "知识考点计算"),
    hepTreatmentPercentCalculator(2, "医疗占比计算"),
    hepOperateRightCalculator(3, "操作准确度计算"),
    hepTotalScoreCalculator(4, "总分计算"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String descr;

    EnumCalcCode(int code, String descr) {
        this.code = code;
        this.descr = descr;
    }
}
