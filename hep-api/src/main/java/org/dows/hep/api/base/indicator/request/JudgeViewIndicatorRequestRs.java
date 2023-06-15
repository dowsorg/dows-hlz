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
@Schema(name = "JudgeViewIndicatorRequestRs", title = "复制判断指标到实验需要的请求参数")
public class JudgeViewIndicatorRequestRs implements Serializable {
  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "实验id")
  private String experimentInstanceId;
}
