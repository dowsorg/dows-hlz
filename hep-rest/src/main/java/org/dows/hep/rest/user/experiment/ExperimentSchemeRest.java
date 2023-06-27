package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.ExperimentScheme1Request;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeAllotRequest;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeRequest;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeSettingResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeStateResponse;
import org.dows.hep.biz.user.experiment.ExperimentBaseBiz;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private final ExperimentBaseBiz baseBiz;
    private final ExperimentSchemeBiz experimentSchemeBiz;

    /**
    * 获取方案设计
    * @param
    * @return
    */
    @Operation(summary = "获取方案设计")
    @GetMapping("v1/userExperiment/experimentScheme/getScheme")
    public ExperimentSchemeResponse getScheme(@NotBlank String experimentInstanceId, @NotBlank String experimentGroupId, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return experimentSchemeBiz.getScheme(experimentInstanceId, experimentGroupId, accountId, true);
    }

    /**
     * 获取方案设计状态
     * @param
     * @return
     */
    @Operation(summary = "获取方案设计状态")
    @GetMapping("v1/userExperiment/experimentScheme/getSchemeState")
    public ExperimentSchemeStateResponse getSchemeState(@NotBlank String experimentInstanceId, @NotBlank String experimentGroupId) {
        return experimentSchemeBiz.getSchemeState(experimentInstanceId, experimentGroupId);
    }

    /**
     * 分配方案设计
     * @param
     * @return
     */
    @Operation(summary = "分配方案设计")
    @PostMapping("v1/userExperiment/experimentScheme/allotSchemeMembers")
    public Boolean allotGroupMembers(@RequestBody @Validated ExperimentSchemeAllotRequest request) {
        return experimentSchemeBiz.allotSchemeMembers(request);
    }

    /**
     * 获取方案设计时间信息
     * @param
     * @return
     */
    @Operation(summary = "获取方案设计时间信息")
    @GetMapping("v1/userExperiment/experimentScheme/getSchemeDuration")
    public ExperimentSchemeSettingResponse getSchemeDuration(@NotBlank String experimentSchemeId) {
        return experimentSchemeBiz.getSchemeDuration(experimentSchemeId);
    }

    /**
     * 保存设计方案-单个保存
     * @param
     * @return
     */
    @Operation(summary = "保存设计方案item-单个保存")
    @PostMapping("v1/userExperiment/experimentScheme/updateSchemeItem")
    public Boolean updateSchemeItem(@RequestBody @Validated ExperimentScheme1Request experimentSchemeRequest, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return experimentSchemeBiz.updateScheme(experimentSchemeRequest.getExperimentSchemeItemId(), experimentSchemeRequest.getQuestionResult(), accountId);
    }

    /**
     * 保存设计方案
     * @param
     * @return
     */
    @Operation(summary = "保存设计方案")
    @PostMapping("v1/userExperiment/experimentScheme/updateScheme")
    public Boolean updateScheme(@RequestBody @Validated ExperimentSchemeRequest experimentSchemeRequest, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return experimentSchemeBiz.updateSchemeBatch(experimentSchemeRequest, accountId);
    }

    /**
    * 提交设计方案
    * @param
    * @return
    */
    @Operation(summary = "提交设计方案")
    @PutMapping("v1/userExperiment/experimentScheme/submitScheme")
    public Boolean submitScheme(@NotBlank String experimentInstanceId, @NotBlank String experimentGroupId, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return experimentSchemeBiz.submitScheme(experimentInstanceId, experimentGroupId, accountId);
    }


}