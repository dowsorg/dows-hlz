package org.dows.hep.event.handler;

import org.dows.hep.biz.calc.ExperimentScoreCalculator;
import org.dows.hep.biz.task.ExperimentTaskScheduler;
import org.dows.hep.biz.user.experiment.ExperimentInsuranceBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTimerService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractEventHandler {
    @Autowired
    protected ExperimentInsuranceBiz experimentInsuranceBiz;
    @Autowired
    protected ExperimentTimerBiz experimentTimerBiz;
    @Autowired
    protected ExperimentTaskScheduler experimentTaskScheduler;
    @Autowired
    protected IdGenerator idGenerator;



    @Autowired
    // 实验实例
    protected  ExperimentInstanceService experimentInstanceService;
    @Autowired
    // 实验参与者
    protected  ExperimentParticipatorService experimentParticipatorService;
    @Autowired
    // 实验计时器
    protected  ExperimentTimerService experimentTimerService;
    @Autowired
    protected ExperimentScoreCalculator experimentScoreCalculator;
}
