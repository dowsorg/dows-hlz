package org.dows.hep.biz.experiment.tenant;

import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.tenant.request.CreateExperimentRequest;
import org.dows.hep.api.experiment.tenant.request.GroupSettingRequest;
import org.dows.hep.api.experiment.tenant.response.ExperimentListResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:实验:实验管理
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
public class ExperimentManageBiz{
    /**
    * @param
    * @return
    * @说明: 分配实验
    * @关联表: ExperimentInstance,experimentSetting
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:31:43
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
    * @创建时间: 2023年4月14日 下午3:31:43
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
    * @创建时间: 2023年4月14日 下午3:31:43
    */
    public ExperimentListResponse experimentList() {
        return new ExperimentListResponse();
    }
}