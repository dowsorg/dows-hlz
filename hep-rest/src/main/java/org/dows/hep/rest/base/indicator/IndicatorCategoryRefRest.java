package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "指标类别", description = "指标类别")
public class IndicatorCategoryRefRest {
  private final ICRBiz

  @PutMapping("v1/baseIndicator/indicatorCategoryRef/update")
  public void updateSeq(@RequestBody UpdateIndicatorCategpryRefSeqRequestRs updateIndicatorCategpryRefSeqRequestRs) {

  }
}
