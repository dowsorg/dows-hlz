package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeRequest;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:实验:实验方案
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验方案", description = "实验方案")
public class ExperimentSchemeRest {
    private final ExperimentSchemeBiz experimentSchemeBiz;

    /**
    * 获取设计方案
    * @param
    * @return
    */
    @Operation(summary = "获取设计方案")
    @PostMapping("v1/userExperiment/experimentScheme/getCaseScheme")
    public ExperimentSchemeResponse getCaseScheme(String experimentInstanceId, String experimentGroupId ) {
        return experimentSchemeBiz.getCaseScheme(experimentInstanceId, experimentGroupId);
    }

    /**
    * 提交设计方案
    * @param
    * @return
    */
    @Operation(summary = "提交设计方案")
    @PostMapping("v1/userExperiment/experimentScheme/submitScheme")
    public Boolean submitScheme(@RequestBody @Validated ExperimentSchemeRequest request ) {
        return experimentSchemeBiz.submitScheme(request);
    }


}