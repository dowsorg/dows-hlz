package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.api.user.experiment.response.CountDownResponse;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.api.user.experiment.response.ExperimentStateResponse;
import org.dows.hep.biz.tenant.experiment.ExperimentManageBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * @author lait.zhang
 * @description project descr:实验:实验计时器
 * @date 2023年4月23日 上午9:44:34
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "实验计时器", description = "实验计时器")
public class ExperimentTimerRest {
    private final ExperimentTimerBiz experimentTimerBiz;
    private final ExperimentManageBiz experimentManageBiz;

    /**
     * 获取实验倒计时
     *
     * @param
     * @return
     */
    @Operation(summary = "获取实验倒计时")
    @GetMapping("v1/userExperiment/experimentTimer/countdown")
    public CountDownResponse countdown(String appId, String experimentInstanceId) {

        CountDownResponse countDownResponse = new CountDownResponse();
        ExperimentPeriodsResonse experimentPeriods = experimentTimerBiz.getExperimentPeriods(appId, experimentInstanceId);
        List<ExperimentPeriodsResonse.ExperimentPeriods> experimentPeriods1 = experimentPeriods.getExperimentPeriods();
        Integer currentPeriod = experimentPeriods.getCurrentPeriod();
        ExperimentPeriodsResonse.ExperimentPeriods experimentPeriods2 = experimentPeriods1.stream()
                .filter(e -> e.getPeriod() == currentPeriod)
                .max(Comparator.comparingInt(ExperimentPeriodsResonse.ExperimentPeriods::getPauseCount))
                .orElse(null);

        if (experimentPeriods2 != null) {
            countDownResponse.setSandTime(experimentPeriods2.getStartTime() - System.currentTimeMillis() + experimentPeriods2.getPeriodInterval());
        } /*else {
            throw new ExperimentException("期数异常");
        }*/
        return countDownResponse;
    }


    /**
     * 获取实验每期时间
     *
     * @param
     * @return
     */
    @Operation(summary = "获取当前实验期数信息[每期开始，结束，间隔等]及当前所在期数")
    @GetMapping("v1/userExperiment/experimentTimer/periods")
    public ExperimentPeriodsResonse periods(String appId, String experimentInstanceId) {
        return experimentTimerBiz.getExperimentPeriods(appId, experimentInstanceId);
    }

    /**
     * 获取实验每期时间
     *
     * @param
     * @return
     */
    @Operation(summary = "获取当前实验状态")
    @GetMapping("v1/userExperiment/experimentTimer/getExperimentState")
    public ExperimentStateResponse getExperimentStarted(String appId, String experimentInstanceId) {
        return experimentManageBiz.getExperimentState(appId, experimentInstanceId);
    }

    /**
     * 获取实验列表
     *
     * @param
     * @return
     */
    @Operation(summary = "开始/暂停实验")
    @PostMapping("v1/userExperiment/experimentTimer/restart")
    public void restart(@RequestBody @Validated ExperimentRestartRequest experimentRestartRequest) {
        experimentManageBiz.restart(experimentRestartRequest);
    }


}