package org.dows.hep.rest.user.experiment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.biz.user.experiment.ExperimentComputeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验分数计算
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验分数计算", description = "实验分数计算")
public class ExperimentComputeRest {
    private final ExperimentComputeBiz experimentComputeBiz;

    /**
    * 计算排名
    * @param
    * @return
    */
    @Operation(summary = "计算排名")
    @PostMapping("v1/userExperiment/experimentCompute/computeRanking")
    public void computeRanking() {
        experimentComputeBiz.computeRanking();
    }

    /**
    * 获取排名
    * @param
    * @return
    */
    @Operation(summary = "获取排名")
    @GetMapping("v1/userExperiment/experimentCompute/getRanks")
    public void getRanks() {
        experimentComputeBiz.getRanks();
    }


}