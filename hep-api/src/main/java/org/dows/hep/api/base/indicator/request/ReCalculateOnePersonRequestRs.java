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
public class ReCalculateOnePersonRequestRs implements Serializable {
  @Schema(title = "appId")
  private String appId;

  @Schema(title = "案例指标-绑定的主体")
  private String accountId;
}
