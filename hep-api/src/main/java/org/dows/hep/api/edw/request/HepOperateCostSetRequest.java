package org.dows.hep.api.edw.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author fhb
 * @version 1.0
 * @description TODO
 * @date 2023/9/12 15:18
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "HepOperateCostSetRequest 对象", title = "健康操作花费set请求")
public class HepOperateCostSetRequest {

    /**
     * 实验ID
     */
    @Schema(title = "实验ID")
    private Long experimentInstanceId;

    /**
     * 实验小组ID
     */
    @Schema(title = "实验小组ID")
    private Long experimentGroupId;

    /**
     * 操作者ID[小组成员]
     */
    @Schema(title = "操作者ID[小组成员]")
    private Long operatorId;

    /**
     * 实验|人物|患者ID|NPC人物ID[uim-accountId]
     */
    @Schema(title = "实验|人物|患者ID|NPC人物ID[uim-accountId]")
    private Long personId;

    /**
     * 机构树ID
     */
    @Schema(title = "机构树ID")
    private Long orgTreeId;

    /**
     * 机构名称
     */
    @Schema(title = "机构名称")
    private String orgName;

    /**
     * 职责|能力|功能|菜单名称
     */
    @Schema(title = "职责|能力|功能|菜单名称")
    private String functionName;

    /**
     * 流程Id[挂号ID|就诊ID]
     */
    @Schema(title = "流程Id[挂号ID|就诊ID]")
    private String flowId;

    /**
     * 费用类型
     */
    @Schema(title = "费用类型")
    private String costType;

    /**
     * 费用名称
     */
    @Schema(title = "费用名称")
    private String feeName;

    /**
     * 费用code
     */
    @Schema(title = "费用code")
    private String feeCode;

    /**
     * 花费
     */
    @Schema(title = "花费")
    private String cost;

    /**
     * 期数
     */
    @Schema(title = "期数")
    private Integer period;

    /**
     * 操作时间(仿真时间)
     */
    @Schema(title = "操作时间(仿真时间)")
    private LocalDateTime atDate;
}
