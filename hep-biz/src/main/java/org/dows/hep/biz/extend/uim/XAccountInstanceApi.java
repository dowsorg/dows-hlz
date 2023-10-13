package org.dows.hep.biz.extend.uim;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dows.account.biz.enums.EnumAccountRolePrincipalType;
import org.dows.account.biz.enums.EnumAccountStatusCode;
import org.dows.account.biz.exception.AccountException;
import org.dows.account.entity.*;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.service.*;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.rbac.api.RbacRoleApi;
import org.dows.user.api.api.UserExtinfoApi;
import org.dows.user.api.api.UserInstanceApi;
import org.dows.user.api.request.UserInstanceRequest;
import org.dows.user.api.response.UserInstanceResponse;
import org.dows.user.service.UserCategoryService;
import org.dows.user.service.UserInstanceService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/10/10 17:44
 */

@Service
@DS("uim")
@RequiredArgsConstructor
public class XAccountInstanceApi {

    private final AccountInstanceService accountInstanceService;
    private final AccountUserService accountUserService;
    private final AccountIdentifierService accountIdentifierService;
    private final RbacRoleApi rbacRoleApi;
    private final AccountRoleService accountRoleService;
    private final AccountOrgService accountOrgService;
    private final AccountGroupService accountGroupService;
    private final UserInstanceApi userInstanceApi;
    private final AccountGroupInfoService accountGroupInfoService;
    private final UserExtinfoApi userExtinfoApi;
    private final UserCategoryService userCategoryService;
    private final UserInstanceService userInstanceService;

    public List<AccountInstance> getAccountInstancesBySource(String source, SFunction<AccountInstance,?>...cols) {

        return accountInstanceService.lambdaQuery()
                .eq(AccountInstance::getSource, source)
                .orderByAsc(AccountInstance::getId)
                .select(cols)
                .list();

    }

    public boolean updateActountDt(String accountId, Date date){
        return accountInstanceService.lambdaUpdate()
                .eq(AccountInstance::getAccountId, accountId)
                .set(AccountInstance::getDt, date)
                .update();
    }

    public String updateAccountInstanceByAccountId(AccountInstanceRequest request) {
        AccountInstance accountInstance = accountInstanceService.lambdaQuery()
                .eq(AccountInstance::getAccountId, request.getAccountId())
                .eq(AccountInstance::getAppId,request.getAppId())
                .one();
        //1、修改账号-实例
        if(StringUtils.isNotEmpty(request.getPassword())){
            request.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        }
        AccountInstance instance = AccountInstance.builder().phone(request.getPhone())
                .avatar(request.getAvatar())
                .password(request.getPassword())
                .indate(request.getIndate())
                .expdate(request.getExpdate())
                .status(request.getStatus())
                .accountId(request.getAccountId())
                .id(accountInstance.getId())
                .build();
        if(null!=request.getDt()){
            instance.setDt(request.getDt());
        }
        boolean flag = accountInstanceService.updateById(instance);
        if (flag == false) {
            throw new AccountException(EnumAccountStatusCode.ACCOUNT_UPDATE_FAIL_EXCEPTION);
        }
        //2、通过账号ID找到用户ID
        AccountUser accountUser = accountUserService.lambdaQuery()
                .eq(AccountUser::getAccountId, request.getAccountId())
                .one();
        //3、修改用户-实例
        UserInstanceResponse instanceResponse = userInstanceApi.getUserInstanceByUserId(accountUser.getUserId());
        UserInstanceRequest user = new UserInstanceRequest().builder()
                .name(request.getUserName())
                .gender(request.getGender())
                .nickName(request.getNickName())
                .avatar(request.getAvatar())
                .phone(request.getPhone())
                .userId(accountUser.getUserId())
                .id(instanceResponse.getId()).build();
        userInstanceApi.updateUserInstance(user);
        return accountUser.getUserId();
    }

