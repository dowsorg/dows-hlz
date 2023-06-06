package org.dows.hep.api.base.indicator.response;

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
@Schema(name = "IndicatorFunc 对象", title = "指标功能列表")
public class IndicatorFuncOrgItemResponse implements Serializable {
  @Schema(title = "功能点名字")
  private String name;

  @Schema(title = "功能点id")
  private String indicatorFuncId;
}
