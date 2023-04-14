package org.dows.hep.rest.user.experiment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.user.experiment.request.OperateDataRequest;
import org.dows.hep.biz.user.experiment.ExperimentDataBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验数据
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验数据", description = "实验数据")
public class ExperimentDataRest {
    private final ExperimentDataBiz experimentDataBiz;

    /**
    * 保存操作数据
    * @param
    * @return
    */
    @Operation(summary = "保存操作数据")
    @PostMapping("v1/userExperiment/experimentData/saveOperateData")
    public void saveOperateData(@RequestBody @Validated OperateDataRequest operateData ) {
        experimentDataBiz.saveOperateData(operateData);
    }


}