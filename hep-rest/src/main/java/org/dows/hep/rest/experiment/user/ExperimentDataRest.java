package org.dows.hep.rest.experiment.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.user.request.OperateDataRequest;
import org.dows.hep.biz.experiment.user.ExperimentDataBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验数据
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:42
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "实验数据")
public class ExperimentDataRest {
    private final ExperimentDataBiz experimentDataBiz;

    /**
    * 保存操作数据
    * @param
    * @return
    */
    @ApiOperation("保存操作数据")
    @PostMapping("v1/experimentUser/experimentData/saveOperateData")
    public void saveOperateData(@RequestBody @Validated OperateDataRequest operateData ) {
        experimentDataBiz.saveOperateData(operateData);
    }


}