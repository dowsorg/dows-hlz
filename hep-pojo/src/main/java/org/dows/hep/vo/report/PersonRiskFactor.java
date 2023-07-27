package org.dows.hep.vo.report;

import lombok.Data;

import java.util.List;

/**
 * 人物风险因素
 */
@Data
public class PersonRiskFactor {
    // npc 人物id
    private String personId;
    // 人物名称
    private String personName;
    // 期数
    private Integer period;
    // 人物因素
    private List<RiskFactor> riskFactors;


    /**
     * 风险因素
     */
    @Data
    public static class RiskFactor{
        // 风险名称
        private String riskName;
        // 风险死亡概率
        private Integer riskDeathProbability;

        //组合危险分数
        private String riskScore;
        // 存在死亡危险
        private String deathRiskScore;
        // 风险item
        private List<RiskItem> riskItems;
    }

    /**
     * 风险item
     */
    @Data
    public static class RiskItem{
        private String itemName;
        private String itemValue;
        private String riskScore;
    }
}
