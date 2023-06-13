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
 * 指标公式实体类
 * @author runsix
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseIndicatorExpressionEntity", title = "案例指标公式")
@TableName("case_indicator_expression")
public class CaseIndicatorExpressionEntity implements CrudEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "案例分布式ID")
  private String caseIndicatorExpressionId;

  @Schema(title = "分布式ID")
  private String indicatorExpressionId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "案例承接结果的分布式ID")
  private String casePrincipalId;

  @Schema(title = "承接结果的分布式ID")
  private String principalId;

  @Schema(title = "上限")
  private String maxIndicatorExpressionItemId;

  @Schema(title = "下限")
  private String minIndicatorExpressionItemId;

  @Schema(title = "公式类型，0-条件，1-随机")
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
