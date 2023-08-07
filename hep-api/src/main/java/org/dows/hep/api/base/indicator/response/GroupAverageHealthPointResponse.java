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
public class GroupAverageHealthPointResponse implements Serializable {
  @Schema(title = "社区人数")
  private Integer experimentPersonCount;

  @Schema(title = "平均健康指数")
  private String averageHealthPoint;

  @Schema(title = "上一期排名")
  private Integer rank;
}
