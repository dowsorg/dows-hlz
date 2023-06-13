package org.dows.hep.biz.user.experiment;

import lombok.RequiredArgsConstructor;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.user.experiment.response.CountDownResponse;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.stereotype.Service;

/**
* @description project descr:实验:实验计时器
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@Service
public class ExperimentTimerBiz{

    private final ExperimentTimerService experimentTimerService;


    /**
    * @param
    * @return
    * @说明: 获取实验倒计时
    * @关联表: 
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public CountDownResponse countdown(String experimentInstanceId ) {

        ExperimentTimerEntity experimentTimerEntity = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .oneOpt()
                .orElse(null);

        if(experimentTimerEntity == null){

        }
        return BeanConvert.beanConvert(experimentTimerEntity,CountDownResponse.class);
    }
}