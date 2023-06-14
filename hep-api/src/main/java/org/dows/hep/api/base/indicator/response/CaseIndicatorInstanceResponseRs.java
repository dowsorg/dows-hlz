package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CaseIndicatorInstanceResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String indicatorInstanceId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "主体id")
  private String principal;

  @Schema(title = "指标分类ID")
  private String indicatorCategoryId;

  @Schema(title = "指标名称")
  private String indicatorName;

  @Schema(title = "是否按照百分比展示")
  private Integer displayByPercent;

  @Schema(title = "单位")
  private String unit;

  @Schema(title = "0-非关键指标，1-关键指标")
  private Integer core;

  @Schema(title = "0-非饮食关键指标，1-饮食关键指标")
  private Integer food;

  @Schema(title = "描述")
  private String descr;

  @Schema(title = "时间戳")
  private Date dt;

  @Schema(title = "指标公式")
  private List<CaseIndicatorExpressionResponseRs> caseIndicatorExpressionResponseRsList;

  @Schema(title = "指标默认值")
  private String def;

  @Schema(title = "指标推荐最小值")
  private String min;

  @Schema(title = "指标推荐最大值")
  private String max;

  @Schema(title = "指标顺序")
  private Integer seq;
}
