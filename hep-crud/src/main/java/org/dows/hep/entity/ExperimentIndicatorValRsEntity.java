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
import org.dows.framework.crud.api.CrudEntity;

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
@Schema(name = "ExperimentIndicatorValRsEntity", title = "实验指标值")
@TableName("experiment_indicator_val_rs")
public class ExperimentIndicatorValRsEntity implements CrudEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "实验指标值分布式ID")
  private String experimentIndicatorValId;

  @Schema(title = "实验实例ID")
  private String experimentId;

  @Schema(title = "案例ID")
  private String caseId;

  @Schema(title = "实验指标ID")
  public String indicatorInstanceId;

  @Schema(title = "当前值")
  private String currentVal;

  @Schema(title = "期数")
  private Integer periods;

  @Schema(title = "最小值")
  private String min;

  @Schema(title = "最大值")
  private String max;

  @Schema(title = "描述")
  private String descr;

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
