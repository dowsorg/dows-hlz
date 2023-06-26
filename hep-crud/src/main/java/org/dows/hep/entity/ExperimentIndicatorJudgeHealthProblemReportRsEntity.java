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
@Schema(name = "ExperimentIndicatorJudgeHealthProblemReportRsEntity", title = "查看指标健康问题报告类")
@TableName("experiment_indicator_judge_health_problem_report_rs")
public class ExperimentIndicatorJudgeHealthProblemReportRsEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String experimentIndicatorJudgeHealthProblemReportId;

  @Schema(title = "实验id")
  private String experimentId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "期数")
  private Integer period;

  @Schema(title = "功能点id")
  private String indicatorFuncId;

  @Schema(title = "实验人物id")
  private String experimentPersonId;

  @Schema(title = "健康问题名称")
  private String name;

  @Schema(title = "目录名称列表")
  private String indicatorCategoryNameArray;



  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