    public IPage<AccountInstanceResponse> customAccountInstanceList(AccountInstanceRequest request) {
        Set<String> accountIds = new HashSet<>();
        boolean flag = false;
        boolean check = false;
        //1.1、获取角色名称获取对应账号Id
        if (StringUtils.isNotEmpty(request.getRoleName())) {
            flag = true;
            Set<String> tempAccountIds = new HashSet<>();
            List<AccountRole> accountRoleList = accountRoleService.lambdaQuery()
                    .select(AccountRole::getPrincipalId)
                    .like(AccountRole::getRoleName, request.getRoleName())
                    .eq(AccountRole::getPrincipalType, EnumAccountRolePrincipalType.PERSONAL.getCode())
                    .eq(AccountRole::getDeleted, false)
                    .list();
            if (accountRoleList != null && accountRoleList.size() > 0) {
                accountRoleList.forEach(accountRole -> {
                    tempAccountIds.add(accountRole.getPrincipalId());
                });
            }
            if (tempAccountIds == null || tempAccountIds.size() == 0) {
                accountIds.clear();
                check = true;
            } else {
                if (accountIds != null && accountIds.size() > 0) {
                    accountIds.retainAll(tempAccountIds);
                } else {
                    accountIds.addAll(tempAccountIds);
                }
            }
        }

        //1.2、根据姓名、性别查询对应用户
        UserInstanceRequest dto = new UserInstanceRequest();
        if (StringUtils.isNotEmpty(request.getUserName())) {
            flag = true;
            dto.setName(request.getUserName());
        }
        if (StringUtils.isNotEmpty(request.getGender())) {
            flag = true;
            dto.setGender(request.getGender());
        }
        if (!ReflectUtil.isObjectNull(dto) && dto != null) {
            Set<String> tempAccountIds = new HashSet<>();
            List<UserInstanceResponse> instanceList = userInstanceApi.getUserInstanceFilterList(dto);
            if (instanceList != null && instanceList.size() > 0) {
                instanceList.forEach(model -> {
                    AccountUser user = accountUserService.lambdaQuery()
                            .eq(AccountUser::getUserId, model.getUserId())
                            .one();
                    if (user != null) {
                        tempAccountIds.add(user.getAccountId());
                    }
                });
            }
            if (tempAccountIds == null || tempAccountIds.size() == 0) {
                accountIds.clear();
                check = true;
            } else {
                if (accountIds != null && accountIds.size() > 0) {
                    accountIds.retainAll(tempAccountIds);
                } else {
                    accountIds.addAll(tempAccountIds);
                }
            }
        }

        //1.3、根据姓名、手机号查询用户之姓名
        if (StringUtils.isNotEmpty(request.getAccountNamePhone())) {
            flag = true;
            Set<String> nameAccountIds = new HashSet<>();
            UserInstanceRequest dtoName = new UserInstanceRequest();
            dtoName.setName(request.getAccountNamePhone());
            if (!ReflectUtil.isObjectNull(dtoName) && dtoName != null) {
                List<UserInstanceResponse> instanceList = userInstanceApi.getUserInstanceFilterList(dtoName);
                if (instanceList != null && instanceList.size() > 0) {
                    instanceList.forEach(model -> {
                        AccountUser user = accountUserService.lambdaQuery()
                                .eq(AccountUser::getUserId, model.getUserId())
                                .one();
                        if (user != null) {
                            nameAccountIds.add(user.getAccountId());
                        }
                    });
                }
            }
            //3、根据姓名、手机号查询用户之手机号
            UserInstanceRequest dtoPhone = new UserInstanceRequest();
            dtoPhone.setPhone(request.getAccountNamePhone());
            if (!ReflectUtil.isObjectNull(dtoPhone) && dtoPhone != null) {
                List<UserInstanceResponse> instanceList = userInstanceApi.getUserInstanceFilterList(dtoPhone);
                if (instanceList != null && instanceList.size() > 0) {
                    instanceList.forEach(model -> {
                        AccountUser user = accountUserService.lambdaQuery()
                                .eq(AccountUser::getUserId, model.getUserId())
                                .one();
                        if (user != null) {
                            nameAccountIds.add(user.getAccountId());
                        }
                    });
                }
            }
            //3、根据账号名称查询用户账号id
            AccountInstanceRequest account = new AccountInstanceRequest();
            account.setAccountName(request.getAccountNamePhone());
            if (!ReflectUtil.isObjectNull(account) && account != null) {
                List<AccountInstance> instanceList = accountInstanceService.lambdaQuery()
                        .like(AccountInstance::getAccountName, request.getAccountNamePhone()).list();
                if (instanceList != null && instanceList.size() > 0) {
                    instanceList.forEach(model -> {
                        nameAccountIds.add(model.getAccountId());
                    });
                }
            }
            if (nameAccountIds == null || nameAccountIds.size() == 0) {
                accountIds.clear();
                check = true;
            } else {
                if (accountIds != null && accountIds.size() > 0) {
                    accountIds.retainAll(nameAccountIds);
                } else {
                    accountIds.addAll(nameAccountIds);
                }
            }
        }

        //4、根据所属机构名称查询账户
        if (StringUtils.isNotEmpty(request.getOrgName())) {
            flag = true;
            //4、1 获取机构id
            List<AccountGroupInfo> infoList = accountGroupInfoService.lambdaQuery()
                    .like(AccountGroupInfo::getOrgName, request.getOrgName())
                    .list();
            Set<String> tempAccountIds = new HashSet<>();
            //4.2、获取机构下的账户id集合
            infoList.forEach(model -> {
                List<AccountGroup> groupList = accountGroupService.lambdaQuery().eq(AccountGroup::getOrgId, model.getOrgId()).eq(AccountGroup::getDeleted, false).list();
                if (groupList != null && groupList.size() > 0) {
                    groupList.forEach(group -> {
                        tempAccountIds.add(group.getAccountId());
                    });
                }
            });
            if (tempAccountIds == null || tempAccountIds.size() == 0) {
                accountIds.clear();
                check = true;
            } else {
                if (accountIds != null && accountIds.size() > 0) {
                    accountIds.retainAll(tempAccountIds);
                } else {
                    accountIds.addAll(tempAccountIds);
                }
            }
        }

        //5、根据组别ID查询账户
        if (StringUtils.isNotEmpty(request.getTeamName())) {
            flag = true;
            //5、1 获取机构id
            List<AccountOrg> orgList = accountOrgService.lambdaQuery()
                    .like(AccountOrg::getOrgName, request.getTeamName())
                    .list();
            //5.2、获取机构下的账户id集合
            Set<String> tempAccountIds = new HashSet<>();
            orgList.forEach(model -> {
                List<AccountGroup> groupList = accountGroupService.lambdaQuery().eq(AccountGroup::getOrgId, model.getId()).eq(AccountGroup::getDeleted, false).list();
                if (groupList != null && groupList.size() > 0) {
                    groupList.forEach(group -> {
                        tempAccountIds.add(group.getAccountId());
                    });
                }
            });
            if (tempAccountIds == null || tempAccountIds.size() == 0) {
                check = true;
            } else {
                if (accountIds != null && accountIds.size() > 0) {
                    accountIds.retainAll(tempAccountIds);
                } else {
                    accountIds.addAll(tempAccountIds);
                }
            }
        }
        if(null!=request.getAccountIds()) {
            if (flag) {
                //取交集
                accountIds.retainAll(request.getAccountIds());
            } else {
                //否则全部查询
                accountIds.addAll(request.getAccountIds());
            }
        }
        /*if (check || accountIds == null || accountIds.size() == 0) {
            accountIds.clear();
            accountIds.add("fill");
        }*/

        //6、查询列表
        LambdaQueryWrapper<AccountInstance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(request.getAccountId()), AccountInstance::getAccountId, request.getAccountId())
                .in(accountIds != null && accountIds.size() > 0, AccountInstance::getAccountId, accountIds)
                .like(StringUtils.isNotEmpty(request.getAccountName()), AccountInstance::getAccountName, request.getAccountName())
                .eq(StringUtils.isNotEmpty(request.getSource()), AccountInstance::getSource, request.getSource())
                .like(StringUtils.isNotEmpty(request.getPhone()), AccountInstance::getPhone, request.getPhone())
                .eq(StringUtils.isNotEmpty(request.getAppId()), AccountInstance::getAppId, request.getAppId())
                .eq(request.getStatus() != null, AccountInstance::getStatus, request.getStatus())
                .eq(request.getDt() != null, AccountInstance::getDt, request.getDt())
                .gt(request.getStartTime() != null, AccountInstance::getDt, request.getStartTime())
                .lt(request.getEndTime() != null, AccountInstance::getDt, request.getEndTime())
                .orderByDesc(AccountInstance::getDt);
        Page<AccountInstance> page = new Page<>(request.getPageNo(), request.getPageSize());
        IPage<AccountInstance> instancePage = accountInstanceService.page(page, queryWrapper);
        //7、属性赋值
        List<AccountInstanceResponse> voList = new ArrayList<>();
        if (instancePage.getRecords() != null && instancePage.getRecords().size() > 0) {
            instancePage.getRecords().forEach(model -> {
                AccountInstanceResponse vo = new AccountInstanceResponse();
                BeanUtils.copyProperties(model, vo);
                vo.setId(model.getId().toString());
                //7.1、设置姓名、性别
                //7.1、1 根据accountId获取userId
                AccountUser user = accountUserService.lambdaQuery()
                        .eq(AccountUser::getAccountId, model.getAccountId())
                        .one();
                if (!ReflectUtil.isObjectNull(user) && user != null) {
                    UserInstanceResponse instance = userInstanceApi.getUserInstanceByUserId(user.getUserId());
                    if (!ReflectUtil.isObjectNull(instance)) {
                        if (StringUtils.isNotEmpty(instance.getName())) {
                            vo.setUserName(instance.getName());
                        }
                        if (StringUtils.isNotEmpty(instance.getGender())) {
                            vo.setGender(instance.getGender());
                        }
                    }
                }
                //7.1.2、设置机构信息
                List<AccountGroup> groupList = accountGroupService.lambdaQuery()
                        .eq(AccountGroup::getAccountId, model.getAccountId())
                        .list();
                if (groupList != null && groupList.size() > 0) {
                    if (StringUtils.isNotEmpty(groupList.get(0).getOrgName())) {
                        vo.setOrgName(groupList.get(0).getOrgName());
                        vo.setOrgId(groupList.get(0).getOrgId());
                    }
                    AccountGroupInfo groupInfo = accountGroupInfoService.lambdaQuery()
                            .eq(AccountGroupInfo::getOrgId, groupList.get(0).getOrgId())
                            .one();
                    if (!ReflectUtil.isObjectNull(groupInfo) && groupInfo != null) {
                        if (StringUtils.isNotEmpty(groupInfo.getGroupInfoId())) {
                            vo.setGroupInfoId(groupInfo.getGroupInfoId());
                        }
                    }
                }
                //7.1.3 设置角色信息
                AccountRole accountRole = accountRoleService.lambdaQuery()
                        .eq(AccountRole::getPrincipalId, model.getAccountId())
                        .one();
                if (!ReflectUtil.isObjectNull(accountRole) && accountRole != null) {
                    if (StringUtils.isNotEmpty(accountRole.getRoleName())) {
                        vo.setRoleName(accountRole.getRoleName());
                    }
                }
                voList.add(vo);
            });
        }
        //8、复制
        IPage<AccountInstanceResponse> voPage = new Page<>();
        BeanUtils.copyProperties(instancePage, voPage, new String[]{"records"});
        voPage.setRecords(voList);
        return voPage;
    }


}
