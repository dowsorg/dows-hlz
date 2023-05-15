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
public class BatchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs implements Serializable {
  @Schema(title = "应用ID")
  private String appId;
  @Schema(title = "指标功能ID")
  private String indicatorFuncId;
  @Schema(title = "批量创建或修改健管目标DTO")
  private List<BatchCreateOrUpdateHealthManagementGoalDTO> batchCreateOrUpdateHealthManagementGoalDTOList;
}
