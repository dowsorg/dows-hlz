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
@Schema(name = "ExperimentIndicatorViewMonitorFollowupReportRsEntity", title = "查看指标体格检查类")
@TableName("experiment_indicator_view_monitor_followup_report_rs")
public class ExperimentIndicatorViewMonitorFollowupReportRsEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String experimentIndicatorViewMonitorFollowupReportId;

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

  @Schema(title = "次数")
  private Integer count;

  @Schema(title = "指标基本信息-监测随访表id")
  private String experimentIndicatorViewMonitorFollowupId;

  @Schema(title = "指标基本信息-监测随访表名称")
  private String name;

  @Schema(title = "监测随访内容名称")
  private String ivmfContentNameArray;

  @Schema(title = "监测随访随访内容指标id, #号分隔不同随访内容，','分隔单个随访内容")
  private String ivmfContentRefIndicatorInstanceIdArray;

  @Schema(title = "监测随访内容对应指标的值")
  private String ivmfIndicatorCurrentValArray;

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
