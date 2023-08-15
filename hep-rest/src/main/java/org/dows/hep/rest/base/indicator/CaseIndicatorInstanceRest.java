package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.annotation.Resubmit;
import org.dows.hep.api.base.indicator.request.CaseCreateCopyToPersonRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateCaseIndicatorInstanceRequestRs;
import org.dows.hep.api.base.indicator.request.RsCaseGetCoreRequest;
import org.dows.hep.api.base.indicator.response.CaseIndicatorInstanceCategoryResponseRs;
import org.dows.hep.api.tenant.casus.request.UpdateIndicatorValueRequest;
import org.dows.hep.biz.base.indicator.CaseIndicatorInstanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "案例管理", description = "案例管理")
public class CaseIndicatorInstanceRest {
    private final CaseIndicatorInstanceBiz caseIndicatorInstanceBiz;

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

    @Resubmit(duration = 5)
    @Operation(summary = "创建或修改指标实例")
    @PostMapping("v1/caseIndicator/indicatorInstance/createOrUpdateRs")
    public void createOrUpdateRs(@RequestBody CreateOrUpdateCaseIndicatorInstanceRequestRs createOrUpdateCaseIndicatorInstanceRequestRs) throws InterruptedException {
        caseIndicatorInstanceBiz.createOrUpdateRs(createOrUpdateCaseIndicatorInstanceRequestRs);
    }

    @Operation(summary = "删除指标")
    @DeleteMapping("v1/caseIndicator/indicatorInstance/delete")
    public void delete(@RequestParam String caseIndicatorInstanceId) throws InterruptedException, ExecutionException {
        caseIndicatorInstanceBiz.delete(caseIndicatorInstanceId);
    }

    @Operation(summary = "查询健康指数")
    @GetMapping("v1/caseIndicator/healthPoint/get")
    public String getHealthPoint(@RequestParam String casePersonId) {
        return caseIndicatorInstanceBiz.getHealthPoint(casePersonId);
    }

    @Operation(summary = "新版查询健康指数")
    @GetMapping("v2/caseIndicator/healthPoint/get")
    public String v2GetHealthPoint(@RequestParam String accountId) {
        return caseIndicatorInstanceBiz.v2GetHealthPoint(accountId);
    }

    @Operation(summary = "复制数据库指标管理给人物")
    @PostMapping("v1/caseIndicator/indicatorInstance/copy")
    public void copyPersonIndicatorInstance(@RequestBody CaseCreateCopyToPersonRequestRs caseCreateCopyToPersonRequestRs) {
        caseIndicatorInstanceBiz.v1OldCopyPersonIndicatorInstance(caseCreateCopyToPersonRequestRs);
    }

    @Operation(summary = "V2复制数据库指标管理给人物")
    @PostMapping("v2/caseIndicator/indicatorInstance/copy")
    public void v2CopyPersonIndicatorInstance(@RequestBody CaseCreateCopyToPersonRequestRs caseCreateCopyToPersonRequestRs) throws ExecutionException, InterruptedException {
        caseIndicatorInstanceBiz.copyPersonIndicatorInstance(caseCreateCopyToPersonRequestRs);
    }

    @Operation(summary = "获取人物的关键指标")
    @PostMapping("v1/caseIndicator/indicatorInstance/core/get")
    public Map<String, List<String>> getCoreByAccountIdList(@RequestBody RsCaseGetCoreRequest rsCaseGetCoreRequest) {
        List<String> accountIdList = rsCaseGetCoreRequest.getAccountIdList();
        return caseIndicatorInstanceBiz.getCoreByAccountIdList(accountIdList);
    }
}
