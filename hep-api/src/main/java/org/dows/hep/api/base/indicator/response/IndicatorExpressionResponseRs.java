package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "IndicatorExpressionResponseRs对象", title = "指标公式响应")
public class IndicatorExpressionResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String indicatorExpressionId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标公式默认值")
  private String def;

  @JsonIgnore
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @Schema(title = "时间戳")
  private Date dt;

  @Schema(title = "指标公式细项列表")
  private List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList;
}
