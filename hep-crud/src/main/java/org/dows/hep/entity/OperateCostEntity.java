package org.dows.hep.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.framework.crud.api.CrudEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 操作花费(OperateCost)实体类
 *
 * @author lait
 * @since 2023-07-24 11:55:29
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OperateCost", title = "操作花费")
@TableName("operate_cost")
public class OperateCostEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "操作花费ID")
    private String operateCostId;

    @Schema(title = "实验ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID[uim-groupId]")
    private String experimentGroupId;

    @Schema(title = "操作者ID[uim-accountId]")
    private String operatorId;

    @Schema(title = "实验机构ID")
    private String experimentOrgId;

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

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

