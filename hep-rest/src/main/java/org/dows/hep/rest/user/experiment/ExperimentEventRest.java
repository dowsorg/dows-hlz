package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.FindExperimentEventRequest;
import org.dows.hep.api.user.experiment.request.HandlerEventRequest;
import org.dows.hep.api.user.experiment.response.ExperimentEventResponse;
import org.dows.hep.biz.user.experiment.ExperimentEventBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:实验:实验突发事件
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验突发事件", description = "实验突发事件")
public class ExperimentEventRest {
    private final ExperimentEventBiz experimentEventBiz;

    /**
    * 获取实验突发事件列表
    * @param
    * @return
    */
    @Operation(summary = "获取实验突发事件列表")
    @PostMapping("v1/userExperiment/experimentEvent/pageExperimentEvent")
    public ExperimentEventResponse pageExperimentEvent(@RequestBody @Validated FindExperimentEventRequest findExperimentEvent ) {
        return experimentEventBiz.pageExperimentEvent(findExperimentEvent);
    }

    /**
    * 实验突发事件处理
    * @param
    * @return
    */
    @Operation(summary = "实验突发事件处理")
    @PostMapping("v1/userExperiment/experimentEvent/handlerEvent")
    public Boolean handlerEvent(@RequestBody @Validated HandlerEventRequest handlerEvent ) {
        return experimentEventBiz.handlerEvent(handlerEvent);
    }


}