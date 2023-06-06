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
@Schema(name = "ExperimentIndicatorExpressionEntity", title = "实验指标值")
@TableName("experiment_indicator_expression")
public class ExperimentIndicatorExpressionEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "实验指标表达式分布式ID")
  private String experimentIndicatorExpressionId;

  @Schema(title = "案例指标表达式分布式ID")
  private String caseIndicatorExpressionId;

  @Schema(title = "实验实例ID")
  private String experimentInstanceId;

  @Schema(title = "上限")
  private String maxIndicatorExpressionItemId;

  @Schema(title = "下限")
  private String minIndicatorExpressionItemId;

  @Schema(title = "公式类型，0-条件，1-随机, 2-判断指标-危险因素")
  private Integer type;
}
