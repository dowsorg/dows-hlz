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
public class RsCalculateHealthScoreRequestRs implements Serializable {
  @Schema(title = "appId")
  private String appId;

  @Schema(title = "实验id")
  private String experimentId;

  @Schema(title = "实验人物id")
  private List<String> experimentPersonIdList;
}
