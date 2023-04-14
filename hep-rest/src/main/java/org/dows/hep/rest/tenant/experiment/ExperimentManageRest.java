package org.dows.hep.rest.tenant.experiment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.tenant.experiment.request.CreateExperimentRequest;
import org.dows.hep.api.tenant.experiment.request.GroupSettingRequest;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.biz.tenant.experiment.ExperimentManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验管理
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验管理", description = "实验管理")
public class ExperimentManageRest {
    private final ExperimentManageBiz experimentManageBiz;

    /**
    * 分配实验
    * @param
    * @return
    */
    @Operation(summary = "分配实验")
    @PostMapping("v1/tenantExperiment/experimentManage/experimentAllot")
    public String experimentAllot(@RequestBody @Validated CreateExperimentRequest createExperiment ) {
        return experimentManageBiz.experimentAllot(createExperiment);
    }

    /**
    * 实验分组ss
    * @param
    * @return
    */
    @Operation(summary = "实验分组ss")
    @PostMapping("v1/tenantExperiment/experimentManage/experimentGrouping")
    public Boolean experimentGrouping(@RequestBody @Validated GroupSettingRequest groupSetting ) {
        return experimentManageBiz.experimentGrouping(groupSetting);
    }

    /**
    * 获取实验列表
    * @param
    * @return
    */
    @Operation(summary = "获取实验列表")
    @GetMapping("v1/tenantExperiment/experimentManage/experimentList")
    public ExperimentListResponse experimentList() {
        return experimentManageBiz.experimentList();
    }


}