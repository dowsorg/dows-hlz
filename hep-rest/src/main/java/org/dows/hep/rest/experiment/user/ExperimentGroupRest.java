package org.dows.hep.rest.experiment.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.user.request.CreateGroupRequest;
import org.dows.hep.api.experiment.user.response.ExperimentGroupResponse;
import org.dows.hep.biz.experiment.user.ExperimentGroupBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验小组
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "实验小组")
public class ExperimentGroupRest {
    private final ExperimentGroupBiz experimentGroupBiz;

    /**
    * 创建团队
    * @param
    * @return
    */
    @ApiOperation("创建团队")
    @PostMapping("v1/experimentUser/experimentGroup/createGroup")
    public Boolean createGroup(@RequestBody @Validated CreateGroupRequest createGroup ) {
        return experimentGroupBiz.createGroup(createGroup);
    }

    /**
    * 获取实验小组列表
    * @param
    * @return
    */
    @ApiOperation("获取实验小组列表")
    @GetMapping("v1/experimentUser/experimentGroup/groupList")
    public ExperimentGroupResponse groupList(@Validated String experimentInstanceId) {
        return experimentGroupBiz.groupList(experimentInstanceId);
    }


}