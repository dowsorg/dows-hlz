package org.dows.hep.biz.user.experiment;

import lombok.RequiredArgsConstructor;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.api.user.experiment.response.CountDownResponse;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:实验:实验计时器
 * @date 2023年4月23日 上午9:44:34
 */
@RequiredArgsConstructor
@Service
public class ExperimentTimerBiz {

    private final ExperimentTimerService experimentTimerService;
    private final ExperimentInstanceService experimentInstanceService;


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
    public CountDownResponse countdown(String experimentInstanceId) {

        ExperimentTimerEntity experimentTimerEntity = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .oneOpt()
                .orElse(null);

        if (experimentTimerEntity == null) {

        }
        return BeanConvert.beanConvert(experimentTimerEntity, CountDownResponse.class);
    }


    /**
     * 获取当前实验期数定时器
     *
     * @param experimentRestartRequest
     * @return
     */
    public List<ExperimentTimerEntity> getCurrentPeriods(ExperimentRestartRequest experimentRestartRequest) {
        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentRestartRequest.getExperimentInstanceId())
                .eq(ExperimentTimerEntity::getAppId, experimentRestartRequest.getAppId())
                .list();
        return list;
    }


    /**
     * 更新定时器
     *
     * @param updateExperimentTimerEntities
     * @return
     */
    public boolean saveOrUpdateBatch(List<ExperimentTimerEntity> updateExperimentTimerEntities) {
        boolean b = experimentTimerService.saveOrUpdateBatch(updateExperimentTimerEntities);
        return b;
    }


    /**
     * 获取当前实验期数间隔
     *
     * @param appId
     * @param experimentInstanceId
     * @return
     */
    public List<ExperimentTimerEntity> getExperimentPeriods(String appId, String experimentInstanceId) {
        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentTimerEntity::getAppId, appId)
                //.eq(ExperimentTimerEntity::getModel, 2) // 沙盘模式
                .ne(ExperimentTimerEntity::getState, ExperimentStateEnum.FINISH.getState())
                .list();

        List<ExperimentTimerEntity> collect = list.stream().filter(t -> t.getPauseCount() == 0).collect(Collectors.toList());
        return collect;
    }


}