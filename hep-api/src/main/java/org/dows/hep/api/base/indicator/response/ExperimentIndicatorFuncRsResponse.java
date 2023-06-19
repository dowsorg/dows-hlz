package org.dows.hep.api.base.indicator.response;

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
public class ExperimentIndicatorFuncRsResponse implements Serializable {
  @Schema(title = "功能点id")
  private String indicatorFuncId;

  @Schema(title = "功能点名称")
  private String indicatorFuncName;

  @Schema(title = "功能点类别")
  private String indicatorCategoryId;

  @Schema(title = "顺序")
  private Integer seq;
}
