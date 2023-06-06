package org.dows.hep.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

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
@Schema(name = "ExperimentIndicatorInstanceEntity", title = "实验小组")
@TableName("experiment_indicator_instance")
public class ExperimentIndicatorInstanceEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

  @Schema(title = "实验指标ID")
  private String experimentIndicatorInstanceId;

  @Schema(title = "案例指标ID")
  private String caseIndicatorInstanceId;

  @Schema(title = "实验实例ID")
  private String experimentInstanceId;

  @Schema(title = "指标名称")
  private String indicatorName;

  @Schema(title = "是否按照百分比展示")
  private Integer displayByPercent;

  @Schema(title = "单位")
  private String unit;

  @Schema(title = "0-非关键指标，1-关键指标")
  private Integer core;

  @Schema(title = "0-非饮食关键指标，1-饮食关键指标")
  private Integer food;

  @Schema(title = "描述")
  private String descr;


  @Schema(title = "指标类别分布式ID")
  @ApiModelProperty(value = "指标类别分布式ID，如果存在以英文逗号分割")
  private String indicatorCategoryIdList;


  @Schema(title = "指标类别")
  @ApiModelProperty(value = "指标类别名称，如果存在以英文逗号分割")
  private String indicatorCategoryNameList;

  @Schema(title = "实验指标表达式分布式ID")
  private String experimentIndicatorExpressionId;
}
