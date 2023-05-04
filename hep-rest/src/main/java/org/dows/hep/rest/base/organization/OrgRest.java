package org.dows.hep.rest.base.organization;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.account.request.AccountGroupRequest;
import org.dows.account.request.AccountOrgRequest;
import org.dows.account.response.AccountGroupResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.hep.biz.base.org.OrgBiz;
import org.springframework.web.bind.annotation.*;

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
    public IPage<AccountOrgResponse> listClasss(@RequestBody AccountOrgRequest request) {
        return orgBiz.listClasss(request);
    }

    /**
     * 创建班级
     * @param
     * @return
     */
    @Operation(summary = "创建班级")
    @PostMapping("v1/baseOrg/org/addClass")
    public String addClass(@RequestBody AccountOrgRequest request, @RequestParam String accountId) {
        return orgBiz.addClass(request,accountId);
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
    public Boolean deleteClasss(@RequestParam Set<String> ids){
        return orgBiz.deleteClasss(ids);
    }

    /**
     * 创建机构
     * @param
     * @return
     */
    @Operation(summary = "创建机构")
    @PostMapping("v1/baseOrg/org/addOrgnization")
    public Boolean addOrgnization(@RequestBody AccountOrgRequest request) {
        return orgBiz.addOrgnization(request);
    }

    /**
     * 添加机构人物
     * @param
     * @return
     */
    @Operation(summary = "添加机构人物")
    @PostMapping("v1/baseOrg/org/addPerson")
    public Integer addPerson(@RequestParam Set<String> personIds, @RequestParam String orgId,@RequestParam String appId) {
        return orgBiz.addPerson(personIds,orgId,appId);
    }

    /**
     * 获取机构人物列表
     */
    @Operation(summary = "获取机构人物列表")
    @PostMapping("v1/baseOrg/org/listPerson")
    public IPage<AccountGroupResponse> listPerson(@RequestBody AccountGroupRequest request) {
        return orgBiz.listPerson(request);
    }

    /**
     * 查看机构基本信息
     */
    @Operation(summary = "查看机构基本信息")
    @GetMapping("v1/baseOrg/org/getOrg/{orgId}/{appId}")
    public AccountOrgResponse getOrg(@PathVariable String orgId,@PathVariable String appId) {
        return orgBiz.getOrg(orgId,appId);
    }
}
