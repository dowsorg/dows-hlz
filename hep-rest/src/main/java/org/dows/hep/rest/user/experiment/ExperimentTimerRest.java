package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.response.CountDownResponse;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:实验:实验计时器
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验计时器", description = "实验计时器")
public class ExperimentTimerRest {
    private final ExperimentTimerBiz experimentTimerBiz;

    /**
    * 获取实验倒计时
    * @param
    * @return
    */
    @Operation(summary = "获取实验倒计时")
    @GetMapping("v1/userExperiment/experimentTimer/countdown")
    public CountDownResponse countdown(@Validated String experimentInstanceId) {
        return experimentTimerBiz.countdown(experimentInstanceId);
    }


}