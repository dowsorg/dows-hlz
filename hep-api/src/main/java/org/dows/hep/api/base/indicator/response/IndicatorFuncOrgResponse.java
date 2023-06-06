package org.dows.hep.api.base.indicator.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "IndicatorFuncOrgResponse对象", title = "自定义-机构管理-编辑功能-指标功能列表")
public class IndicatorFuncOrgResponse implements Serializable {
  @Schema(title = "父类名称")
  private String pName;

  @Schema(title = "父类id")
  private String pid;

  @Schema(title = "功能点列表")
  private List<IndicatorFuncOrgItemResponse> indicatorFuncOrgItemResponseList;
}
