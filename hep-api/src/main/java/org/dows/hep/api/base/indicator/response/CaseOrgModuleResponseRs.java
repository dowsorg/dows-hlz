package org.dows.hep.api.base.indicator.response;

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
public class CaseOrgModuleResponseRs implements Serializable {
  @Schema(title = "分布式ID")
  private String caseOrgModuleId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "机构分布式ID")
  private String caseOrgId;

  @Schema(title = "模块名称")
  private String name;

  @Schema(title = "顺序")
  private Integer seq;

  @Schema(title = "功能点响应列表")
  private List<CaseOrgModuleFuncRefResponseRs> caseOrgModuleFuncRefResponseRsList;
}
