package org.dows.hep.rest.experiment.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.user.response.CountDownResponse;
import org.dows.hep.biz.experiment.user.ExperimentTimerBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验计时器
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:42
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "实验计时器")
public class ExperimentTimerRest {
    private final ExperimentTimerBiz experimentTimerBiz;

    /**
    * 获取实验倒计时
    * @param
    * @return
    */
    @ApiOperation("获取实验倒计时")
    @GetMapping("v1/experimentUser/experimentTimer/countdown")
    public CountDownResponse countdown(@Validated String experimentInstanceId) {
        return experimentTimerBiz.countdown(experimentInstanceId);
    }


}