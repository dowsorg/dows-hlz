package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.user.experiment.ExperimentComputeBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:实验:实验分数计算
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
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