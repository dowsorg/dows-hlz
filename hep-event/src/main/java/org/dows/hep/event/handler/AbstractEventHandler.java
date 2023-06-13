package org.dows.hep.event.handler;

import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractEventHandler {
    @Autowired
    private ExperimentTimerBiz experimentTimerBiz;
}
