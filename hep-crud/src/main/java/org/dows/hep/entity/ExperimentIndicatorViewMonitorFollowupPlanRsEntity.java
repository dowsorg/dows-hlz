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

import java.util.Date;

/**
 * (ExperimentIndicatorViewMonitorFollowupPlanRs)实体类
 *
 * @author lait
 * @since 2023-07-24 14:59:17
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentIndicatorViewMonitorFollowupPlanRs", title = "")
@TableName("experiment_indicator_view_monitor_followup_plan_rs")
public class ExperimentIndicatorViewMonitorFollowupPlanRsEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "id")
    private Long id;

    @Schema(title = "随访计划ID")
    private String experimentIndicatorViewMonitorFollowupPlanId;

    @Schema(title = "实验ID")
    private String experimentId;

    @Schema(title = "监测随访ID")
    private String experimentIndicatorViewMonitorFollowupId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标功能ID")
    private String indicatorFuncId;

    @Schema(title = "人物ID")
    private String experimentPersonId;

    @Schema(title = "操作流水ID")
    private String operateFlowId;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "间隔天数")
    private Integer intervalDay;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "")
    private Date dt;
}

