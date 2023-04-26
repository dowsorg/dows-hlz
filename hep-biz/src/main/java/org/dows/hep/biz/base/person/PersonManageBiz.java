package org.dows.hep.biz.base.person;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.account.api.*;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.request.AccountUserRequest;
import org.dows.account.response.*;
import org.dows.hep.api.base.person.request.PersonInstanceRequest;
import org.dows.hep.api.base.person.response.PersonInstanceResponse;
import org.dows.hep.biz.base.org.OrgBiz;
import org.dows.user.api.api.UserExtinfoApi;
import org.dows.user.api.api.UserInstanceApi;
import org.dows.user.api.request.UserExtinfoRequest;
import org.dows.user.api.request.UserInstanceRequest;
import org.dows.user.api.response.UserExtinfoResponse;
import org.dows.user.api.response.UserInstanceResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:人物:人物管理
 * @date 2023年4月23日 上午9:44:34
 */
@Service
@RequiredArgsConstructor
public class PersonManageBiz {
    private final AccountInstanceApi accountInstanceApi;

    private final AccountGroupApi accountGroupApi;

    private final AccountOrgApi accountOrgApi;

    private final UserExtinfoApi userExtinfoApi;

    private final UserInstanceApi userInstanceApi;

    private final AccountUserApi accountUserApi;

    private final AccountGroupInfoApi accountGroupInfoApi;

    private final OrgBiz orgBiz;

