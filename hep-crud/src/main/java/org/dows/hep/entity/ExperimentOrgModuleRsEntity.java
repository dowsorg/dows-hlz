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
@Schema(name = "ExperimentOrgModuleRsEntity", title = "实验机构模块")
@TableName("experiment_org_module_rs")
public class ExperimentOrgModuleRsEntity implements CrudEntity {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

  @Schema(title = "分布式ID")
  private String experimentOrgModuleId;

  @Schema(title = "分布式ID")
  private String caseOrgModuleId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "机构分布式ID")
  private String orgId;

  @Schema(title = "模块名称")
  private String name;

  @Schema(title = "功能点分布式ID数组")
  private String indicatorFuncIdArray;

  @Schema(title = "功能点分布式ID类别数组")
  private String indicatorCategoryIdArray;

  @Schema(title = "顺序")
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
