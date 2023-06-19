package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.api.tenant.experiment.request.ExperimentGroupSettingRequest;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class CreateGroupHandler extends AbstractEventHandler implements EventHandler<ExperimentGroupSettingRequest> {

    private static ConcurrentHashMap map = new ConcurrentHashMap();

    @Override
    public void exec(ExperimentGroupSettingRequest experimentGroupSettingRequest) {

        ExperimentContext experimentContext = new ExperimentContext();
        experimentContext.setExperimentId(experimentGroupSettingRequest.getExperimentInstanceId());
        experimentContext.setExperimentName(experimentGroupSettingRequest.getExperimentName());
        experimentContext.setState(ExperimentStateEnum.UNBEGIN);
        //设置小组个数
        experimentContext.setGroupCount(experimentGroupSettingRequest.getGroupSettings().size());
        ExperimentContext.set(experimentContext);

        log.info("分组计数....");
    }
}
