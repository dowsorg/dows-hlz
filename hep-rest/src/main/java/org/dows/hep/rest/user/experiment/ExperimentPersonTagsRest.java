package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.ExperimentIndicatorInstanceRequest;
import org.dows.hep.api.user.experiment.response.EchartsDataResonse;
import org.dows.hep.biz.user.experiment.ExperimentPersonTagsBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jx
 * @date 2023/7/13 17:06
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "实验人物标签", description = "实验人物标签")
public class ExperimentPersonTagsRest {

    private final ExperimentPersonTagsBiz experimentPersonTagsBiz;
    @Operation(summary = "实验疾病类别统计")
    @PostMapping("v1/experimentIndicator/diseaseRate/stat")
    public List<EchartsDataResonse> statDiseaseRate(@RequestBody ExperimentIndicatorInstanceRequest experimentIndicatorInstanceRequest) {
        return experimentPersonTagsBiz.statDiseaseRate(experimentIndicatorInstanceRequest);
    }
}
