package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.InterveneInfoResponse;
import org.dows.hep.api.user.experiment.response.InterveneResponse;
import org.dows.hep.biz.user.experiment.ExperimentInterveneBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @description project descr:实验:健康干预
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "健康干预", description = "健康干预")
public class ExperimentInterveneRest {
    private final ExperimentInterveneBiz experimentInterveneBiz;

    /**
    * 保存饮食干预记录
    * @param
    * @return
    */
    @Operation(summary = "保存饮食干预记录")
    @PostMapping("v1/userExperiment/experimentIntervene/saveInterveneFood")
    public Boolean saveInterveneFood(@RequestBody @Validated SaveInterveneFoodRequest saveInterveneFood ) {
        return experimentInterveneBiz.saveInterveneFood(saveInterveneFood);
    }

    /**
    * 保存运动干预记录
    * @param
    * @return
    */
    @Operation(summary = "保存运动干预记录")
    @PostMapping("v1/userExperiment/experimentIntervene/saveInterveneSport")
    public Boolean saveInterveneSport(@RequestBody @Validated SaveInterveneSportRequest saveInterveneSport ) {
        return experimentInterveneBiz.saveInterveneSport(saveInterveneSport);
    }

    /**
    * 保存治疗干预记录
    * @param
    * @return
    */
    @Operation(summary = "保存治疗干预记录")
    @PostMapping("v1/userExperiment/experimentIntervene/saveInterveneTreat")
    public Boolean saveInterveneTreat(@RequestBody @Validated SaveInterveneTreatRequest saveInterveneTreat ) {
        return experimentInterveneBiz.saveInterveneTreat(saveInterveneTreat);
    }

    /**
    * 获取干预记录列表
    * @param
    * @return
    */
    @Operation(summary = "获取干预记录列表")
    @PostMapping("v1/userExperiment/experimentIntervene/listIntervene")
    public List<InterveneResponse> listIntervene(@RequestBody @Validated ListInterveneRequest listIntervene ) {
        return experimentInterveneBiz.listIntervene(listIntervene);
    }

    /**
    * 获取干预记录信息
    * @param
    * @return
    */
    @Operation(summary = "获取干预记录信息")
    @GetMapping("v1/userExperiment/experimentIntervene/getIntervene")
    public InterveneInfoResponse getIntervene(@Validated GetInterveneRequest getIntervene) {
        return experimentInterveneBiz.getIntervene(getIntervene);
    }


}