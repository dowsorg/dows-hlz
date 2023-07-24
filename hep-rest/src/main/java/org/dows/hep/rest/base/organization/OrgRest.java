package org.dows.hep.rest.base.organization;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.account.request.AccountGroupRequest;
import org.dows.account.request.AccountOrgRequest;
import org.dows.account.response.AccountGroupResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.account.util.JwtUtil;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.api.user.organization.request.CaseOrgRequest;
import org.dows.hep.api.user.organization.response.CaseOrgResponse;
import org.dows.hep.biz.base.org.OrgBiz;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author jx
 * @date 2023/4/21 17:09
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "机构", description = "机构")
public class OrgRest {
    private final OrgBiz orgBiz;

    /**
     * 班级 列表
     * @param
     * @return
     */
    @Operation(summary = "班级列表")
    @PostMapping("v1/baseOrg/org/listClasss")
    public IPage<AccountOrgResponse> listClasss(@RequestBody AccountOrgRequest request,@Nullable @RequestParam String accountId) {
        return orgBiz.listClasss(request,accountId);
    }

    /**
     * 创建班级
     * @param
     * @return
     */
    @Operation(summary = "创建班级")
    @PostMapping("v1/baseOrg/org/addClass")
    public String addClass(@RequestBody AccountOrgRequest request,
                           @RequestParam String accountId, HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        //1、获取登录账户和角色
        String loginId = map.get("accountId").toString();
        return orgBiz.addClass(request,accountId,loginId);
    }

    /**
     * 编辑班级
     * @param
     * @return
     */
    @Operation(summary = "编辑班级")
    @PutMapping("v1/baseOrg/org/editClass")
    public Boolean editClass(@RequestBody AccountOrgRequest request,@RequestParam String accountId) {
        return orgBiz.editClass(request,accountId);
    }

    /**
     * 删除班级
     * @param
     * @return
     */
    @Operation(summary =  "删除 班级")
    @DeleteMapping("v1/baseOrg/org/deleteClasss")
    public Boolean deleteClasss(@RequestBody Set<String> ids){
        return orgBiz.deleteClasss(ids);
    }

    /**
     * 获取案例机构 分页
     * @param
     * @return
     */
    @Operation(summary = "获取案例机构 分页")
    @PostMapping("v1/baseOrg/org/listOrgnization")
    public IPage<CaseOrgResponse> listOrgnization(@RequestBody CaseOrgRequest request){
        return orgBiz.listOrgnization(request);
    }

    /**
     * 创建案例机构
     * @param
     * @return
     */
    @Operation(summary = "创建案例机构")
    @PostMapping("v1/baseOrg/org/addOrgnization")
    public String addOrgnization(@RequestBody AccountOrgRequest request,
                                 @RequestParam String caseInstanceId,
                                 @Nullable @RequestParam String ver,
                                 @Nullable @RequestParam String caseIdentifier) {
        return orgBiz.addOrgnization(request,caseInstanceId,ver,caseIdentifier);
    }

    /**
     * 添加案例机构人物
     * @param
     * @return
     */
    @Operation(summary = "添加案例机构人物")
    @PostMapping("v1/baseOrg/org/addPerson")
    public Integer addPerson(@RequestBody Set<String> personIds,
                             @RequestParam String caseInstanceId,
                             @RequestParam String caseOrgId,
                             @RequestParam String appId) {
        return orgBiz.addPerson(personIds,caseInstanceId,caseOrgId,appId);
    }

    /**
     * 通过案例人物ID获取accountId
     * @param
     * @return
     */
    @Operation(summary = "通过案例人物ID获取accountId")
    @PostMapping("v1/baseOrg/org/getAccountIdByCasePerson")
    public String getAccountIdByCasePerson(@RequestParam String casePersonId) {
        return orgBiz.getAccountIdByCasePerson(casePersonId);
    }

