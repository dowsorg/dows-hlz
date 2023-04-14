package org.dows.hep.rest.user.experiment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.user.experiment.request.SaveOperateInteveneRequest;
import org.dows.hep.biz.user.experiment.ExperimentInterveneBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:健康干预
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "健康干预", description = "健康干预")
public class ExperimentInterveneRest {
    private final ExperimentInterveneBiz experimentInterveneBiz;

    /**
    * 保存干预记录
    * @param
    * @return
    */
    @Operation(summary = "保存干预记录")
    @PostMapping("v1/userExperiment/experimentIntervene/saveOperateIntevene")
    public Boolean saveOperateIntevene(@RequestBody @Validated SaveOperateInteveneRequest saveOperateIntevene ) {
        return experimentInterveneBiz.saveOperateIntevene(saveOperateIntevene);
    }


}