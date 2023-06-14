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

  @Schema(title = "功能点列表")
  private List<String> caseOrgModuleFuncRefIdList;
}
