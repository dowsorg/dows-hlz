package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.user.experiment.request.ExperimentPersonRequest;
import org.dows.hep.api.user.experiment.response.IntervalResponse;
import org.dows.hep.biz.user.person.PersonStatiscBiz;
import org.springframework.stereotype.Component;

/**
 * 实验间隔处理器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentIntervalHandler extends AbstractEventHandler implements EventHandler<IntervalResponse> {
    private final PersonStatiscBiz personStatiscBiz;

    @Override
    public void exec(IntervalResponse intervalResponse) {

        ExperimentPersonRequest experimentPersonRequest = ExperimentPersonRequest.builder()
                .experimentInstanceId(intervalResponse.getExperimentInstanceId())
                .appId(intervalResponse.getAppId())
                .periods(intervalResponse.getPeriod() - 1)// 计算上一期
                .build();
        // 一期结束保险返还
        personStatiscBiz.refundFunds(experimentPersonRequest);
    }
}
