package org.dows.hep.api.event;

import lombok.Data;

import java.io.Serializable;

/**
 * 分配实验事件
 */
public class AllotEvent extends ExperimentEvent implements Serializable {


    public AllotEvent(Object source) {
        super(source);
    }
}
