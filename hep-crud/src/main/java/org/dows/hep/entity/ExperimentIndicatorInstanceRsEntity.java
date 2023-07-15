package org.dows.hep.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
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
@Schema(name = "ExperimentIndicatorInstanceRsEntity", title = "实验小组")
@TableName("experiment_indicator_instance_rs")
public class ExperimentIndicatorInstanceRsEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

  @Schema(title = "实验指标ID")
  private String experimentIndicatorInstanceId;

  @Schema(title = "案例指标ID")
  private String caseIndicatorInstanceId;

  @Schema(title = "基础库指标ID")
  private String indicatorInstanceId;

  @Schema(title = "实验实例ID")
  private String experimentId;

  @Schema(title = "案例ID")
  private String caseId;

  @Schema(title = "实验人物ID")
  private String experimentPersonId;

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

  @Schema(title = "类型，0代表非基础指标可以删除，其它均不可删除")
  private Integer type;

  @Schema(title = "描述")
  private String descr;

  @Schema(title = "最小值")
  private String min;

  @Schema(title = "最大值")
  private String max;

  @Schema(title = "默认值")
  private String def;

  @Schema(title = "期数反转时，重新计算人物指标顺序，内部使用，不需要对外提供")
  private Integer recalculateSeq;

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
