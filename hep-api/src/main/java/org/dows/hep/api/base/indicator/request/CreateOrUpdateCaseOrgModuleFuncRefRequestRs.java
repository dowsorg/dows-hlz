package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrUpdateCaseOrgModuleFuncRefRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String caseOrgModuleFuncRefId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  private String caseOrgModuleId;

  @Schema(title = "功能点分布式ID")
  private String indicatorFuncId;

  @Schema(title = "顺序")
  private Integer seq;
}
