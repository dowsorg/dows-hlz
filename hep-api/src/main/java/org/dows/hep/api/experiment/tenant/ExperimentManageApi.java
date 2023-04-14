package org.dows.hep.api.experiment.tenant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.tenant.request.CreateExperimentRequest;
import org.dows.hep.api.experiment.tenant.request.GroupSettingRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
* @description project descr:实验:实验管理
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@Api(tags = "实验管理")
public interface ExperimentManageApi{
    /**
    * 分配实验
    * @param
    * @return
    */
    @ApiOperation("分配实验")
    @PostMapping("experimentTenant/experimentManage/experimentAllot/v1")
    String experimentAllot(CreateExperimentRequest createExperiment );
    /**
    * 实验分组ss
    * @param
    * @return
    */
    @ApiOperation("实验分组ss")
    @PostMapping("experimentTenant/experimentManage/experimentGrouping/v1")
    Boolean experimentGrouping(GroupSettingRequest groupSetting );

}