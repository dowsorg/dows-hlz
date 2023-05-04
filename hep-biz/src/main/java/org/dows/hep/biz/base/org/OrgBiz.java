package org.dows.hep.biz.base.org;

import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.dows.account.api.*;
import org.dows.account.request.AccountGroupInfoRequest;
import org.dows.account.request.AccountGroupRequest;
import org.dows.account.request.AccountOrgGeoRequest;
import org.dows.account.request.AccountOrgRequest;
import org.dows.account.response.AccountGroupResponse;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.account.response.AccountUserResponse;
import org.dows.hep.entity.CaseOrgFeeEntity;
import org.dows.hep.service.CaseOrgFeeService;
import org.dows.sequence.api.IdGenerator;
import org.dows.user.api.api.UserInstanceApi;
import org.dows.user.api.response.UserInstanceResponse;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author jx
 * @date 2023/4/21 17:12
 */
@Service
@RequiredArgsConstructor
public class OrgBiz {
    private final AccountOrgApi accountOrgApi;
    private final AccountGroupInfoApi accountGroupInfoApi;
    private final AccountGroupApi accountGroupApi;
    private final AccountUserApi accountUserApi;
    private final UserInstanceApi userInstanceApi;
    private final AccountInstanceApi accountInstanceApi;
    private final CaseOrgFeeService caseOrgFeeService;
    private final IdGenerator idGenerator;
    private final AccountOrgGeoApi accountOrgGeoApi;

    /**
     * @param
     * @return
     * @说明: 创建 班级
     * @关联表: account_group、account_org、account_org_info、account_group_info
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/21 17:12
     */
    @DSTransactional
    public String addClass(AccountOrgRequest request, String accountId) {
        //1、生成随机code
        String orgCode = createCode(7);
        request.setOrgCode(orgCode);
        //2、创建机构
        String orgId = accountOrgApi.createAccountOrg(request);
        //3、创建团队负责人
        String groupInfoId = accountGroupInfoApi.insertAccountGroupInfo(AccountGroupInfoRequest.builder()
                .accountId(accountId)
                .orgId(orgId)
                .descr(request.getDescr())
                .appId(request.getAppId())
                .build());
        //4、创建团队组员
        request.setOrgId(orgId);
        String groupId = accountGroupApi.insertAccountGroup(AccountGroupRequest.builder()
                .accountId(accountId)
                .orgId(orgId)
                .appId(request.getAppId())
                .build());
        return orgId;
    }

    /**
     * @param
     * @return
     * @说明: 编辑 班级
     * @关联表: account_group、account_org、account_org_info、account_group_info、account_instance、account_user、user_instance
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/23 13:20
     */
    @DSTransactional
    public Boolean editClass(AccountOrgRequest request, String accountId) {
        //1、修改机构
        Boolean flag1 = accountOrgApi.updateAccountOrgByOrgId(request);
        //2、更新团队负责人
        //2.1、获取账户实例
        AccountInstanceResponse accountInstance = accountInstanceApi.getAccountInstanceByAccountId(accountId);
        //2.2、获取团队负责人的用户信息
        AccountUserResponse accountUser = accountUserApi.getUserByAccountId(accountId);
        //2.3、获取用户实例
        UserInstanceResponse userInstance = userInstanceApi.getUserInstanceByUserId(accountUser.getUserId());
        Boolean flag2 = accountGroupInfoApi.updateAccountGroupInfo(AccountGroupInfoRequest.builder()
                .orgId(request.getOrgId())
                .descr(request.getDescr())
                .owner(userInstance.getName())
                .accountId(accountId)
                .userId(userInstance.getUserId())
                .build());
        //3、更新机构团队
        List<AccountGroupResponse> groupList = accountGroupApi.getAccountGroupByOrgId(request.getOrgId());
        if (groupList != null && groupList.size() > 0) {
            groupList.forEach(group -> {
                accountGroupApi.updateOrgById(AccountGroupRequest.builder()
                        .id(group.getId())
                        .orgId(request.getOrgId())
                        .orgName(request.getOrgName())
                        .accountId(accountId)
                        .accountName(accountInstance.getAccountName())
                        .userId(userInstance.getUserId())
                        .build());
            });
        }
        return flag2;
    }

