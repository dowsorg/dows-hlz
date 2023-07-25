package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.user.experiment.request.ExperimentPersonRequest;
import org.dows.hep.biz.user.person.PersonStatiscBiz;
import org.springframework.stereotype.Component;

/**
 * @author jx
 * @date 2023/7/25 19:18
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class FeeReimburseHandler extends AbstractEventHandler implements EventHandler<ExperimentPersonRequest> {
    private final PersonStatiscBiz personStatiscBiz;

    @Override
    public void exec(ExperimentPersonRequest request) {
        personStatiscBiz.refundFunds(request);
    }
}
