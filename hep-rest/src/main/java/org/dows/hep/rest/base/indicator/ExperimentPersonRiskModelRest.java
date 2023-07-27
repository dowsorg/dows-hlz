package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.ExperimentPersonRiskModelBiz;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验人物危险因素", description = "实验人物危险因素")
public class ExperimentPersonRiskModelRest {
  private final ExperimentPersonRiskModelBiz experimentPersonRiskModelBiz;
}
