package org.dows.hep.event;

import java.io.Serializable;
import java.time.Clock;

/**
 * todo
 * 1.实验结束事件,触发算法计算，针对小组独立计算
 * 2.汇总，排名计算等
 */
public class FinishEvent extends ExperimentEvent implements Serializable {
    public FinishEvent(Object source) {
        super(source);
    }

    public FinishEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
