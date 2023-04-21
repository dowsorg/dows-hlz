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
 * 判断指标健管目标关联指标(IndicatorJudgeHealthManagementGoalRef)实体类
 *
 * @author lait
 * @since 2023-04-21 19:41:31
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "IndicatorJudgeHealthManagementGoalRef", title = "判断指标健管目标关联指标")
@TableName("indicator_judge_health_management_goal_ref")
public class IndicatorJudgeHealthManagementGoalRefEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "判断指标健管目标关联指标分布式ID")
    private String indicatorJudgeHealthManagementGoalRefId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "判断指标健管目标分布式ID")
    private String indicatorJudgeHealthManagementGoalId;

    @Schema(title = "指标实例分布式ID")
    private String indicatorInstanceId;

    @Schema(title = "判断规则")
    private String expression;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

