package org.dows.hep.rest.tenant.organization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.organization.request.*;
import org.dows.hep.biz.tenant.organization.OrgManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:机构操作:机构管理
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "机构管理", description = "机构管理")
public class OrgManageRest {
    private final OrgManageBiz orgManageBiz;

    /**
    * 保存机构基本信息
    * @param
    * @return
    */
    @Operation(summary = "保存机构基本信息")
    @PostMapping("v1/tenantOrganization/orgManage/addOrg")
    public String addOrg(@RequestBody @Validated AddOrgRequest addOrg ) {
        return orgManageBiz.addOrg(addOrg);
    }

    /**
    * 绑定机构功能
    * @param
    * @return
    */
    @Operation(summary = "绑定机构功能")
    @PostMapping("v1/tenantOrganization/orgManage/bindOrgFunc")
    public Boolean bindOrgFunc(@RequestBody @Validated OrgFuncRequest orgFunc ) {
        return orgManageBiz.bindOrgFunc(orgFunc);
    }

    /**
    * 设置机构相关费用
    * @param
    * @return
    */
    @Operation(summary = "设置机构相关费用")
    @PostMapping("v1/tenantOrganization/orgManage/settingOrgFee")
    public Boolean settingOrgFee(@RequestBody @Validated OrgFeeSettingRequest orgFeeSetting ) {
        return orgManageBiz.settingOrgFee(orgFeeSetting);
    }

    /**
    * 增加机构组员
    * @param
    * @return
    */
    @Operation(summary = "增加机构组员")
    @PostMapping("v1/tenantOrganization/orgManage/addMember")
    public Boolean addMember(@RequestBody @Validated AddOrgMemberRequest addOrgMember ) {
        return orgManageBiz.addMember(addOrgMember);
    }

    /**
    * 导出机构组员数据
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
    * @param
    * @return
    */
    @Operation(summary = "导入机构组员数据")
    @PostMapping("v1/tenantOrganization/orgManage/importOrgMember")
    public Boolean importOrgMember(@RequestBody @Validated MultipartFileRequest file ) {
        return orgManageBiz.importOrgMember(file);
    }

    /**
    * 下载模板
    * @param
    * @return
    */
    @Operation(summary = "下载模板")
    @GetMapping("v1/tenantOrganization/orgManage/dowsnloadTemplate")
    public void dowsnloadTemplate() {
        orgManageBiz.dowsnloadTemplate();
    }


}