package org.dows.hep.rest.tenant.casus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.tenant.casus.request.CaseSettingRequest;
import org.dows.hep.api.tenant.casus.response.CaseSettingResponse;
import org.dows.hep.biz.tenant.casus.CaseSettingBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案例:案例问卷设置
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例问卷设置", description = "案例问卷设置")
public class CaseSettingRest {
    private final CaseSettingBiz caseSettingBiz;

    /**
    * 新增和更新案例问卷设置
    * @param
    * @return
    */
    @Operation(summary = "新增和更新案例问卷设置")
    @PostMapping("v1/tenantCasus/caseSetting/saveOrUpdCaseSetting")
    public Boolean saveOrUpdCaseSetting(@RequestBody @Validated CaseSettingRequest caseSetting ) {
        return caseSettingBiz.saveOrUpdCaseSetting(caseSetting);
    }

    /**
    * 获取案例问卷设置
    * @param
    * @return
    */
    @Operation(summary = "获取案例问卷设置")
    @GetMapping("v1/tenantCasus/caseSetting/getCaseSetting")
    public CaseSettingResponse getCaseSetting(@Validated String caseInstanceId) {
        return caseSettingBiz.getCaseSetting(caseInstanceId);
    }

    /**
    * 删除案例问卷设置
    * @param
    * @return
    */
    @Operation(summary = "删除案例问卷设置")
    @DeleteMapping("v1/tenantCasus/caseSetting/delCaseSetting")
    public Boolean delCaseSetting(@Validated String caseInstanceId ) {
        return caseSettingBiz.delCaseSetting(caseInstanceId);
    }


}