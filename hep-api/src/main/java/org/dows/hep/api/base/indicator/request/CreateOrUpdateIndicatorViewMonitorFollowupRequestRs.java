package org.dows.hep.api.base.indicator.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
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
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "指标功能ID")
  @ApiModelProperty(required = true)
  private String indicatorFuncId;

  @Schema(title = "指标监测随访类表名称")
  @ApiModelProperty(required = true)
  private String name;

  @Schema(title = "监测随访表类别Id")
  @ApiModelProperty(required = true)
  private String indicatorCategoryId;

  @Schema(title = "0-禁用，1-启用")
  @ApiModelProperty(required = true)
  private Integer status;

  @Schema(title = "随访内容列表")
  @ApiModelProperty(required = true, value = "查看指标-监测随访-随访内容列表")
  private List<CreateOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs> createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRsList;
}
