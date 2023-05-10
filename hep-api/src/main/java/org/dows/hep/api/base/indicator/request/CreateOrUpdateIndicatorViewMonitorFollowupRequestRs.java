package org.dows.hep.api.base.indicator.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.Acceleration;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrUpdateIndicatorViewMonitorFollowupRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorViewMonitorFollowupId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标功能ID")
  private String indicatorFuncId;

  @Schema(title = "指标监测随访类表名称")
  private String name;

  @Schema(title = "监测随访表类别Id")
  private String indicatorCategoryId;

  @Schema(title = "0-禁用，1-启用")
  private Integer status;

  @Schema(title = "随访内容列表")
  private List<CreateOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs> createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRsList;
}
