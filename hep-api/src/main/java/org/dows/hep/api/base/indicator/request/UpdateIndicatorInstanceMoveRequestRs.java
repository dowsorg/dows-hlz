package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@Schema(name = "UpdateIndicatorInstanceMoveRequestRs对象", title = "指标上下移动")
public class UpdateIndicatorInstanceMoveRequestRs implements Serializable {
  @ApiModelProperty(required = true)
  private String indicatorInstanceId;

  @ApiModelProperty(required = true, value = "1-上移，0-下移")
  private Integer up;
}
