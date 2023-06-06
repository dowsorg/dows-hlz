package org.dows.hep.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Schema(name = "ExperimentIndicatorExpressionItemEntity", title = "指标公式")
@TableName("experiment_indicator_expression_item")
public class ExperimentIndicatorExpressionItemEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "实验指标表达式分布式ID")
  private String experimentIndicatorExpressionId;

  @Schema(title = "案例指标表达式分布式ID")
  private String caseIndicatorExpressionId;

  @Schema(title = "实验实例ID")
  private String experimentInstanceId;

  @Schema(title = "原始条件")
  private String conditionRaw;

  @Schema(title = "条件")
  private String conditionExpression;

  @Schema(title = "条件参数名字，以英文逗号分割")
  private String conditionNameList;

  @Schema(title = "条件参数数值，以英文逗号分割")
  private String conditionValList;

  @Schema(title = "原始结果")
  private String resultRaw;

  @Schema(title = "结果")
  private String resultExpression;

  @Schema(title = "结果参数名字，以英文逗号分割")
  private String resultNameList;

  @Schema(title = "结果参数数值，以英文逗号分割")
  private String resultValList;

  @Schema(title = "优先判断顺序")
  private Integer seq;
}
