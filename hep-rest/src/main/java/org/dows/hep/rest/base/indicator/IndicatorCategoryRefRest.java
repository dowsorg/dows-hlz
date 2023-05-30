package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "指标类别", description = "指标类别")
public class IndicatorCategoryRefRest {
}
