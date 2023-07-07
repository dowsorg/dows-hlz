package org.dows.hep.api.base.indicator.response;

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
public class ExperimentIndicatorViewBaseInfoDescrRsResponse implements Serializable {
  private String name;
  private List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList;
}
