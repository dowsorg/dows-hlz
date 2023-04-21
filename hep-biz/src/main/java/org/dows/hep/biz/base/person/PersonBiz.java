package org.dows.hep.biz.base.person;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.dows.account.api.AccountGroupApi;
import org.dows.account.api.AccountInstanceApi;
import org.dows.account.api.AccountOrgApi;
import org.dows.account.api.AccountUserApi;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.request.AccountUserRequest;
import org.dows.account.response.AccountGroupResponse;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.user.api.api.UserExtinfoApi;
import org.dows.user.api.api.UserInstanceApi;
import org.dows.user.api.request.UserExtinfoRequest;
import org.dows.user.api.request.UserInstanceRequest;
import org.dows.user.api.response.UserExtinfoResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jx
 * @date 2023/4/20 13:18
 */
@Service
@RequiredArgsConstructor
public class PersonBiz {
    private final AccountInstanceApi accountInstanceApi;

    private final AccountGroupApi accountGroupApi;

    private final AccountOrgApi accountOrgApi;

    private final UserExtinfoApi userExtinfoApi;

    private final UserInstanceApi userInstanceApi;

    private final AccountUserApi accountUserApi;

    /**
     * @param
     * @return
     * @说明: 登录
     * @关联表: account_instance、account_group、account_org、account_role
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/20 13:20
     */
    public Map<String, Object> login(AccountInstanceRequest request) {
        return accountInstanceApi.login(request);
    }

    /**
     * @param
     * @return
     * @说明: 重置密码
     * @关联表: account_instance
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/20 13:46
     */
    public Boolean resetPwd(AccountInstanceRequest request) {
        return accountInstanceApi.resetPwd(request);
    }

    /**
     * @param
     * @return
     * @说明: 查看个人资料
     * @关联表: account_instance、account_user、user_instance、account_group、account_org、user_extinfo
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/20 13:59
     */
    public AccountInstanceResponse getPersonalInformation(String accountId, String appId) {
        //1、获取用户实例和账户实例
        AccountInstanceResponse instance = accountInstanceApi.getPersonalInformationByAccountId(accountId, appId);
        //2、获取账户所属机构
        List<AccountGroupResponse> groupList = accountGroupApi.getAccountGroupListByAccountId(instance.getAccountId(), appId);
        if (groupList != null && groupList.size() > 0) {
            //2.1、根据机构ID去重
            groupList = groupList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(AccountGroupResponse::getOrgId))), ArrayList::new));
            //2.2、机构名称拼接
            List<String> orgNameList = new ArrayList<>();
            groupList.forEach(group -> {
                AccountOrgResponse org = accountOrgApi.getAccountOrgByOrgId(group.getOrgId(), appId);
                orgNameList.add(org.getOrgName());
            });
            instance.setOrgName(orgNameList.stream().collect(Collectors.joining(",")));
        }
        //3、获取用户拓展信息
        UserExtinfoResponse extinfo = userExtinfoApi.getUserExtinfoByUserId(instance.getUserId());
        instance.setIntro(extinfo.getIntro());
        return instance;
    }

    /**
     * @param
     * @return
     * @说明: 修改个人资料
     * @关联表: account_instance、account_user、user_instance、user_extinfo
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/20 14:50
     */
    @DSTransactional
    public String updatePersonalInformation(AccountInstanceRequest request) {
        //1、更新账户实例
        String userId = accountInstanceApi.updateAccountInstanceByAccountId(request);
        //2、更新用户简介
        UserExtinfoRequest extinfo = UserExtinfoRequest.builder()
                .userId(userId)
                .intro(request.getIntro())
                .build();
        userExtinfoApi.insertOrUpdateExtinfo(extinfo);
        return userId;
    }

    /**
     * @param
     * @return
     * @说明: 创建教师/学生
     * @关联表: account_identifier、rbac_role、account_org、account_instance、account_role、account_group、account_user、user_instance
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/20 19:37
     */
    public AccountInstanceResponse createTeacherOrStudent(AccountInstanceRequest request) {
        //1、新增账号信息
        AccountInstanceResponse vo = accountInstanceApi.createAccountInstance(request);
        //2、新增用户信息
        UserInstanceRequest user = new UserInstanceRequest();
        BeanUtils.copyProperties(request, user);
        user.setName(request.getUserName());
        String userId = userInstanceApi.insertUserInstance(user);
        //3、创建账户和用户之间的关联关系
        AccountUserRequest accountUserRequest = AccountUserRequest.builder()
                .accountId(vo.getAccountId())
                .userId(userId)
                .appId(request.getAppId())
                .tentantId(request.getTenantId()).build();
        this.accountUserApi.createAccountUser(accountUserRequest);
        return vo;
    }

    /**
     * @param
     * @return
     * @说明: 创建教师/学生
     * @关联表: account_identifier、rbac_role、account_org、account_instance、account_role、account_group、user_instance、account_user、account_group_info
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/20 20:04
     */
    public IPage<AccountInstanceResponse> listTeacherOrStudent(AccountInstanceRequest request) {
        //1、获取所有accountIds
        Set<String> accountIds = new HashSet<>();
        List<AccountInstanceResponse> responses = accountInstanceApi.getAccountInstanceList(AccountInstanceRequest.builder().appId(request.getAppId()).build());
        //2、将accountIds传入
        responses.forEach(res -> {
            accountIds.add(res.getAccountId());
        });
        request.setAccountIds(accountIds);
        return accountInstanceApi.customAccountInstanceList(request);
    }

    /**
     * @param
     * @return
     * @说明: 编辑教师/学生
     * @关联表: account_instance、account_user、user_instance
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/21 10:30
     */
    public String editTeacherOrStudent(AccountInstanceRequest request) {
        return accountInstanceApi.updateAccountInstanceByAccountId(request);
    }

    /**
     * @param
     * @return
     * @说明: 教师 判断是否有班级
     * @关联表: ？？
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/21 10:52
     */
    public Boolean checkOwnClass(AccountInstanceRequest request) {
        List<AccountGroupResponse> groupList = accountGroupApi.getAccountGroupListByAccountId(request.getAccountId(), request.getAppId());
        if (groupList != null && groupList.size() > 0) {
            return true;
        }
        return false;
    }
}
