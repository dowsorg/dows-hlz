package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.tenant.experiment.request.CreateExperimentRequest;
import org.dows.hep.biz.tenant.experiment.ExperimentManageBiz;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @author jx
 * @date 2023/6/21 11:30
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CopyExperimentPersonAndOrgHandler extends AbstractEventHandler implements EventHandler<List<CreateExperimentRequest>> {
    private final ExperimentManageBiz experimentManageBiz;

    @Override
    public void exec(List<CreateExperimentRequest> createExperimentRequest) {
        experimentManageBiz.copyExperimentPersonAndOrg(createExperimentRequest);
    }
}
