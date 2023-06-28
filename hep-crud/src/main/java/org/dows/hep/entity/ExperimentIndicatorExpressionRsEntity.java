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
@Schema(name = "ExperimentIndicatorExpressionRsEntity", title = "实验指标值")
@TableName("experiment_indicator_expression_rs")
public class ExperimentIndicatorExpressionRsEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "实验指标表达式分布式ID")
  private String experimentIndicatorExpressionId;

  @Schema(title = "案例指标表达式分布式ID")
  private String caseIndicatorExpressionId;

  @Schema(title = "数据库指标表达式分布式ID")
  private String indicatorExpressionId;

  @Schema(title = "实验实例ID")
  private String experimentId;

  @Schema(title = "案例ID")
  private String caseId;

  @Schema(title = "承接结果的分布式ID")
  private String principalId;

  @Schema(title = "产生这个指标公式的分布式ID")
  private String reasonId;

  @Schema(title = "上限")
  private String maxIndicatorExpressionItemId;

  @Schema(title = "下限")
  private String minIndicatorExpressionItemId;

  @Schema(title = "公式类型，0-条件，1-随机, 2-判断指标-危险因素")
  private Integer type;

  @Schema(title = "公式来源，详情见EnumIndicatorExpressionSource")
  private Integer source;

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
