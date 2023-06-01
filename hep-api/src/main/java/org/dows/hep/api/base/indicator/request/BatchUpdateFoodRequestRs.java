package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@Schema(name = "BatchUpdateFoodRequestRs 对象", title = "批量更新饮食关键指标")
public class BatchUpdateFoodRequestRs implements Serializable {
  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标实例ID列表")
  private List<String> indicatorInstanceIdList;
}
