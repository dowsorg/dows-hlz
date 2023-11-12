package org.dows.hep.biz.extend.uim;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.account.biz.enums.EnumAccountRolePrincipalType;
import org.dows.account.entity.*;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.request.AccountOrgRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.account.service.*;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.HepArmEntity;
import org.dows.hep.service.HepArmService;
import org.dows.user.entity.UserInstance;
import org.dows.user.service.UserInstanceService;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/11/8 15:17
 */
@Service
@RequiredArgsConstructor
public class XOrgBiz {
    private final HepArmService hepArmService;

    private final AccountOrgService accountOrgService;

    private final CoreBiz coreBiz;
    public IPage<AccountOrgResponse> listClasss(AccountOrgRequest request, String accountId){
        IPage<AccountOrgResponse> rst= coreBiz.listClasss(request, getOrgIds(accountId));
        Set<String> orgIds=ShareUtil.XCollection.toSet(rst.getRecords(), AccountOrgResponse::getOrgId);
        Map<String,Long> cntTeathers=cntTeachers(orgIds);
        rst.getRecords().forEach(item->{
            Optional.ofNullable(cntTeathers.get(item.getOrgId()))
                    .ifPresent(i->{
                        if(ShareUtil.XObject.isEmpty(item.getCurrentNum())){
                            return;
                        }
                        item.setCurrentNum(Math.max(0, item.getCurrentNum()-i.intValue()));
                    });
        });

        return rst;
    }

    public IPage<AccountInstanceResponse> listTeacherOrStudent(AccountInstanceRequest request, String accountId) {
        return listTeacherOrStudent(request, accountId, null, null);
    }

    public IPage<AccountInstanceResponse> listTeacherOrStudent(AccountInstanceRequest request, String accountId,String sortField,Integer sortDesc){
        return coreBiz.listTeacherOrStudent(request, getOrgIds(accountId), sortField, sortDesc);
    }

