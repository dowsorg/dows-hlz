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

import java.io.Serializable;
import java.util.Date;

/**
 * @author runsix
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentIndicatorViewMonitorFollowupPlanRsEntity", title = "查看指标体格检查类")
@TableName("experiment_indicator_view_monitor_followup_plan_rs")
public class ExperimentIndicatorViewMonitorFollowupPlanRsEntity implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String experimentIndicatorViewMonitorFollowupPlanId;

  @Schema(title = "实验id")
  private String experimentId;

  @Schema(title = "案例id")
  private String caseId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "期数")
  private Integer period;

  @Schema(title = "功能点id")
  private String indicatorFuncId;

  @Schema(title = "实验人物id")
  private String experimentPersonId;

  @Schema(title = "挂号流水号")
  private String operateFlowId;

  @Schema(title = "随访频率天数")
  private Integer intervalDay;

  @Schema(title = "监测随访表id列表")
  private String experimentIndicatorViewMonitorFollowupId;

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
