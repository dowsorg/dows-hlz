package org.dows.hep.rest.tenant.organization;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.hep.api.tenant.excel.BatchMemberInsertRequest;
import org.dows.hep.api.tenant.organization.request.AddOrgMemberRequest;
import org.dows.hep.api.tenant.organization.request.AddOrgRequest;
import org.dows.hep.api.tenant.organization.request.OrgFeeSettingRequest;
import org.dows.hep.api.tenant.organization.request.OrgFuncRequest;
import org.dows.hep.biz.base.org.OrgBiz;
import org.dows.hep.biz.base.person.PersonManageBiz;
import org.dows.hep.biz.tenant.excel.BatchInsertBiz;
import org.dows.hep.biz.tenant.organization.OrgManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lait.zhang
 * @description project descr:机构操作:机构管理
 * @date 2023年4月23日 上午9:44:34
 */
@Tag(name = "机构管理", description = "机构管理")
@RequiredArgsConstructor
@RestController
public class OrgManageRest {
    private final OrgManageBiz orgManageBiz;

    /**
     * 保存机构基本信息
     *
     * @param
     * @return
     */
    @Operation(summary = "保存机构基本信息")
    @PostMapping("v1/tenantOrganization/orgManage/addOrg")
    public String addOrg(@RequestBody @Validated AddOrgRequest addOrg) {
        return orgManageBiz.addOrg(addOrg);
    }

    /**
     * 绑定机构功能
     *
     * @param
     * @return
     */
    @Operation(summary = "绑定机构功能")
    @PostMapping("v1/tenantOrganization/orgManage/bindOrgFunc")
    public Boolean bindOrgFunc(@RequestBody @Validated OrgFuncRequest orgFunc) {
        return orgManageBiz.bindOrgFunc(orgFunc);
    }

    /**
     * 设置机构相关费用
     *
     * @param
     * @return
     */
    @Operation(summary = "设置机构相关费用")
    @PostMapping("v1/tenantOrganization/orgManage/settingOrgFee")
    public Boolean settingOrgFee(@RequestBody @Validated OrgFeeSettingRequest orgFeeSetting) {
        return orgManageBiz.settingOrgFee(orgFeeSetting);
    }

    /**
     * 增加机构组员
     *
     * @param
     * @return
     */
    @Operation(summary = "增加机构组员")
    @PostMapping("v1/tenantOrganization/orgManage/addMember")
    public Boolean addMember(@RequestBody @Validated AddOrgMemberRequest addOrgMember) {
        return orgManageBiz.addMember(addOrgMember);
    }

    /**
     * 导出机构组员数据
     *
     * @param
     * @return
     */
    @Operation(summary = "导出机构组员数据")
    @PostMapping("v1/tenantOrganization/orgManage/exportMember")
    public void exportMember() {
        orgManageBiz.exportMember();
    }

    /**
     * 导入机构组员数据
     *
     * @param
     * @return
     */
    @Operation(summary = "导入机构组员数据")
    @PostMapping("v1/tenantOrganization/orgManage/importOrgMember")
    public Map<String, Object> importOrgMember(@RequestParam(value = "file") MultipartFile file,
                                               @RequestParam(value = "rbacRoleId") Long rbacRoleId,
                                               @RequestParam(value = "status") Integer status,
                                               @RequestParam(value = "principalType") Integer principalType,
                                               @RequestParam(value = "accountOrgOrgId") String accountOrgOrgId
    ) {
        //1、构建账户实例
        AccountInstanceRequest request = AccountInstanceRequest.builder()
                .appId("3")
                .rbacRoleId(rbacRoleId)
                .status(status)
                .principalType(principalType)
                .accountOrgOrgId(accountOrgOrgId)
                .build();
        //2、导入excel
        return orgManageBiz.importOrgMember(request,file);
    }

    /**
     * 下载模板
     *
     * @param
     * @return
     */
    @Operation(summary = "下载模板")
    @GetMapping("v1/tenantOrganization/orgManage/dowsnloadTemplate")
    public void dowsnloadTemplate() {
        orgManageBiz.dowsnloadTemplate();
    }
}