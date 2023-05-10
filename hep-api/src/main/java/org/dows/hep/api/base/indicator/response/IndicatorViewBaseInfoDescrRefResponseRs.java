package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicatorViewBaseInfoDescrRefResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoDescRefId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标描述表ID")
  private String indicatorViewBaseInfoDescId;

  @Schema(title = "指标")
  private IndicatorInstanceResponseRs indicatorInstanceResponseRs;

  @Schema(title = "展示顺序")
  private Integer seq;

  @Schema(title = "时间戳")
  private Date dt;
}
