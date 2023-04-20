package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.HRequest;
import org.dows.hep.api.user.experiment.request.ListExperimentEventRequest;
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
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验突发事件", description = "实验突发事件")
public class ExperimentEventRest {
    private final ExperimentEventBiz experimentEventBiz;

    /**
    * 
    * @param
    * @return
    */
    @Operation(summary = "")
    @PostMapping("v1/userExperiment/experimentEvent/list")
    public ExperimentEventResponse list(@RequestBody @Validated ListExperimentEventRequest listExperimentEvent ) {
        return experimentEventBiz.list(listExperimentEvent);
    }

    /**
    * 
    * @param
    * @return
    */
    @Operation(summary = "")
    @PostMapping("v1/userExperiment/experimentEvent/handlerEvent")
    public Boolean handlerEvent(@RequestBody @Validated HRequest h ) {
        return experimentEventBiz.handlerEvent(h);
    }


}