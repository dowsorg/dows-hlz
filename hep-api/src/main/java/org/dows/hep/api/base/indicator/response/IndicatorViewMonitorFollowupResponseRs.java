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
public class IndicatorViewMonitorFollowupResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String indicatorViewMonitorFollowupId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标功能ID")
  private String indicatorFuncId;

  @Schema(title = "指标监测随访类表名称")
  private String name;

  @Schema(title = "0-禁用，1-启用")
  private Integer status;

  @Schema(title = "监测随访表的类别")
  private IndicatorCategoryResponse indicatorCategoryResponse;

  @Schema(title = "时间戳")
  private Date dt;

  @Schema(title = "监测随访表的随访内容列表")
  private List<IndicatorViewMonitorFollowupFollowupContentResponseRs> indicatorViewMonitorFollowupFollowupContentResponseRsList;

}
