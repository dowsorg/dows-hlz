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

import java.math.BigDecimal;
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
@Schema(name = "ExperimentIndicatorViewSupportExamReportRsEntity", title = "查看指标辅助2检查类")
@TableName("experiment_indicator_view_support_exam_report_rs")
public class ExperimentIndicatorViewSupportExamReportRsEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String experimentIndicatorViewSupportExamReportId;

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

  @Schema(title = "挂号流水号")
  private String operateFlowId;

  @Schema(title = "体格检查名称")
  private String name;

  @Schema(title = "体格检查费用")
  private BigDecimal fee;

  @Schema(title = "当前人物指标值")
  private String currentVal;

  @Schema(title = "指标单位")
  private String unit;

  @Schema(title = "结果解读（这里是指标的结果值，并非体格检查配置的）")
  private String resultExplain;

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
