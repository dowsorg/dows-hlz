package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsAgeRequest implements Serializable {
  @Schema(title = "期数")
  private Integer periods;

  @Schema(title = "实验人物id集合")
  private Set<String> experimentPersonIdSet;
}
