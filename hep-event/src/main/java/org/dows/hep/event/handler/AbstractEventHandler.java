package org.dows.hep.event.handler;

import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.sequence.api.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractEventHandler {
    @Autowired
    protected ExperimentTimerBiz experimentTimerBiz;
    @Autowired
    protected IdGenerator idGenerator;
}
