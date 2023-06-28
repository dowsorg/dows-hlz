package org.dows.hep.biz.timer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;

/**
 * ranking 排行/分数计算任务
 */
@Slf4j
@RequiredArgsConstructor
public class ExperimentRankingTimerTask implements Runnable {

    private final ExperimentTimerBiz experimentTimerBiz;
    private final String experimentInstanceId;
    @Override
    public void run() {

    }
}
