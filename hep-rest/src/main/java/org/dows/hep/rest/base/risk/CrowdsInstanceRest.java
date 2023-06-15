package org.dows.hep.rest.base.risk;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.crud.api.model.PageResponse;
import org.dows.hep.api.base.risk.request.CrowdsInstanceRequest;
import org.dows.hep.api.base.risk.request.PageCrowdsRequest;
import org.dows.hep.api.base.risk.response.CrowdsInstanceResponse;
import org.dows.hep.biz.base.risk.CrowdsInstanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author jx
 * @date 2023/6/15 14:00
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "人群类别", description = "人群类别")
public class CrowdsInstanceRest {
    private final CrowdsInstanceBiz crowdsInstanceBiz;

    @Operation(summary = "新增或修改人群类别")
    @PostMapping("v1/baseRisk/crowds/insertOrUpdateCrows")
    public void insertOrUpdateCrows(@RequestBody @Validated CrowdsInstanceRequest crowdsInstanceRequest){
        crowdsInstanceBiz.insertOrUpdateCrows(crowdsInstanceRequest);
    }

    @Operation(summary = "分页获取人群类别")
    @GetMapping("v1/baseRisk/crowds/page")
    public PageResponse<CrowdsInstanceResponse> page(PageCrowdsRequest pageCrowdsRequest){
        return crowdsInstanceBiz.page(pageCrowdsRequest);
    }

    @Operation(summary = "查询人群类别")
    @GetMapping("v1/baseRisk/crowds/getCrowdsByCrowdsId/{crowdsId}")
    public CrowdsInstanceResponse getCrowdsByCrowdsId(@PathVariable @Validated String crowdsId){
        return crowdsInstanceBiz.getCrowdsByCrowdsId(crowdsId);
    }

    @Operation(summary = "删除人群类别")
    @DeleteMapping("v1/baseRisk/crowds/batchDelCrowds")
    public Boolean batchDelCrowds(@RequestBody @Validated Set<String> crowdsIds){
        return crowdsInstanceBiz.batchDelCrowds(crowdsIds);
    }
}