    @DS("uim")
    public void checkExitsClassName(AccountOrgRequest req){
        AccountOrg accountOrg = accountOrgService.lambdaQuery()
                .eq(AccountOrg::getOrgName, req.getOrgName())
                .eq(AccountOrg::getOrgType,req.getOrgType())
                .ne(ShareUtil.XObject.notEmpty(req.getOrgId()),AccountOrg::getOrgId,req.getOrgId())
                .eq(AccountOrg::getAppId, req.getAppId())
                .eq(AccountOrg::getDeleted, false)
                .last("limit 1")
                .one();
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(accountOrg))
                .throwMessage("该班级名称已存在");
    }
    private Set<String> getOrgIds(String teacherId){
        if(ShareUtil.XObject.isEmpty(teacherId)){
            return new HashSet<>();
        }
        return ShareUtil.XCollection.toSet(hepArmService.lambdaQuery()
                .in(HepArmEntity::getAccountId, Arrays.asList(teacherId.split(",")))
                .eq(HepArmEntity::getDeleted, false)
                .select(HepArmEntity::getOrgId)
                .list(),HepArmEntity::getOrgId);
    }

    private Map<String,Long> cntTeachers(Set<String> orgIds){
        if(ShareUtil.XObject.isEmpty(orgIds)){
            return Collections.emptyMap();
        }
        return ShareUtil.XCollection.toMap(hepArmService.query()
                .select("org_id","count(1) id")
                .in("org_id",orgIds)
                .groupBy("org_id")
                .list(),HepArmEntity::getOrgId,HepArmEntity::getId);
    }


    @Service
    @RequiredArgsConstructor
    @DS("uim")

    public static class  CoreBiz {


        private final AccountRoleService accountRoleService;

        private final AccountGroupService accountGroupService;

        private final UserInstanceService userInstanceService;

        private final AccountUserService accountUserService;

        private final AccountGroupInfoService accountGroupInfoService;

        private final AccountInstanceService accountInstanceService;

        private final AccountOrgService accountOrgService;

        public IPage<AccountOrgResponse> listClasss(AccountOrgRequest request, Set<String> orgIds) {
            IPage<AccountOrgResponse> rst = Page.of(request.getPageNo(), request.getPageSize());

            LambdaQueryWrapper<AccountOrg> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ShareUtil.XObject.notEmpty(orgIds), AccountOrg::getOrgId, orgIds)
                    .eq(StringUtils.isNotEmpty(request.getAppId()), AccountOrg::getAppId, request.getAppId())
                    .eq(request.getOrgType() != null, AccountOrg::getOrgType, request.getOrgType())
                    .like(StringUtils.isNotEmpty(request.getOrgName()), AccountOrg::getOrgName, request.getOrgName())
                    .eq(request.getPid() != null, AccountOrg::getPid, request.getPid())
                    .like(StringUtils.isNotEmpty(request.getOrgId()), AccountOrg::getOrgId, request.getOrgId())
                    .like(StringUtils.isNotEmpty(request.getOrgCode()), AccountOrg::getOrgCode, request.getOrgCode())
                    .eq(StringUtils.isNotEmpty(request.getNameLetters()), AccountOrg::getNameLetters, request.getNameLetters())
                    .eq(StringUtils.isNotEmpty(request.getProfile()), AccountOrg::getProfile, request.getProfile())

                    .like(StringUtils.isNotEmpty(request.getTenantId()), AccountOrg::getTenantId, request.getTenantId())
                    .like(StringUtils.isNotEmpty(request.getDescr()), AccountOrg::getDescr, request.getDescr())

                    .eq(request.getSorted() != null, AccountOrg::getSorted, request.getSorted())
                    .eq(request.getStatus() != null, AccountOrg::getStatus, request.getStatus())
                    .eq(request.getDt() != null, AccountOrg::getDt, request.getDt())
                    .gt(request.getStartTime() != null, AccountOrg::getDt, request.getStartTime())
                    .lt(request.getEndTime() != null, AccountOrg::getDt, request.getEndTime());

            if (StringUtils.isNotEmpty(request.getOrgCodeType())) {
                if (request.getOrgCodeType().equals("down")) {
                    queryWrapper.orderByDesc(AccountOrg::getOrgCode);
                }
                if (request.getOrgCodeType().equals("up")) {
                    queryWrapper.orderByAsc(AccountOrg::getOrgCode);
                }
            } else if (StringUtils.isNotEmpty(request.getOrgNameType())) {
                if (request.getOrgNameType().equals("down")) {
                    queryWrapper.orderByDesc(AccountOrg::getOrgName);
                }
                if (request.getOrgNameType().equals("up")) {
                    queryWrapper.orderByAsc(AccountOrg::getOrgName);
                }
            } else if (StringUtils.isNotEmpty(request.getDtType())) {
                if (request.getDtType().equals("down")) {
                    queryWrapper.orderByDesc(AccountOrg::getDt);
                }
                if (request.getDtType().equals("up")) {
                    queryWrapper.orderByAsc(AccountOrg::getDt);
                }
            } else {
                queryWrapper.orderByDesc(AccountOrg::getId);
            }
            IPage<AccountOrg> pageOrg= accountOrgService.page(Page.of(request.getPageNo(), request.getPageSize()),queryWrapper);
            orgIds.clear();
            orgIds.addAll(ShareUtil.XCollection.toSet(pageOrg.getRecords(),AccountOrg::getOrgId));
            if(ShareUtil.XObject.isEmpty(orgIds)){
                return rst;
            }
            Map<String,AccountGroupInfo> mapGroup=ShareUtil.XCollection.toMap(accountGroupInfoService.lambdaQuery()
                    .in(AccountGroupInfo::getOrgId,orgIds)
                    .select(AccountGroupInfo::getOrgId,
                            AccountGroupInfo::getAccountId,
                            AccountGroupInfo::getOwner,
                            AccountGroupInfo::getOwnerPhone,
                            AccountGroupInfo::getDescr)
                    .list(),AccountGroupInfo::getOrgId);
            Map<String,Long> cntAccounts=cntAccounts(orgIds);

            List<AccountOrgResponse> data = new ArrayList<>();
            pageOrg.getRecords().forEach(org -> {
                AccountOrgResponse item = new AccountOrgResponse();
                data.add(CopyWrapper.create(item).endFrom(org)
                        .setId(String.valueOf(org.getId()))
                        .setCurrentNum(0));
                Optional.ofNullable(cntAccounts.get(org.getOrgId()))
                        .ifPresent(i -> item.setCurrentNum(i.intValue()));
                Optional.ofNullable(mapGroup.get(org.getOrgId()))
                        .ifPresent(i -> {
                            item.setOwnerAccountId(i.getAccountId())
                                    .setOwnerName(i.getOwner())
                                    .setTelePhone(i.getOwnerPhone())
                                    .setGroupDescr(i.getDescr())
                            ;
                        });

            });
            orgIds.clear();
            mapGroup.clear();
            return rst.setRecords(data)
                    .setCurrent(pageOrg.getCurrent())
                    .setSize(pageOrg.getSize())
                    .setTotal(pageOrg.getTotal());
        }




        public IPage<AccountInstanceResponse> listTeacherOrStudent(AccountInstanceRequest request,  Set<String> orgIds , String sortField,Integer sortDesc) {
            AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(request.getRoleName()))
                    .throwMessage("请选择教师或学生列表");
            IPage<AccountInstanceResponse> rst = Page.of(request.getPageNo(), request.getPageSize());
            Set<String> accountIds = new HashSet<>();
            Map<String, AccountGroup> mapGroup = new HashMap<>();
            Map<String, UserInstance> mapUser = new HashMap<>();

            if (ShareUtil.XObject.anyNotEmpty(orgIds, request.getAccountOrgOrgId(), request.getOrgName())) {
                List<AccountGroup> rowsGroup = accountGroupService.lambdaQuery()
                        .eq(ShareUtil.XObject.notEmpty(request.getAccountOrgOrgId()), AccountGroup::getOrgId, request.getAccountOrgOrgId())
                        .eq(ShareUtil.XObject.isEmpty(request.getAccountOrgOrgId()) && ShareUtil.XObject.notEmpty(request.getOrgName()), AccountGroup::getOrgName, request.getOrgName())
                        .eq(AccountGroup::getDeleted, false)
                        .in(ShareUtil.XObject.notEmpty(orgIds), AccountGroup::getOrgId, orgIds)
                        .select(AccountGroup::getOrgId, AccountGroup::getOrgName, AccountGroup::getAccountId)
                        .list();
                if (ShareUtil.XObject.isEmpty(rowsGroup)) {
                    return rst;
                }
                orgIds.clear();
                accountIds.clear();
                orgIds.addAll(ShareUtil.XCollection.map(rowsGroup, AccountGroup::getOrgId));
                accountIds.addAll(ShareUtil.XCollection.map(rowsGroup, AccountGroup::getAccountId));
                rowsGroup.forEach(i -> mapGroup.computeIfAbsent(i.getAccountId(), k -> i));

            }
            if (ShareUtil.XObject.notEmpty(request.getAccountNamePhone())) {
                userInstanceService.lambdaQuery()
                        .like(UserInstance::getName, request.getAccountNamePhone())
                        .eq(UserInstance::getDeleted, false)
                        .select(UserInstance::getUserId, UserInstance::getName, UserInstance::getGender)
                        .list()
                        .forEach(i -> mapUser.put(i.getUserId(), i));
                if (ShareUtil.XObject.isEmpty(mapUser)) {
                    return rst;
                }
                List<AccountUser> rowsUser = accountUserService.lambdaQuery()
                        .in(AccountUser::getUserId, mapUser.keySet())
                        .eq(AccountUser::getDeleted, false)
                        .select(AccountUser::getAccountId, AccountUser::getUserId)
                        .list();
                if (ShareUtil.XObject.isEmpty(rowsUser)) {
                    return rst;
                }
                if (ShareUtil.XObject.isEmpty(accountIds)) {
                    accountIds.addAll(ShareUtil.XCollection.map(rowsUser, AccountUser::getAccountId));
                } else {
                    accountIds.retainAll(ShareUtil.XCollection.map(rowsUser, AccountUser::getAccountId));
                }
                rowsUser.forEach(i -> mapUser.put(i.getAccountId(), mapUser.get(i.getUserId())));
            }

            IPage<AccountRole> pageAccount = accountRoleService.lambdaQuery()
                    .eq(AccountRole::getPrincipalType, EnumAccountRolePrincipalType.PERSONAL.getCode())
                    //.eq(ShareUtil.XObject.noneEmpty(request.getAppId()), AccountRole::getAppId, request.getAppId())
                    .eq(AccountRole::getRoleName, request.getRoleName())
                    .in(ShareUtil.XObject.notEmpty(accountIds), AccountRole::getPrincipalId, accountIds)
                    .eq(AccountRole::getDeleted, false)
                    .select(AccountRole::getPrincipalId,AccountRole::getRoleId,AccountRole::getRoleName)
                    .orderByDesc(AccountRole::getId)
                    .page(Page.of(request.getPageNo(), request.getPageSize()));
            accountIds.clear();
            accountIds.addAll(ShareUtil.XCollection.map(pageAccount.getRecords(), AccountRole::getPrincipalId));
            if (ShareUtil.XObject.isEmpty(accountIds)) {
                return rst;
            }

            if (ShareUtil.XObject.isEmpty(orgIds)) {
                accountGroupService.lambdaQuery()
                        .in(AccountGroup::getAccountId, accountIds)
                        .eq(AccountGroup::getDeleted, false)
                        .select(AccountGroup::getOrgId, AccountGroup::getOrgName, AccountGroup::getAccountId)
                        .list()
                        .forEach(i -> mapGroup.computeIfAbsent(i.getAccountId(), k -> i));
            }
            if (ShareUtil.XObject.isEmpty(mapUser)) {
                Map<String, String> mapOwner = new HashMap<>();
                accountUserService.lambdaQuery()
                        .in(AccountUser::getAccountId, accountIds)
                        .eq(AccountUser::getDeleted, false)
                        .select(AccountUser::getAccountId, AccountUser::getUserId)
                        .list()
                        .forEach(i -> mapOwner.put(i.getUserId(), i.getAccountId()));
                if(ShareUtil.XObject.notEmpty(mapOwner)){
                    userInstanceService.lambdaQuery()
                            .in(UserInstance::getUserId, mapOwner.keySet())
                            .eq(UserInstance::getDeleted, false)
                            .select(UserInstance::getUserId, UserInstance::getName, UserInstance::getGender)
                            .list()
                            .forEach(i -> mapUser.put(mapOwner.get(i.getUserId()), i));
                }

            }
            Map<String, AccountInstance> mapAccount = ShareUtil.XCollection.toMap(accountInstanceService.lambdaQuery()
                    .in(AccountInstance::getAccountId, accountIds)
                    .eq(ShareUtil.XObject.noneEmpty(request.getAppId()), AccountInstance::getAppId, request.getAppId())
                    .eq(AccountInstance::getDeleted, false)
                    .select(AccountInstance::getId,
                            AccountInstance::getAccountId,
                            AccountInstance::getAccountName,
                            AccountInstance::getAvatar,
                            AccountInstance::getSource,
                            AccountInstance::getAppId,
                            AccountInstance::getStatus)
                    .list(), AccountInstance::getAccountId);

            List<AccountInstanceResponse> data = new ArrayList<>();
            pageAccount.getRecords().forEach(role -> {
                AccountInstanceResponse item = new AccountInstanceResponse();
                data.add(item.setAccountId(role.getPrincipalId())
                        .setRoleId(role.getRoleId())
                        .setRoleName(role.getRoleName()));
                Optional.ofNullable((mapAccount.get(role.getPrincipalId())))
                        .ifPresent(i -> {
                            CopyWrapper.create(item).endFrom(i).setId(String.valueOf(i.getId()));
                        });
                Optional.ofNullable(mapGroup.get(role.getPrincipalId()))
                        .ifPresent(i -> {
                            item.setOrgId(i.getOrgId()).setOrgName(i.getOrgName());
                        });
                Optional.ofNullable(mapUser.get(role.getPrincipalId()))
                        .ifPresent(i -> {
                            item.setUserId(i.getUserId())
                                    .setUserName(i.getName())
                                    .setGender(i.getGender());
                        });

            });
            accountIds.clear();
            orgIds.clear();
            mapAccount.clear();
            mapGroup.clear();
            mapUser.clear();
            if(ShareUtil.XObject.notEmpty(sortField)){
                boolean isDesc=Optional.ofNullable(sortDesc).orElse(0)>0;
                switch (sortField.toLowerCase()){
                    case "username"-> {
                        Comparator cnComparator = Collator.getInstance(java.util.Locale.CHINA);
                        if (isDesc) {
                            data.sort((x, y) -> cnComparator.compare(Optional.ofNullable(y.getUserName()).orElse(""), Optional.ofNullable(x.getUserName()).orElse("")));
                        } else {
                            data.sort((x, y) -> cnComparator.compare(Optional.ofNullable(x.getUserName()).orElse(""), Optional.ofNullable(y.getUserName()).orElse("")));
                        }
                    }

                }
            }

            return rst.setRecords(data)
                    .setCurrent(pageAccount.getCurrent())
                    .setSize(pageAccount.getSize())
                    .setTotal(pageAccount.getTotal());
        }


        private Map<String,Long> cntAccounts(Set<String> orgIds){
            if(ShareUtil.XObject.isEmpty(orgIds)){
                return Collections.emptyMap();
            }
            return ShareUtil.XCollection.toMap(accountGroupService.query()
                    .select("org_id","count(1) id")
                    .in("org_id",orgIds)
                    .groupBy("org_id")
                    .list(),AccountGroup::getOrgId,AccountGroup::getId);
        }

    }
}
