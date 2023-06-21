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
@Schema(name = "ExperimentIndicatorExpressionItemRsEntity", title = "实验指标值")
@TableName("experiment_indicator_expression_item_rs")
public class ExperimentIndicatorExpressionItemRsEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String experimentIndicatorExpressionItemId;

  @Schema(title = "分布式ID")
  private String caseIndicatorExpressionItemId;

  @Schema(title = "实验实例ID")
  private String experimentId;

  @Schema(title = "案例ID")
  private String caseId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  private String indicatorExpressionId;

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

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
