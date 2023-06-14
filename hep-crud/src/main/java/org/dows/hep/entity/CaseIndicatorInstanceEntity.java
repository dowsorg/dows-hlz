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
@Schema(name = "CaseIndicatorInstanceEntity", title = "案例库指标")
@TableName("case_indicator_instance")
public class CaseIndicatorInstanceEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

  @Schema(title = "案例指标ID")
  private String caseIndicatorInstanceId;

  @Schema(title = "数据库指标ID")
  private String indicatorInstanceId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "主体id")
  private String principalId;

  @Schema(title = "指标分类ID")
  private String indicatorCategoryId;

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
