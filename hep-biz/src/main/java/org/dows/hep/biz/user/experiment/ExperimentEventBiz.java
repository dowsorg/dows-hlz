package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.user.experiment.request.FindExperimentEventRequest;
import org.dows.hep.api.user.experiment.request.HandlerEventRequest;
import org.dows.hep.api.user.experiment.response.ExperimentEventResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:实验:实验突发事件
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class ExperimentEventBiz{
    /**
    * @param
    * @return
    * @说明: 获取实验突发事件列表
    * @关联表: CaseEvent,CaseEventAction
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public ExperimentEventResponse pageExperimentEvent(FindExperimentEventRequest findExperimentEvent ) {
        return new ExperimentEventResponse();
    }
    /**
    * @param
    * @return
    * @说明: 实验突发事件处理
    * @关联表: CaseEvent,CaseEventAction,OperateEvent,OperateIndicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean handlerEvent(HandlerEventRequest handlerEvent ) {
        return Boolean.FALSE;
    }
}