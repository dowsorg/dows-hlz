package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.CreateGroupRequest;
import org.dows.hep.api.user.experiment.request.ExperimentAllotSchemeRequest;
import org.dows.hep.api.user.experiment.request.ExperimentParticipatorRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.biz.user.experiment.ExperimentGroupBiz;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.entity.ExperimentOrgEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验小组
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验小组", description = "实验小组")
public class ExperimentGroupRest {
    private final ExperimentSchemeBiz experimentSchemeBiz;
    private final ExperimentGroupBiz experimentGroupBiz;

    /**
    * 创建团队
    * @param
    * @return
    */
    @Operation(summary = "创建团队")
    @PostMapping("v1/userExperiment/experimentGroup/createGroup")
    public Boolean createGroup(@RequestBody @Validated CreateGroupRequest createGroup ) {
        return experimentGroupBiz.createGroup(createGroup);
    }

    /**
     * 获取某个实验中某个小组的机构列表
     * @param
     * @return
     */
    @Operation(summary = "获取某个实验中某个小组的机构列表")
    @PostMapping("v1/userExperiment/experimentGroup/listExperimentGroupOrg")
    public List<ExperimentOrgEntity> listExperimentGroupOrg(@RequestParam @Validated String experimentGroupId,
                                                            @RequestParam @Validated String experimentInstanceId,
                                                            @RequestParam @Validated String periods) {
        return experimentGroupBiz.listExperimentGroupOrg(experimentGroupId,experimentInstanceId,periods);
    }

    /**
    * 获取实验小组列表
    * @param
    * @return
    */
    @Operation(summary = "获取实验小组列表")
    @PostMapping("v1/userExperiment/experimentGroup/listGroup")
    public List<ExperimentGroupResponse> listGroup(@Validated String experimentInstanceId) {
        return experimentGroupBiz.listGroup(experimentInstanceId);
    }

    /**
     * 获取小组组员列表
     * @param
     * @return
     */
    @Operation(summary = "获取小组组员列表")
    @PostMapping("v1/userExperiment/experimentGroup/listGroupMembers")
    public List<ExperimentParticipatorResponse> listGroupMembers(@RequestParam @Validated String experimentGroupId,@RequestParam @Validated String experimentInstanceId) {
        return experimentGroupBiz.listGroupMembers(experimentGroupId,experimentInstanceId);
    }

    /**
     * 分配小组成员
     * @param
     * @return
     */
    @Operation(summary = "分配小组成员")
    @PostMapping("v1/userExperiment/experimentGroup/allotGroupMembers")
    public Boolean allotGroupMembers(@RequestBody @Validated ExperimentParticipatorRequest request) {
        return experimentGroupBiz.allotGroupMembers(request);
    }

    /**
     * 获取方案设计列表
     * @param
     * @return
     */
    @Operation(summary = "获取方案设计列表")
    @GetMapping("v1/userExperiment/experimentGroup/listSchemeItem")
    public ExperimentSchemeResponse listSchemeItem(String experimentInstanceId, String experimentGroupId, String accountId) {
        return experimentSchemeBiz.getScheme(experimentInstanceId, experimentGroupId, accountId);
    }

    /**
     * 分配方案设计
     * @param
     * @return
     */
    @Operation(summary = "分配方案设计")
    @PostMapping("v1/userExperiment/experimentGroup/allotSchemeMembers")
    public Boolean allotGroupMembers(@RequestBody @Validated ExperimentAllotSchemeRequest request) {
        return experimentGroupBiz.allotSchemeMembers(request);
    }

    /**
     * 根据小组ID获取小组信息
     * @param
     * @return
     */
    @Operation(summary = "根据小组ID获取小组信息")
    @GetMapping("v1/userExperiment/experimentGroup/getGroupInfoByExperimentId/{experimentGroupId}/{experimentGInstanceId}")
    public ExperimentGroupResponse getGroupInfoByExperimentId(@PathVariable @Validated String experimentGroupId,@PathVariable @Validated String experimentGInstanceId) {
        return experimentGroupBiz.getGroupInfoByExperimentId(experimentGroupId,experimentGInstanceId);
    }

}