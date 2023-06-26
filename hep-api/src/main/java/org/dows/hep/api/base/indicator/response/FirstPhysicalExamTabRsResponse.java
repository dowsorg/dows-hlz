package org.dows.hep.api.base.indicator.response;

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
public class FirstPhysicalExamTabRsResponse implements Serializable {
  @Schema(title = "第一层目录id")
  private String indicatorCategoryId;

  @Schema(title = "第一层目录名称")
  private String indicatorCategoryName;

  @Schema(title = "第一层目录创建时间")
  private Date indicatorCategoryDt;

  @Schema(title = "查看指标-二级类-无报告-体格检查列表")
  private List<ExperimentIndicatorViewPhysicalExamRsResponse> children;
}
