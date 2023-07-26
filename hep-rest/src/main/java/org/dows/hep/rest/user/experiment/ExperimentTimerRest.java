package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.api.user.experiment.response.IntervalResponse;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.api.user.experiment.response.ExperimentStateResponse;
import org.dows.hep.biz.tenant.experiment.ExperimentManageBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public IntervalResponse countdown(String appId, String experimentInstanceId) {

        IntervalResponse intervalResponse = experimentTimerBiz.countdown(experimentInstanceId);
        return intervalResponse;
    }

    /**
     * 获取实验倒计时
     *
     * @param
     * @return
     */
    @Operation(summary = "获取实验进度条")
    @GetMapping("v1/tenantExperiment/experimentTimer/progress")
    public IntervalResponse progress(@RequestParam String experimentInstanceId) {
        IntervalResponse countdown = experimentTimerBiz.countdown(experimentInstanceId);
        return countdown;
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
        return experimentTimerBiz.getExperimentCurrentPeriods(appId, experimentInstanceId);
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

    /**
     * 获取实验期数
     *
     * @param
     * @return
     */
    @Operation(summary = "获取实验当前期数")
    @PostMapping("v1/userExperiment/experimentTimer/getExperimentPeriods")
    public void getExperimentPeriods(@RequestParam @Validated String appId,
                                     @RequestParam @Validated String experimentInstanceId) {
        experimentTimerBiz.getExperimentCurrentPeriods(appId,experimentInstanceId);
    }
}