    /**
     * 将自定义人物添加到案例机构中
     * @param
     * @return
     */
    @Operation(summary = "将自定义人物添加到案例机构中")
    @PostMapping("v1/baseOrg/org/addPersonToCaseOrg")
    public String addPersonToCaseOrg(@RequestParam String personId,
                                     @RequestParam String caseInstanceId,
                                     @RequestParam String caseOrgId,
                                     @RequestParam String appId) {
        return orgBiz.addPersonToCaseOrg(personId,caseInstanceId,caseOrgId,appId);
    }



    /**
     * 获取案例机构人物列表
     */
    @Operation(summary = "获取案例机构人物列表")
    @PostMapping("v1/baseOrg/org/listPerson")
    public IPage<AccountGroupResponse> listPerson(@RequestBody AccountGroupRequest request,
                                                  @RequestParam String caseOrgId) {
        return orgBiz.listPerson(request,caseOrgId);
    }

    /**
     * 查看案例机构基本信息
     */
    @Operation(summary = "查看机构基本信息")
    @GetMapping("v1/baseOrg/org/getOrg/{caseOrgId}/{appId}")
    public AccountOrgResponse getOrg(@PathVariable String caseOrgId,
                                     @PathVariable String appId) {
        return orgBiz.getOrg(caseOrgId,appId);
    }

    /**
     * 编辑机构基本信息
     */
    @Operation(summary = "编辑机构基本信息")
    @PostMapping("v1/baseOrg/org/editOrg")
    public Boolean editOrg(@RequestBody AccountOrgRequest request,
                           @RequestParam String caseOrgId,
                           @Nullable @RequestParam String ver,
                           @Nullable @RequestParam String caseIdentifier) {
        return orgBiz.editOrg(request,caseOrgId,ver,caseIdentifier);
    }

    /**
     * 判断机构名称是否重复
     */
    @Operation(summary = "判断机构名称是否重复")
    @GetMapping("v1/baseOrg/org/checkOrg")
    public Boolean checkOrg(@RequestParam String orgCode,
                            @RequestParam String appId,
                            @RequestParam String orgName) {
        return orgBiz.checkOrg(orgCode,appId,orgName);
    }

    /**
     * 删除机构基本信息
     */
    @Operation(summary = "删除机构基本信息")
    @DeleteMapping("v1/baseOrg/org/deleteOrgs")
    public Boolean deleteOrgs(@Nullable @RequestBody Set<String> caseOrgIds,
                              @RequestParam String caseInstanceId,
                              @RequestParam String appId) {
        return orgBiz.deleteOrgs(caseOrgIds,caseInstanceId,appId);
    }

    /**
     * 删除机构人物
     */
    @Operation(summary = "删除机构人物")
    @DeleteMapping("v1/baseOrg/org/deletePersons")
    public Boolean deletePersons(@RequestBody Map<String,Object> ids,
                                 @RequestParam String caseInstanceId,
                                 @RequestParam String appId) {
        return orgBiz.deletePersons(
                new HashSet<>((ArrayList)ids.get("caseOrgIds")),
                caseInstanceId,
                new HashSet<>((ArrayList)ids.get("accountIds")),
                appId);
    }


    /**
     * 同一案例中，人物不能被多个机构共享
     */
    @Operation(summary = "同一案例中，人物不能被多个机构共享")
    @PostMapping("v1/baseOrg/org/checkInstancePerson")
    public Boolean checkInstancePerson(@RequestParam String caseOrgId,
                                       @RequestParam String caseInstanceId,
                                       @RequestParam String accountId) {
        return orgBiz.checkInstancePerson(caseOrgId,caseInstanceId,accountId);
    }

    /**
     * 复制机构人物
     */
    @Operation(summary = "复制机构人物")
    @PostMapping("v1/baseOrg/org/copyPerson")
    public String copyPerson(@RequestParam String caseOrgId,
                             @RequestParam String caseInstanceId,
                             @RequestParam String accountId) {
        return orgBiz.copyPerson(caseOrgId,caseInstanceId,accountId);
    }
}
