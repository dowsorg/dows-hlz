package org.dows.hep.event.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.event.EventName;
import org.springframework.stereotype.Component;

/**
 * todo
 * 1.实验结束事件,触发算法计算，针对小组独立计算
 * 2.汇总，排名计算等
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class FinishHandler extends AbstractEventHandler implements EventHandler {



}
