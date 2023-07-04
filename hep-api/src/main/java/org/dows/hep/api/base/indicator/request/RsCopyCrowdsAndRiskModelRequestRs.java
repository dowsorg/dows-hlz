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
@Schema(name = "RsCopyCrowdsAndRiskModelRequestRs", title = "复制人群类型以及死亡原因以及公式到实验")
public class RsCopyCrowdsAndRiskModelRequestRs implements Serializable {
  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "实验id")
  private String experimentInstanceId;
}
