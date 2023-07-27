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
@Schema(name = "ExperimentPersonHealthRiskFactorRsEntity", title = "实验风险模型")
@TableName("experiment_person_health_risk_factor_rs")
public class ExperimentPersonHealthRiskFactorRsEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

  @Schema(title = "分布式id")
  private String experimentPersonHealthRiskFactorId;

  @Schema(title = "关联分布式id")
  private String experimentPersonRiskModelId;

  @Schema(title = "实验指标id")
  private String experimentIndicatorInstanceId;

  @Schema(title = "实验指标名称")
  private String name;

  @Schema(title = "实验指标值")
  private String val;

  @Schema(title = "危险分数")
  private Double riskScore;

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
