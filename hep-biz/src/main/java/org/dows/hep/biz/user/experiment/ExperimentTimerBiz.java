package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.user.experiment.response.CountDownResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:实验:实验计时器
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class ExperimentTimerBiz{
    /**
    * @param
    * @return
    * @说明: 获取实验倒计时
    * @关联表: 
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public CountDownResponse countdown(String experimentInstanceId ) {
        return new CountDownResponse();
    }
}