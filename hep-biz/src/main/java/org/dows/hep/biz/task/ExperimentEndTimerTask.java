package org.dows.hep.biz.task;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.biz.calc.ExperimentScoreCalculator;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTimerService;

import java.util.List;

/**
 * 实验结束任务
 * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为已结束
 * 计算总排行
 */
@Slf4j
@RequiredArgsConstructor
//@Component
public class ExperimentEndTimerTask implements Runnable {
    // 实验实例
    private final ExperimentInstanceService experimentInstanceService;
    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验计时器
    private final ExperimentTimerService experimentTimerService;
    //
    private final ExperimentScoreCalculator experimentScoreCalculator;

    private final String experimentInstanceId;

    @Override
    public void run() {
        /**
         * todo
         * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为准备中
         * 触发实验暂停事件，并根据实验模式确定新增ExperimentTimer的暂停计时器的暂停开始时间
         */
        ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .oneOpt()
                .orElse(null);
        if (experimentInstanceEntity == null) {
            throw new ExperimentException("不存在该实验!");
        }
        log.info("执行开始任务,查询到实验实例:{}", JSONUtil.toJsonStr(experimentInstanceEntity));

        //1、判断实验是否到时间，到时间则更新状态
        experimentInstanceService.lambdaUpdate()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
                .eq(ExperimentInstanceEntity::getDeleted, false)
                .set(ExperimentInstanceEntity::getState, EnumExperimentState.FINISH.getState())
                .update();

        experimentParticipatorService.lambdaUpdate()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
                .eq(ExperimentParticipatorEntity::getDeleted, false)
                .set(ExperimentParticipatorEntity::getState, EnumExperimentState.FINISH.getState())
                .update();
        experimentTimerService.lambdaUpdate()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
                .eq(ExperimentTimerEntity::getDeleted, false)
                .set(ExperimentTimerEntity::getState, EnumExperimentState.FINISH.getState())
                .update();

        //todo 计算总排行
        ExperimentScoreCalcRequest experimentScoreCalcRequest = new ExperimentScoreCalcRequest();
        experimentScoreCalcRequest.setExperimentInstanceId(experimentInstanceId);
        experimentScoreCalcRequest.setEnumCalcCodes(List.of(EnumCalcCode.hepTotalScoreCalculator));
        //todo 根据条件计算总排行
        experimentScoreCalculator.calc(experimentScoreCalcRequest);
    }
}
