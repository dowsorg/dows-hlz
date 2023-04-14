package org.dows.hep.biz.experiment.user;

import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.user.response.CountDownResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:实验:实验计时器
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
public class ExperimentTimerBiz{
    /**
    * @param
    * @return
    * @说明: 获取实验倒计时
    * @关联表: 
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public CountDownResponse countdown(String experimentInstanceId ) {
        return new CountDownResponse();
    }
}