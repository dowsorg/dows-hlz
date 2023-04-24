package org.dows.hep.entity;

import java.util.Date;

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

/**
 * 实验操作流程(OperateFlow)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:53
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OperateFlow", title = "实验操作流程")
@TableName("operate_flow")
public class OperateFlowEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验操作流程id")
    private String operateFlowId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例账号ID")
    private String caseAccountId;

    @Schema(title = "账号名称")
    private String caseAccountName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "流程名称")
    private String flowName;

    @Schema(title = "流程顺序")
    private String flowSequence;

    @Schema(title = "流程类型 1-体检挂号 2-医院挂号")
    private Boolean flowType;

    @Schema(title = "展示类型 0-不展示 1-用户端展示")
    private Boolean reportFlag;

    @Schema(title = "消耗资金")
    private Double feeCost;

    @Schema(title = "剩余资金")
    private Double feeRemain;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;

    @Schema(title = "操作时间")
    private Date operateTime;

    @Schema(title = "操作描述")
    private String operateDescr;

    @Schema(title = "标签")
    private String label;

    @Schema(title = "流程完结状态 0-未完结 1-已完结")
    private Boolean endState;

    @Schema(title = "状态")
    private Integer state;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

