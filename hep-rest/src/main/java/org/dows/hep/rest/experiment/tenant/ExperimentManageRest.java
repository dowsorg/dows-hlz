package org.dows.hep.rest.experiment.tenant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.tenant.request.CreateExperimentRequest;
import org.dows.hep.api.experiment.tenant.request.GroupSettingRequest;
import org.dows.hep.api.experiment.tenant.response.ExperimentListResponse;
import org.dows.hep.biz.experiment.tenant.ExperimentManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验管理
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "实验管理")
public class ExperimentManageRest {
    private final ExperimentManageBiz experimentManageBiz;

    /**
    * 分配实验
    * @param
    * @return
    */
    @ApiOperation("分配实验")
    @PostMapping("v1/experimentTenant/experimentManage/experimentAllot")
    public String experimentAllot(@RequestBody @Validated CreateExperimentRequest createExperiment ) {
        return experimentManageBiz.experimentAllot(createExperiment);
    }

    /**
    * 实验分组
    * @param
    * @return
    */
    @ApiOperation("实验分组")
    @PostMapping("v1/experimentTenant/experimentManage/experimentGrouping")
    public Boolean experimentGrouping(@RequestBody @Validated GroupSettingRequest groupSetting ) {
        return experimentManageBiz.experimentGrouping(groupSetting);
    }

    /**
    * 获取实验列表
    * @param
    * @return
    */
    @ApiOperation("获取实验列表")
    @GetMapping("v1/experimentTenant/experimentManage/experimentList")
    public ExperimentListResponse experimentList() {
        return experimentManageBiz.experimentList();
    }


}