package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.user.experiment.request.CreateGroupRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:实验:实验小组
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class ExperimentGroupBiz{
    /**
    * @param
    * @return
    * @说明: 创建团队
    * @关联表: 
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean createGroup(CreateGroupRequest createGroup ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取实验小组列表
    * @关联表: 
    * @工时: 0H
    * @开发者: 
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public ExperimentGroupResponse groupList(String experimentInstanceId ) {
        return new ExperimentGroupResponse();
    }
}