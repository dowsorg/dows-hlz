package org.dows.hep.biz.tenant.experiment;

import org.dows.hep.api.tenant.experiment.request.CreateExperimentRequest;
import org.dows.hep.api.tenant.experiment.request.GroupSettingRequest;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:实验:实验管理
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class ExperimentManageBiz{
    /**
    * @param
    * @return
    * @说明: 分配实验
    * @关联表: ExperimentInstance,experimentSetting
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String experimentAllot(CreateExperimentRequest createExperiment ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 实验分组ss
    * @关联表: experimentGroup,experimentParticipator
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean experimentGrouping(GroupSettingRequest groupSetting ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取实验列表
    * @关联表: ExperimentInstance
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public ExperimentListResponse experimentList() {
        return new ExperimentListResponse();
    }
}