package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
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
public class BatchCreateOrUpdateCaseOrgModuleRequestRs implements Serializable {
  @Schema(title = "机构分布式ID")
  @ApiModelProperty(required = true)
  private String caseOrgId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "案例机构模块列表")
  private List<CreateOrUpdateCaseOrgModuleRequestRs> createOrUpdateCaseOrgModuleRequestRsList;
}
