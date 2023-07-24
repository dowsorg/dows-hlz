package org.dows.hep.biz.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CostRequest {

    @Schema(title = "实验ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID[uim-groupId]")
    private String experimentGroupId;

    @Schema(title = "操作者ID[uim-accountId]")
    private String operatorId;

    @Schema(title = "机构ID")
    private String caseOrgId;

    @Schema(title = "挂号流水")
    private String operateFlowId;

    @Schema(title = "患者ID|NPC人物ID[uim-accountId]")
    private String patientId;

    @Schema(title = "费用名称")
    private String feeName;

    @Schema(title = "费用code")
    private String feeCode;

    @Schema(title = "花费")
    private BigDecimal cost;

    @Schema(title = "返回|报销比例或金额")
    private BigDecimal restitution;

    @Schema(title = "期数")
    private Integer period;
}
