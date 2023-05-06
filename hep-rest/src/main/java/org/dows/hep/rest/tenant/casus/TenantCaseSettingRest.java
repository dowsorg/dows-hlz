package org.dows.hep.rest.tenant.casus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.request.CaseSettingRequest;
import org.dows.hep.api.tenant.casus.response.CaseSettingResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseSettingBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:案例:案例问卷设置
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例问卷设置", description = "案例问卷设置")
public class TenantCaseSettingRest {
    private final TenantCaseSettingBiz tenantCaseSettingBiz;

    /**
    * 新增和更新案例问卷设置
    * @param
    * @return
    */
    @Operation(summary = "新增和更新案例问卷设置")
    @PostMapping("v1/tenantCasus/caseSetting/saveOrUpdCaseSetting")
    public Boolean saveOrUpdCaseSetting(@RequestBody @Validated CaseSettingRequest caseSetting ) {
        return tenantCaseSettingBiz.saveOrUpdCaseSetting(caseSetting);
    }

    /**
    * 获取案例问卷设置
    * @param
    * @return
    */
    @Operation(summary = "获取案例问卷设置")
    @GetMapping("v1/tenantCasus/caseSetting/getCaseSetting")
    public CaseSettingResponse getCaseSetting(@Validated String caseInstanceId) {
        return tenantCaseSettingBiz.getCaseSetting(caseInstanceId);
    }

    /**
    * 删除案例问卷设置
    * @param
    * @return
    */
    @Operation(summary = "删除案例问卷设置")
    @DeleteMapping("v1/tenantCasus/caseSetting/delCaseSetting")
    public Boolean delCaseSetting(@Validated String caseInstanceId ) {
        return tenantCaseSettingBiz.delCaseSetting(caseInstanceId);
    }


}