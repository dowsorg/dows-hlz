package org.dows.hep.rest.experiment.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.user.request.SaveOperateInteveneRequest;
import org.dows.hep.biz.experiment.user.ExperimentInterveneBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:健康干预
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "健康干预")
public class ExperimentInterveneRest {
    private final ExperimentInterveneBiz experimentInterveneBiz;

    /**
    * 保存干预记录
    * @param
    * @return
    */
    @ApiOperation("保存干预记录")
    @PostMapping("v1/experimentUser/experimentIntervene/saveOperateIntevene")
    public Boolean saveOperateIntevene(@RequestBody @Validated SaveOperateInteveneRequest saveOperateIntevene ) {
        return experimentInterveneBiz.saveOperateIntevene(saveOperateIntevene);
    }


}