    /**
     * @param
     * @return
     * @说明: 批量删除人物
     * @关联表: AccountInstance、AccountUser、AccountRole、UserInstance、UserExtinfo、IndicatorInstance、IndicatorPrincipalRef、CaseEvent、CaseEventEval、CaseEventAction
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean deletePersons(String ids) {
        return Boolean.FALSE;
    }

    /**
     * @param
     * @return
     * @说明: 查看人物基本信息
     * @关联表: AccountInstance、AccountUser、UserInstance、UserExtinfo
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public PersonInstanceResponse getPerson(String accountId) {
        //1、根据账户ID获取账户信息
        AccountInstanceResponse accounInstance = accountInstanceApi.getAccountInstanceByAccountId(accountId);
        //2、获取用户信息
        AccountUserResponse accountUser = accountUserApi.getUserByAccountId(accountId);
        //3、获取用户实例
        UserInstanceResponse userInstance = userInstanceApi.getUserInstanceByUserId(accountUser.getUserId());
        //4、获取用户拓展信息
        UserExtinfoResponse extinfoResponse = userExtinfoApi.getUserExtinfoByUserId(userInstance.getUserId());
        PersonInstanceResponse response = PersonInstanceResponse.builder()
                .accountId(accountId)
                .accountName(accounInstance.getAccountName())
                .intro(extinfoResponse.getIntro())
                .avatar(accounInstance.getAvatar())
                .build();
        return response;
    }

    /**
     * @param
     * @return
     * @说明: 编辑人物基本信息
     * @关联表: AccountInstance、AccountUser、UserInstance、UserExtinfo
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean editPerson(PersonInstanceRequest personInstance) {
        return Boolean.FALSE;
    }

    /**
     * @param
     * @return
     * @说明: 复制人物
     * @关联表: AccountInstance、AccountUser、AccountRole、UserInstance、UserExtinfo、IndicatorInstance、IndicatorPrincipalRef、CaseEvent、CaseEventEval、CaseEventAction
     * @工时: 6H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean copyPerson(String accountId) {
        return Boolean.FALSE;
    }

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
            Set<String> orgNameList = new HashSet<>();
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
    @DSTransactional
    public AccountInstanceResponse createTeacherOrStudent(AccountInstanceRequest request) {
        //1、新增用户信息
        UserInstanceRequest user = new UserInstanceRequest();
        BeanUtils.copyProperties(request, user);
        user.setName(request.getUserName());
        String userId = userInstanceApi.insertUserInstance(user);
        //2、新增账号信息
        request.setUserId(userId);
        request.setIdentifier(orgBiz.createCode(7));
        AccountInstanceResponse vo = accountInstanceApi.createAccountInstance(request);
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
     * @说明: 教师/学生 列表
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
    @DSTransactional
    public String editTeacherOrStudent(AccountInstanceRequest request) {
        return accountInstanceApi.updateAccountInstanceByAccountId(request);
    }

    /**
     * @param
     * @return
     * @说明: 教师 获取负责班级
     * @关联表: account_group_info、account_org
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/21 10:52
     */
    public Set<String> listOwnClass(AccountInstanceRequest request) {
        List<AccountGroupInfoResponse> infoList = accountGroupInfoApi.getGroupInfoListByAccountId(request.getAccountId());
        Set<String> orgIdsList = new HashSet<>();
        if (infoList != null && infoList.size() > 0) {
            //2.1、根据机构ID去重
            infoList = infoList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(AccountGroupInfoResponse::getOrgId))), ArrayList::new));
            //2.2、机构id拼接
            infoList.forEach(group -> {
                AccountOrgResponse org = accountOrgApi.getAccountOrgByOrgId(group.getOrgId(), request.getAppId());
                orgIdsList.add(org.getOrgId());
            });
        }
        return orgIdsList;
    }

    /**
     * @param
     * @return
     * @说明: 教师 班级转移
     * @关联表: account_group_info、account_group
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/21 11:51
     */
    @DSTransactional
    public Boolean transferClass(AccountInstanceRequest request) {
        Boolean flag = true;
        //1、获取班级的负责人
        List<AccountGroupInfoResponse> responseList = accountGroupInfoApi.getGroupInfoListByOrgIds(request.getOrgIds());
        //2、获取项目负责人的账号ID,并更新
        String ownId = responseList.get(0).getAccountId();
        Integer count1 = accountGroupApi.transferAccountIdOfAccountGroup(request.getOrgIds(), ownId, request.getAccountId());
        if (count1 == 0) {
            flag = false;
        }
        Integer count2 = accountGroupInfoApi.transferAccountIdOfGroupInfo(request.getOrgIds(), ownId, request.getAccountId());
        if (count2 == 0) {
            flag = false;
        }
        //3、删除该账户相关信息
        Integer count3 = accountInstanceApi.deleteAccountInstanceByAccountIds(Arrays.asList(ownId).stream().collect(Collectors.toSet()));
        if (count3 == 0) {
            flag = false;
        }
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 删除 教师/学生
     * @关联表: account_group、account_org、account_org_info、account_group_info、account_instance、account_identifier、account_role、account_user、user_instance
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/21 14:12
     */
    @DSTransactional
    public Boolean deleteTeacherOrStudents(Set<String> accountIds, String roleName, String appId) {
        Boolean flag = false;
        for (String accountId : accountIds) {
            //1、教师
            if (roleName.equals("教师")) {
                //1.1、获取用户组织架构信息
                List<AccountGroupResponse> groupList = accountGroupApi.getAccountGroupListByAccountId(accountId, appId);
                Set<String> orgIdsList = new HashSet<>();
                if (groupList != null && groupList.size() > 0) {
                    //1.2、根据机构ID去重
                    groupList = groupList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(AccountGroupResponse::getOrgId))), ArrayList::new));
                    //1.3、机构id拼接
                    groupList.forEach(group -> {
                        AccountOrgResponse org = accountOrgApi.getAccountOrgByOrgId(group.getOrgId(), appId);
                        orgIdsList.add(org.getOrgId());
                    });
                    //1.4、删除上述机构下的所有成员及机构相关信息
                    accountOrgApi.batchDeleteAccountOrgsByOrgIds(orgIdsList);
                }
                //1.5、删除账号相关信息
                accountInstanceApi.deleteAccountInstanceByAccountIds(Arrays.asList(accountId).stream().collect(Collectors.toSet()));
                flag = true;
            }
            //2、学生
            if (roleName.equals("学生")) {
                //2.1、删除学生账户相关信息
                accountInstanceApi.deleteAccountInstanceByAccountIds(Arrays.asList(accountId).stream().collect(Collectors.toSet()));
                //2.2、删除学生与机构的关系表
                List<AccountGroupResponse> groupList = accountGroupApi.getAccountGroupListByAccountId(accountId, appId);
                Set<String> ids = new HashSet<>();
                if (groupList != null && groupList.size() > 0) {
                    groupList.forEach(group -> {
                        ids.add(group.getId());
                    });
                }
                accountGroupApi.batchDeleteGroups(ids);

                List<AccountGroupInfoResponse> groupInfoList = accountGroupInfoApi.getGroupInfoListByAccountId(accountId);
                Set<String> groupInfoIds = new HashSet<>();
                if (groupInfoList != null && groupInfoList.size() > 0) {
                    groupInfoList.forEach(groupInfo -> {
                        groupInfoIds.add(groupInfo.getId());
                    });
                }
                accountGroupInfoApi.batchDeleteGroupInfos(groupInfoIds);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 新增 人物
     * @关联表: user_instance、user_extinfo、account_identifier、rbac_role、account_org、account_instance、account_role、account_group、account_user
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/25 17:35
     */
    @DSTransactional
    public PersonInstanceResponse addPerson(AccountInstanceRequest request) {
        //1、创建随机账号
        request.setAccountName(randomWord(6));
        //2、新增用户信息
        UserInstanceRequest user = new UserInstanceRequest();
        BeanUtils.copyProperties(request, user);
        user.setName(request.getUserName());
        String userId = userInstanceApi.insertUserInstance(user);
        //3、新增用户简介
        UserExtinfoRequest userExtinfo = UserExtinfoRequest.builder()
                .userId(userId)
                .intro(request.getIntro())
                .build();
        String extinfoId = userExtinfoApi.insertUserExtinfo(userExtinfo);
        //4、新增账号信息
        request.setIdentifier(orgBiz.createCode(7));
        AccountInstanceResponse vo = accountInstanceApi.createAccountInstance(request);
        //5、创建账户和用户之间的关联关系
        AccountUserRequest accountUserRequest = AccountUserRequest.builder()
                .accountId(vo.getAccountId())
                .userId(userId)
                .appId(request.getAppId())
                .tentantId(request.getTenantId()).build();
        this.accountUserApi.createAccountUser(accountUserRequest);
        return PersonInstanceResponse.builder().accountId(vo.getAccountId())
                .build();
    }
    /**
     * 生成随机账号
     */
    public static String randomWord(int length) {
        Random random = new Random();
        StringBuilder word = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            word.append((char)('a' + random.nextInt(26)));
        }

        return word.toString();
    }

    /**
     * @param
     * @return
     * @说明: 人物 列表
     * @关联表: user_instance、user_extinfo、account_identifier、rbac_role、account_org、account_instance、account_role、account_group、account_user
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/25 17:35
     */
    public IPage<PersonInstanceResponse> listPerson(AccountInstanceRequest request) {
        //1、获取所有accountIds
        Set<String> accountIds = new HashSet<>();
        List<AccountInstanceResponse> responses = accountInstanceApi.getAccountInstanceList(AccountInstanceRequest.builder().appId(request.getAppId()).build());
        //2、将accountIds传入
        responses.forEach(res -> {
            accountIds.add(res.getAccountId());
        });
        request.setAccountIds(accountIds);
        IPage<AccountInstanceResponse> accountInstancePage = accountInstanceApi.customAccountInstanceList(request);
        //3、复制
        List<PersonInstanceResponse> personInstanceResponseList = new ArrayList<>();
        List<AccountInstanceResponse> accountInstanceList = accountInstancePage.getRecords();
        accountInstanceList.forEach(accountInstance->{
            PersonInstanceResponse personInstance = PersonInstanceResponse.builder()
                    .accountId(accountInstance.getAccountId())
                    .accountName(accountInstance.getAccountName())
                    .avatar(accountInstance.getAvatar())
                    .intro(accountInstance.getIntro())
                    .build();
            personInstanceResponseList.add(personInstance);
        });
        IPage<PersonInstanceResponse> personInstancePage = new Page<>();
        BeanUtils.copyProperties(accountInstancePage, personInstancePage, new String[]{"records"});
        personInstancePage.setRecords(personInstanceResponseList);
        return personInstancePage;
    }
}