package org.dows.hep.biz.experiment.user;

import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.user.request.CreateGroupRequest;
import org.dows.hep.api.experiment.user.response.ExperimentGroupResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:实验:实验小组
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
public class ExperimentGroupBiz{
    /**
    * @param
    * @return
    * @说明: 创建团队
    * @关联表: 
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
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
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public ExperimentGroupResponse groupList(String experimentInstanceId ) {
        return new ExperimentGroupResponse();
    }
}