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

import org.dows.hep.api.experiment.tenant.ExperimentManageApi;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验管理
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
@RequiredArgsConstructor
@RestController
public class ExperimentManageRest implements ExperimentManageApi{
    private final ExperimentManageBiz experimentManageBiz;

    /**
    * 分配实验
    * @param
    * @return
    */
    @Override
    public String experimentAllot(@RequestBody @Validated CreateExperimentRequest createExperiment) {
        return experimentManageBiz.experimentAllot(createExperiment);
    }

    /**
    * 实验分组ss
    * @param
    * @return
    */
    @Override
    public Boolean experimentGrouping(@RequestBody @Validated GroupSettingRequest groupSetting) {
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