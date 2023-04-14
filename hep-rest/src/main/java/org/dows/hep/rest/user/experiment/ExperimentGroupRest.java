package org.dows.hep.rest.user.experiment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.user.experiment.request.CreateGroupRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.biz.user.experiment.ExperimentGroupBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验小组
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
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
    @GetMapping("v1/userExperiment/experimentGroup/groupList")
    public ExperimentGroupResponse groupList(@Validated String experimentInstanceId) {
        return experimentGroupBiz.groupList(experimentInstanceId);
    }


}