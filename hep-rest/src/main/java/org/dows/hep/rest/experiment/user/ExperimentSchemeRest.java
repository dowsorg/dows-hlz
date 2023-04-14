package org.dows.hep.rest.experiment.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.user.request.DesignSchemeRequest;
import org.dows.hep.biz.experiment.user.ExperimentSchemeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验方案
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:42
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "实验方案")
public class ExperimentSchemeRest {
    private final ExperimentSchemeBiz experimentSchemeBiz;

    /**
    * 设计实验方案
    * @param
    * @return
    */
    @ApiOperation("设计实验方案")
    @PostMapping("v1/experimentUser/experimentScheme/designScheme")
    public Boolean designScheme(@RequestBody @Validated DesignSchemeRequest designScheme ) {
        return experimentSchemeBiz.designScheme(designScheme);
    }

    /**
    * 提交方案
    * @param
    * @return
    */
    @ApiOperation("提交方案")
    @PostMapping("v1/experimentUser/experimentScheme/submitScheme")
    public Boolean submitScheme(@RequestBody @Validated String schemeId ) {
        return experimentSchemeBiz.submitScheme(schemeId);
    }


}