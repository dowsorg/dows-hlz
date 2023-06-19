package org.dows.hep.api.base.indicator.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentOrgModuleRsResponse implements Serializable {
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

  @Schema(title = "功能点列表")
  private List<ExperimentIndicatorFuncRsResponse> experimentIndicatorFuncRsResponseList;

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
