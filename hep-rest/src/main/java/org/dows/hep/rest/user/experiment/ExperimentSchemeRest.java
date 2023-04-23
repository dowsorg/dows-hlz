package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.DesignSchemeRequest;
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
    * 设计实验方案
    * @param
    * @return
    */
    @Operation(summary = "设计实验方案")
    @PostMapping("v1/userExperiment/experimentScheme/designScheme")
    public Boolean designScheme(@RequestBody @Validated DesignSchemeRequest designScheme ) {
        return experimentSchemeBiz.designScheme(designScheme);
    }

    /**
    * 提交方案
    * @param
    * @return
    */
    @Operation(summary = "提交方案")
    @PostMapping("v1/userExperiment/experimentScheme/submitScheme")
    public Boolean submitScheme(@RequestBody @Validated String schemeId ) {
        return experimentSchemeBiz.submitScheme(schemeId);
    }


}