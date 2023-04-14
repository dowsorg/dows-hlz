package org.dows.hep.rest.experiment.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.biz.experiment.user.ExperimentComputeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验分数计算
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "实验分数计算")
public class ExperimentComputeRest {
    private final ExperimentComputeBiz experimentComputeBiz;

    /**
    * 计算排名
    * @param
    * @return
    */
    @ApiOperation("计算排名")
    @PostMapping("v1/experimentUser/experimentCompute/computeRanking")
    public void computeRanking() {
        experimentComputeBiz.computeRanking();
    }

    /**
    * 获取排名
    * @param
    * @return
    */
    @ApiOperation("获取排名")
    @GetMapping("v1/experimentUser/experimentCompute/getRanks")
    public void getRanks() {
        experimentComputeBiz.getRanks();
    }


}