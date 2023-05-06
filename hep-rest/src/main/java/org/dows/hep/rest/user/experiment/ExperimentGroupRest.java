package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.CreateGroupRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.biz.user.experiment.ExperimentGroupBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public List<ExperimentParticipatorResponse> listGroupMembers(@RequestParam @Validated String experimentGroupId) {
        return experimentGroupBiz.listGroupMembers(experimentGroupId);
    }

//    /**
//     * 分配小组成员
//     * @param
//     * @return
//     */
//    @Operation(summary = "分配小组成员")
//    @PostMapping("v1/userExperiment/experimentGroup/allotGroupMembers")
//    public ExperimentGroupResponse allotGroupMembers(@RequestBody @Validated CasePersonIndicatorFuncRequest request) {
//        return experimentGroupBiz.allotGroupMembers(experimentInstanceId);
//    }

}