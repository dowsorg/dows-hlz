package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.ExperimentAllotSchemeRequest;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeRequest;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:实验:实验方案设计
* @folder user-hep/实验方案设计
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验方案设计", description = "实验方案设计")
public class ExperimentSchemeRest {
    private final ExperimentSchemeBiz experimentSchemeBiz;

    /**
    * 获取方案设计
    * @param
    * @return
    */
    @Operation(summary = "获取方案设计")
    @GetMapping("v1/userExperiment/experimentScheme/getScheme")
    public ExperimentSchemeResponse getScheme(String experimentInstanceId, String experimentGroupId, String accountId ) {
        return experimentSchemeBiz.getScheme(experimentInstanceId, experimentGroupId, accountId);
    }

    /**
     * 分配方案设计
     * @param
     * @return
     */
    @Operation(summary = "分配方案设计")
    @PostMapping("v1/userExperiment/experimentScheme/allotSchemeMembers")
    public Boolean allotGroupMembers(@RequestBody @Validated ExperimentAllotSchemeRequest request) {
        return experimentSchemeBiz.allotSchemeMembers(request);
    }

    /**
     * 保存设计方案
     * @param
     * @return
     */
    @Operation(summary = "保存设计方案")
    @PostMapping("v1/userExperiment/experimentScheme/updateScheme")
    public Boolean updateScheme(@RequestBody @Validated ExperimentSchemeRequest request ) {
        return experimentSchemeBiz.updateScheme(request);
    }

    /**
    * 提交设计方案
    * @param
    * @return
    */
    @Operation(summary = "提交设计方案")
    @GetMapping("v1/userExperiment/experimentScheme/submitScheme")
    public Boolean submitScheme(String experimentInstanceId, String experimentGroupId, String accountId) {
        return experimentSchemeBiz.submitScheme(experimentInstanceId, experimentGroupId, accountId);
    }


}