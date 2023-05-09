package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class IndicatorViewMonitorFollowupContentRefResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String indicatorViewMonitorFollowupContentRefId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标监测随访内容ID")
  private String indicatorViewMonitorFollowupFollowupContentId;

  @Schema(title = "指标ID")
  private IndicatorInstanceResponseRs indicatorInstanceResponseRs;

  @Schema(title = "展示顺序")
  private Integer seq;
}
