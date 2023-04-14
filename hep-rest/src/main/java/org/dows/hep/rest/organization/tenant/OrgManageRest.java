package org.dows.hep.rest.organization.tenant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.organization.tenant.request.AddOrgRequest;
import org.dows.hep.api.organization.tenant.request.OrgFuncRequest;
import org.dows.hep.api.organization.tenant.request.OrgFeeSettingRequest;
import org.dows.hep.api.organization.tenant.request.AddOrgMemberRequest;
import org.dows.hep.api.organization.tenant.request.MultipartFileRequest;
import org.dows.hep.biz.organization.tenant.OrgManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:机构操作:机构管理
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "机构管理")
public class OrgManageRest {
    private final OrgManageBiz orgManageBiz;

    /**
    * 保存机构基本信息
    * @param
    * @return
    */
    @ApiOperation("保存机构基本信息")
    @PostMapping("v1/organizationTenant/orgManage/addOrg")
    public String addOrg(@RequestBody @Validated AddOrgRequest addOrg ) {
        return orgManageBiz.addOrg(addOrg);
    }

    /**
    * 绑定机构功能
    * @param
    * @return
    */
    @ApiOperation("绑定机构功能")
    @PostMapping("v1/organizationTenant/orgManage/bindOrgFunc")
    public Boolean bindOrgFunc(@RequestBody @Validated OrgFuncRequest orgFunc ) {
        return orgManageBiz.bindOrgFunc(orgFunc);
    }

    /**
    * 设置机构相关费用
    * @param
    * @return
    */
    @ApiOperation("设置机构相关费用")
    @PostMapping("v1/organizationTenant/orgManage/settingOrgFee")
    public Boolean settingOrgFee(@RequestBody @Validated OrgFeeSettingRequest orgFeeSetting ) {
        return orgManageBiz.settingOrgFee(orgFeeSetting);
    }

    /**
    * 增加机构组员
    * @param
    * @return
    */
    @ApiOperation("增加机构组员")
    @PostMapping("v1/organizationTenant/orgManage/addMember")
    public Boolean addMember(@RequestBody @Validated AddOrgMemberRequest addOrgMember ) {
        return orgManageBiz.addMember(addOrgMember);
    }

    /**
    * 导出机构组员数据
    * @param
    * @return
    */
    @ApiOperation("导出机构组员数据")
    @PostMapping("v1/organizationTenant/orgManage/exportMember")
    public void exportMember() {
        orgManageBiz.exportMember();
    }

    /**
    * 导入机构组员数据
    * @param
    * @return
    */
    @ApiOperation("导入机构组员数据")
    @PostMapping("v1/organizationTenant/orgManage/importOrgMember")
    public Boolean importOrgMember(@RequestBody @Validated MultipartFileRequest file ) {
        return orgManageBiz.importOrgMember(file);
    }

    /**
    * 下载模板
    * @param
    * @return
    */
    @ApiOperation("下载模板")
    @GetMapping("v1/organizationTenant/orgManage/dowsnloadTemplate")
    public void dowsnloadTemplate() {
        orgManageBiz.dowsnloadTemplate();
    }


}