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
 * 指标公式影响类
 * @author runsix
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "IndicatorExpressionInfluenceEntity", title = "指标公式影响表")
@TableName("indicator_expression_influence")
public class IndicatorExpressionInfluenceEntity implements CrudEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String indicatorExpressionInfluenceId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标分布式ID")
  private String indicatorInstanceId;

  @Schema(title = "这个指标影响其它指标列表，用逗号分割")
  private String influenceIndicatorInstanceIdList;

  @Schema(title = "这个指标被其它指标影响的列表，用逗号分割")
  private String influencedIndicatorInstanceIdList;

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
