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
@Schema(name = "IndicatorExpressionItemEntity", title = "指标公式细项")
@TableName("indicator_expression_item")
public class IndicatorExpressionItemEntity implements CrudEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String indicatorExpressionItemId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  private String indicatorExpressionId;

  @Schema(title = "条件")
  private String condition;

  @Schema(title = "条件参数名字，以英文逗号分割")
  private String conditionNameList;

  @Schema(title = "条件参数数值，以英文逗号分割")
  private String conditionValList;

  @Schema(title = "结果")
  private String result;

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
