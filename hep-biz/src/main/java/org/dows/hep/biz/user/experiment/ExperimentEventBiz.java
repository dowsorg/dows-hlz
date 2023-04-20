package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.user.experiment.request.HRequest;
import org.dows.hep.api.user.experiment.request.ListExperimentEventRequest;
import org.dows.hep.api.user.experiment.response.ExperimentEventResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:实验:实验突发事件
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class ExperimentEventBiz{
    /**
    * @param
    * @return
    * @说明: 
    * @关联表: CaseEvent,CaseEventAction
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public ExperimentEventResponse list(ListExperimentEventRequest listExperimentEvent ) {
        return new ExperimentEventResponse();
    }
    /**
    * @param
    * @return
    * @说明: 
    * @关联表: CaseEvent,CaseEventAction,OperateEvent,OperateIndicator,
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean handlerEvent(HRequest h ) {
        return Boolean.FALSE;
    }
}