package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsCalculatePersonRequestRs implements Serializable {
  @Schema(title = "appId")
  private String appId;

  @Schema(title = "实验id")
  private String experimentId;

  @Schema(title = "期数")
  private Integer periods;

  @Schema(title = "人物id")
  private Set<String> personIdSet;
}
