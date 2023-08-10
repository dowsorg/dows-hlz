package org.dows.hep.api.base.indicator.request;

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
@Schema(name = "RsExperimentGetCoreRequest对象", title = "根据实验期数以及实验人物ID获取核心指标串")
public class RsExperimentGetCoreRequest implements Serializable {
  @Schema(title = "实验期数")
  private Integer periods;

  @Schema(title = "实验人物ID列表")
  private List<String> experimentPersonIdList;
}
