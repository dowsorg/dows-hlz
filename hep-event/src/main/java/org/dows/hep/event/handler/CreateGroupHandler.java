package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.tenant.experiment.request.ExperimentGroupSettingRequest;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CreateGroupHandler extends AbstractEventHandler implements EventHandler<ExperimentGroupSettingRequest> {

    @Override
    public void exec(ExperimentGroupSettingRequest experimentGroupSettingRequest) {

//        ExperimentContext experimentContext = ExperimentContext.getExperimentContext(experimentGroupSettingRequest.getExperimentInstanceId());
//
//        List<ExperimentContext.ExperimentGroup> experimentGroups = new ArrayList<>();
//        ExperimentContext.ExperimentGroup experimentGroup = new  ExperimentContext.ExperimentGroup();
//        //todo experimentGroupSettingRequest 转换 experimentGroup
//        experimentGroups.add(experimentGroup);
//        experimentContext.setExperimentGroups(experimentGroups);
        //todo 定时器
        log.info("开启调度....");

    }
}
