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
@Schema(name = "CaseIndicatorPrincipalRefEntity", title = "案例库指标实体关联关系")
@TableName("case_indicator_principal_ref")
public class CaseIndicatorPrincipalRefEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

  @Schema(title = "分布式id")
  private String caseIndicatorPrincipalRefId;

  @Schema(title = "主体id")
  private String principalId;

  @Schema(title = "数据库指标ID")
  private String indicatorInstanceId;

  @Schema(title = "应用ID")
  private String appId;

  @JsonIgnore
  @TableLogic
  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @TableField(fill = FieldFill.INSERT)
  @Schema(title = "时间戳")
  private Date dt;
}