    /**
     * @param
     * @return
     * @说明: 编辑 班级
     * @关联表: account_group、account_org、account_org_info、account_group_info、account_instance
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/23 15:20
     */
    @DSTransactional
    public Boolean deleteClasss(Set<String> ids) {
        Boolean flag = true;
        //1、获取机构下的所有成员
        Set<String> accountIds = new HashSet<>();
        ids.forEach(id -> {
            List<AccountGroupResponse> groupList = accountGroupApi.getAccountGroupByOrgId(id);
            if (groupList != null && groupList.size() > 0) {
                groupList.forEach(group -> {
                    accountIds.add(group.getAccountId());
                });
            }
        });
        //2、删除组织架构
        Integer count1 = accountOrgApi.batchDeleteAccountOrgs(ids);
        if (count1 == 0) {
            flag = false;
        }
        //3、删除账户实例
        Integer count2 = accountInstanceApi.deleteAccountInstanceByAccountIds(accountIds);
        if (count2 == 0) {
            flag = false;
        }
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 班级 列表
     * @关联表: account_group、account_org、account_org_info、account_group_info
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/23 16:58
     */
    public IPage<AccountOrgResponse> listClasss(AccountOrgRequest request) {
        IPage<AccountOrgResponse> accountOrgResponse = accountOrgApi.customAccountOrgList(request);
        //1、总人数剔除教师一个
        List<AccountOrgResponse> accountList = accountOrgResponse.getRecords();
        if (accountList != null && accountList.size() > 0) {
            accountList.forEach(account -> {
                account.setCurrentNum(account.getCurrentNum() - 1);
            });
        }
        accountOrgResponse.setRecords(accountList);
        return accountOrgResponse;
    }

    /**
     * @param
     * @return
     * @说明: 创建 机构
     * @关联表: account_org、account_org_fee
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/28 11:50
     */
    @DSTransactional
    public Boolean addOrgnization(AccountOrgRequest request) {
        //1、创建机构
        String orgId = accountOrgApi.createAccountOrg(request);
        //2、创建机构费用明细
        List<CaseOrgFeeEntity> caseOrgList = JSONUtil.toList(request.getDescr(), CaseOrgFeeEntity.class);
        caseOrgList.forEach(caseOrg -> {
            caseOrg.setAppId(request.getAppId());
            caseOrg.setCaseOrgId(orgId);
            caseOrg.setCaseOrgFeeId(idGenerator.nextIdStr());
        });
        caseOrgFeeService.saveBatch(caseOrgList);
        //3、创建机构点位
        AccountOrgGeoRequest geoRequest = AccountOrgGeoRequest
                .builder()
                .orgId(orgId)
                .orgName(request.getOrgName())
                .orgLongitude(request.getOrgLongitude())
                .orgLatitude(request.getOrgLatitude())
                .build();
        return accountOrgGeoApi.insertOrgGeo(geoRequest);
    }

    /**
     * @param
     * @return
     * @说明: 添加机构人物
     * @关联表: account_group、account
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/04 14:30
     */
    @DSTransactional
    public Integer addPerson(Set<String> personIds, String orgId, String appId) {
        Integer count = 0;
        for (String personId : personIds) {
            AccountInstanceResponse instanceResponse = accountInstanceApi.getAccountInstanceByAccountId(personId);
            AccountOrgResponse orgResponse = accountOrgApi.getAccountOrgByOrgId(orgId, appId);
            AccountGroupRequest request = AccountGroupRequest
                    .builder()
                    .orgId(orgId)
                    .orgName(orgResponse.getOrgName())
                    .accountId(personId)
                    .accountName(instanceResponse.getAccountName())
                    .userId(instanceResponse.getUserId())
                    .appId(appId)
                    .build();
            String groupId = accountGroupApi.insertAccountGroup(request);
            if (StringUtils.isNotEmpty(groupId)) {
                count++;
            }
        }
        return count;
    }

    /**
     * @param
     * @return
     * @说明: 生成 随机机构编码
     */
    public String createCode(int n) {
        Random r = new Random();
        String code = "";
        for (int i = 0; i < n; i++) {
            int type = r.nextInt(3);  // 0  1  2
            switch (type) {
                case 0:
                    // 大写英文 A 65  Z 90
                    char ch = (char) (r.nextInt(25) + 65);
                    code += ch;
                    break;
                case 1:
                    // 小写英文 A 97 Z 122
                    char ch1 = (char) (r.nextInt(25) + 97);
                    code += ch1;
                    break;
                case 2:
                    // 数字
                    int ch2 = r.nextInt(10);
                    code += ch2;
                    break;
            }
        }
        return code;
    }
}
