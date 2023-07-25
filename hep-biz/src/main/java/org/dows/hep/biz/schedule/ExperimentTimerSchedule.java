package org.dows.hep.biz.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.springframework.stereotype.Component;

/**
 * 实验计时器调度：
 * 每期结束时调度ws
 * 每期开始时调度ws
 * 实验结束时调度ws
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentTimerSchedule {

    private final ExperimentTimerBiz experimentTimerBiz;

    private final TaskScheduler taskScheduler;





}
