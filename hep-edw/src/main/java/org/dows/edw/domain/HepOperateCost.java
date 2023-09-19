package org.dows.edw.domain;

import lombok.Data;
import org.dows.edw.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * hep操作花费
 * @TableName hep_operate_cost
 */
@Data
@MongoEntity
public class HepOperateCost implements Serializable, HepOperateEntity {
    /**
     * 操作花费ID
     */
    @FieldFill
    @MongoEntityId
    private Long hepOperateCostId;

    /**
     * 实验ID
     */
    private Long experimentInstanceId;

    /**
     * 实验小组ID
     */
    private Long experimentGroupId;

    /**
     * 操作者ID[小组成员]
     */
    private Long operatorId;

    /**
     * 实验|人物|患者ID|NPC人物ID[uim-accountId]
     */
    private Long personId;

    /**
     * 机构树ID
     */
    private Long orgTreeId;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 职责|能力|功能|菜单名称
     */
    private String functionName;

    /**
     * 流程Id[挂号ID|就诊ID]
     */
    private String flowId;

    /**
     * 费用类型
     */
    private String costType;

    /**
     * 费用名称
     */
    private String feeName;

    /**
     * 费用code
     */
    private String feeCode;

    /**
     * 花费
     */
    private String cost;

    /**
     * 期数
     */
    private Integer period;

    /**
     * 逻辑删除
     */
    @FieldFill
    @LogicDel
    private Integer deleted;

    /**
     * 操作时间(仿真时间)
     */
    private LocalDateTime atDate;

    /**
     * 时间戳
     */
    @FieldFill
    private LocalDateTime dt;

    private static final long serialVersionUID = 1L;

    @Override
    public String getData() {
        return null;
    }
}