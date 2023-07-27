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
@Schema(name = "ExperimentPersonRiskModelRsEntity", title = "实验风险模型")
@TableName("experiment_person_risk_model_rs")
public class ExperimentPersonRiskModelRsEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

  @Schema(title = "分布式id")
  private String experimentPersonRiskModelId;

  @Schema(title = "实验id")
  private String experimentId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "期数-从0开始，0表示干预前")
  private Integer periods;

  @Schema(title = "实验人物id")
  private String experimentPersonId;

  @Schema(title = "风险模型ID")
  private String experimentRiskModelId;

  @Schema(title = "模型名称")
  private String name;

  @Schema(title = "死亡概率")
  private Integer riskDeathProbability;

  @Schema(title = "组合危险分数")
  private Double composeRiskScore;

  @Schema(title = "存在死亡危险")
  private Double existDeathRiskScore;

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
