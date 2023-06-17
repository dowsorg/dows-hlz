package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.user.experiment.request.ExperimentParticipatorRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 小组成员分配机构处理器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class GroupMemberAllotHandler extends AbstractEventHandler implements EventHandler<List<ExperimentParticipatorRequest>> {

    private int groupSize = 0;

    private AtomicInteger atomicInteger = new AtomicInteger(0);
    @Override
    public void exec(List<ExperimentParticipatorRequest> experimentParticipatorRequests) {
        // todo 从ExperimentContext 取该实验的人员信息
        int amount = atomicInteger.incrementAndGet();
        if(groupSize !=0) {
            // 先计数
            ExperimentContext experimentContext = ExperimentContext.getExperimentContext("实验id");
            groupSize = experimentContext.getExperimentGroups().size();
        }
        if(amount == groupSize){
            //todo 定时器
            log.info("开启调度....");
        }


    }
}
