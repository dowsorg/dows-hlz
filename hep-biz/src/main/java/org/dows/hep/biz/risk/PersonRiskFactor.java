package org.dows.hep.biz.risk;

import lombok.Data;

import java.util.List;

/**
 * 人物风险因素
 */
@Data
public class PersonRiskFactor {

    private String personId;
    private String personName;
    // 因素
    private RiskFactor riskFactor;


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

    @Data
    public static class RiskItem{
        private String itemName;
        private String itemValue;
        private String riskScore;
    }
}
