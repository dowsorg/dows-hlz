package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrUpdateCaseOrgModuleRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String caseOrgModuleId;

  @Schema(title = "机构分布式ID")
  private String caseOrgId;

  @Schema(title = "模块名称")
  private String name;

  @Schema(title = "顺序")
  private Integer seq;

  @Schema(title = "创建或修改功能关联关系列表")
  private List<CreateOrUpdateCaseOrgModuleFuncRefRequestRs> createOrUpdateCaseOrgModuleFuncRefRequestRsList;
}
