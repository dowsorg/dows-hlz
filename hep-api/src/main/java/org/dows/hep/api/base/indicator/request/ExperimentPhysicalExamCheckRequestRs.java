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
public class ExperimentPhysicalExamCheckRequestRs implements Serializable {
  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "实验id")
  private String experimentId;

  @Schema(title = "实验人物id")
  private String experimentPersonId;

  @Schema(title = "功能点id")
  private String indicatorFuncId;

  @Schema(title = "机构id")
  private String experimentOrgId;

  @Schema(title = "体格检查id")
  private List<String> experimentIndicatorViewPhysicalExamIdList;

}
