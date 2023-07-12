package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CaseCreateCopyToPersonRequestRs;
import org.dows.hep.api.base.indicator.response.CaseIndicatorInstanceCategoryResponseRs;
import org.dows.hep.api.tenant.casus.request.UpdateIndicatorValueRequest;
import org.dows.hep.biz.base.indicator.CaseIndicatorInstanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "案例指标实例", description = "案例指标实例")
public class CaseIndicatorInstanceRest {
    private final CaseIndicatorInstanceBiz caseIndicatorInstanceBiz;

    @Operation(summary = "查询健康指数")
    @GetMapping("v1/caseIndicator/healthPoint/get")
    public String getHealthPoint(@RequestParam String casePersonId) {
        return caseIndicatorInstanceBiz.getHealthPoint(casePersonId);
    }

    @Operation(summary = "复制数据库指标管理给人物")
    @PostMapping("v1/caseIndicator/indicatorInstance/copy")
    public void copyPersonIndicatorInstance(@RequestBody CaseCreateCopyToPersonRequestRs caseCreateCopyToPersonRequestRs) {
        caseIndicatorInstanceBiz.copyPersonIndicatorInstance(caseCreateCopyToPersonRequestRs);
    }

    @Operation(summary = "根据人物id和appId查询出所有的指标")
    @GetMapping("v1/caseIndicator/indicatorInstance/getByPersonIdAndAppId")
    public List<CaseIndicatorInstanceCategoryResponseRs> getByPersonIdAndAppId(
            @RequestParam String personId,
            @RequestParam String appId) {
        return caseIndicatorInstanceBiz.getByPersonIdAndAppId(personId, appId);
    }

    @Operation(summary = "更新NPC人物指标的值或默认值或描述")
    @PutMapping("v1/caseIndicator/indicatorInstance/updateNpcIndicatorValue")
    public boolean updateNpcIndicatorValue(@RequestBody @Validated UpdateIndicatorValueRequest updateIndicatorValueRequest) {
        return caseIndicatorInstanceBiz.updateNpcIndicatorValue(updateIndicatorValueRequest);
    }


}
