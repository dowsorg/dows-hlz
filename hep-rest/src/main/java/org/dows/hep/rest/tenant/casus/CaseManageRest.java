package org.dows.hep.rest.tenant.casus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.tenant.casus.request.CaseInstanceRequest;
import org.dows.hep.api.tenant.casus.request.CaseInstancePageRequest;
import org.dows.hep.api.tenant.casus.response.CaseInstanceResponse;
import org.dows.hep.api.tenant.casus.response.CaseInstanceResponse;
import org.dows.hep.biz.tenant.casus.CaseManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案例:案例管理
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例管理", description = "案例管理")
public class CaseManageRest {
    private final CaseManageBiz caseManageBiz;

    /**
    * 创建和更新案例
    * @param
    * @return
    */
    @Operation(summary = "创建和更新案例")
    @PostMapping("v1/tenantCasus/caseManage/saveOrUpdCaseInstance")
    public String saveOrUpdCaseInstance(@RequestBody @Validated CaseInstanceRequest caseInstance ) {
        return caseManageBiz.saveOrUpdCaseInstance(caseInstance);
    }

    /**
    * 复制
    * @param
    * @return
    */
    @Operation(summary = "复制")
    @PostMapping("v1/tenantCasus/caseManage/copyCaseInstance")
    public String copyCaseInstance(@RequestBody @Validated String oriCaseInstanceId ) {
        return caseManageBiz.copyCaseInstance(oriCaseInstanceId);
    }

    /**
    * 列表
    * @param
    * @return
    */
    @Operation(summary = "列表")
    @PostMapping("v1/tenantCasus/caseManage/pageCaseInstance")
    public CaseInstanceResponse pageCaseInstance(@RequestBody @Validated CaseInstancePageRequest caseInstancePage ) {
        return caseManageBiz.pageCaseInstance(caseInstancePage);
    }

    /**
    * 获取案例详情
    * @param
    * @return
    */
    @Operation(summary = "获取案例详情")
    @GetMapping("v1/tenantCasus/caseManage/getCaseInstance")
    public CaseInstanceResponse getCaseInstance(@Validated String caseInstanceId) {
        return caseManageBiz.getCaseInstance(caseInstanceId);
    }

    /**
    * 删除
    * @param
    * @return
    */
    @Operation(summary = "删除")
    @DeleteMapping("v1/tenantCasus/caseManage/delCaseInstance")
    public Boolean delCaseInstance(@Validated String caseInstanceId ) {
        return caseManageBiz.delCaseInstance(caseInstanceId);
    }


}