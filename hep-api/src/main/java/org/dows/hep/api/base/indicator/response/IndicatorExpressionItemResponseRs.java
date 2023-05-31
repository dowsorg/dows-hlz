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

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "IndicatorExpressionItemResponseRs对象", title = "指标公式细项响应")
public class IndicatorExpressionItemResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String indicatorExpressionItemId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  private String indicatorExpressionId;

  @Schema(title = "条件")
  private String conditionExpression;

  @Schema(title = "条件参数名字，以英文逗号分割")
  private String conditionNameList;

  @Schema(title = "条件参数数值，以英文逗号分割")
  private String conditionValList;

  @Schema(title = "结果")
  private String resultExpression;

  @Schema(title = "优先判断顺序")
  private Integer seq;

  @JsonIgnore
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @Schema(title = "时间戳")
  private Date dt;
}
