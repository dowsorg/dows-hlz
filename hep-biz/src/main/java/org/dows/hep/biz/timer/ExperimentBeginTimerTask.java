package org.dows.hep.biz.timer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentSettingService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

/**
 * 实验开始任务
 * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为准备中
 * 触发实验暂停事件，并根据实验模式确定新增ExperimentTimer的暂停计时器的暂停开始时间
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentBeginTimerTask extends TimerTask {
    // 实验实例
    private final ExperimentInstanceService experimentInstanceService;
    // 实验设置
    private final ExperimentSettingService experimentSettingService;
    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验计时器
    private final ExperimentTimerService experimentTimerService;

    @Override
    public void run() {
        /**
         * todo
         * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为准备中
         * 触发实验暂停事件，并根据实验模式确定新增ExperimentTimer的暂停计时器的暂停开始时间
         */

    }
}
