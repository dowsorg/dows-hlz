package org.dows.hep.rest.base.risk;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.risk.request.CrowdsInstanceRequest;
import org.dows.hep.biz.base.risk.CrowdsInstanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jx
 * @date 2023/6/15 14:00
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "人群类别", description = "人群类别")
public class CrowsInstanceRest {
    private final CrowdsInstanceBiz crowdsInstanceBiz;

    @Operation(summary = "新增或修改人群类别")
    @PostMapping("v1/baseRisk/crowds/insertOrUpdateCrows")
    public void insertOrUpdateCrows(@RequestBody @Validated CrowdsInstanceRequest crowdsInstanceRequest){
        crowdsInstanceBiz.insertOrUpdateCrows(crowdsInstanceRequest);
    }
}
