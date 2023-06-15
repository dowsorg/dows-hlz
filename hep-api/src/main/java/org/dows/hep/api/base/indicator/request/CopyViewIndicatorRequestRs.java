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
@Schema(name = "CopyViewIndicatorRequestRs", title = "复制查看指标到实验需要的请求参数")
public class CopyViewIndicatorRequestRs implements Serializable {
  @Schema(title = "应用ID")
  private String appId;
}
