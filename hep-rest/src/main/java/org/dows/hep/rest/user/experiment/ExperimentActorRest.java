package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.AddActorRequest;
import org.dows.hep.biz.user.experiment.ExperimentActorBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:实验:实验扮演
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验扮演", description = "实验扮演")
public class ExperimentActorRest {
    private final ExperimentActorBiz experimentActorBiz;

    /**
    * 增加扮演者
    * @param
    * @return
    */
    @Operation(summary = "增加扮演者")
    @PostMapping("v1/userExperiment/experimentActor/addActor")
    public void addActor(@RequestBody @Validated AddActorRequest addActor ) {
        experimentActorBiz.addActor(addActor);
    }


}