package org.dows.hep.biz.base.org;

import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.account.api.*;
import org.dows.account.biz.enums.EnumAccountStatusCode;
import org.dows.account.biz.exception.AccountException;
import org.dows.account.request.*;
import org.dows.account.response.*;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.enums.EnumCaseFee;
import org.dows.hep.api.exception.CaseFeeException;
import org.dows.hep.api.user.organization.request.CaseOrgRequest;
import org.dows.hep.api.user.organization.response.CaseOrgResponse;
import org.dows.hep.biz.base.indicator.CaseIndicatorInstanceBiz;
import org.dows.hep.entity.CaseOrgEntity;
import org.dows.hep.entity.CaseOrgFeeEntity;
import org.dows.hep.entity.CasePersonEntity;
import org.dows.hep.entity.HepArmEntity;
import org.dows.hep.service.CaseOrgFeeService;
import org.dows.hep.service.CaseOrgService;
import org.dows.hep.service.CasePersonService;
import org.dows.hep.service.HepArmService;
import org.dows.sequence.api.IdGenerator;
import org.dows.user.api.api.UserExtinfoApi;
import org.dows.user.api.api.UserInstanceApi;
import org.dows.user.api.request.UserExtinfoRequest;
import org.dows.user.api.request.UserInstanceRequest;
import org.dows.user.api.response.UserExtinfoResponse;
import org.dows.user.api.response.UserInstanceResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private final CaseOrgService caseOrgService;
    private final CasePersonService casePersonService;
    private final UserExtinfoApi userExtinfoApi;
    private final HepArmService hepArmService;
    private final AccountRoleApi accountRoleApi;
    private final CaseIndicatorInstanceBiz caseIndicatorInstanceBiz;

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
    public String addClass(AccountOrgRequest request, String accountId,String loginId) {
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
        //5、只创建教师和班级的映射关系
        AccountRoleResponse roleResponse = accountRoleApi.getAccountRoleByPrincipalId(loginId);
        if(roleResponse.getRoleName().equals("教师")) {
            HepArmEntity hepArmEntity = HepArmEntity
                    .builder()
                    .armId(idGenerator.nextIdStr())
                    .accountId(loginId)
                    .orgId(orgId)
                    .tenantId(request.getTenantId())
                    .appId(request.getAppId())
                    .build();
            hepArmService.save(hepArmEntity);
        }
        if(roleResponse.getRoleName().equals("管理员")) {
            HepArmEntity hepArmEntity = HepArmEntity
                    .builder()
                    .armId(idGenerator.nextIdStr())
                    .accountId(accountId)
                    .orgId(orgId)
                    .tenantId(request.getTenantId())
                    .appId(request.getAppId())
                    .build();
            hepArmService.save(hepArmEntity);
        }
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
        //1、获取机构下的所有学生，不删除老师
        Set<String> accountIds = new HashSet<>();
        ids.forEach(id -> {
            List<AccountGroupResponse> groupList = accountGroupApi.getAccountGroupByOrgId(id);
            if (groupList != null && groupList.size() > 0) {
                groupList.forEach(group -> {
                    //2、删除学生，不删除教师
                    List<AccountGroupInfoResponse> infoList = accountGroupInfoApi.getAccountGroupInfoByOrgIdAndAccountId(id,group.getAccountId());
                    if(infoList == null || infoList.size() == 0) {
                        //2.1、说明不是教师，是学生
                        accountIds.add(group.getAccountId());
                    }
                });
            }
            //3、删除映射关系
            List<HepArmEntity> hepArmList = hepArmService.lambdaQuery()
                    .eq(HepArmEntity::getOrgId, id)
                    .eq(HepArmEntity::getDeleted, false)
                    .list();
            if(hepArmList != null && hepArmList.size() > 0) {
                LambdaUpdateWrapper<HepArmEntity> armWrapper = Wrappers.lambdaUpdate(HepArmEntity.class);
                armWrapper.set(HepArmEntity::getDeleted, true)
                        .eq(HepArmEntity::getOrgId, id);
                boolean flag3 = hepArmService.update(armWrapper);
                if (!flag3) {
                    throw new AccountException(EnumAccountStatusCode.ACCOUNT_UPDATE_FAIL_EXCEPTION);
                }
            }
        });
        //4、删除组织架构
        Integer count1 = accountOrgApi.batchDeleteAccountOrgs(ids);
        if (count1 == 0) {
            flag = false;
        }
        //5、删除账户实例
        if(accountIds != null && accountIds.size() > 0) {
            Integer count2 = accountInstanceApi.deleteAccountInstanceByAccountIds(accountIds);
            if (count2 == 0) {
                flag = false;
            }
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
    public IPage<AccountOrgResponse> listClasss(AccountOrgRequest request,String accountId) {
        //1、如果是教师，只能查看该教师下面的班级
        if(StringUtils.isNotEmpty(accountId)){
          List<String>  split = Arrays.asList(accountId.split(","));
          List<HepArmEntity> armList = hepArmService.lambdaQuery().in(HepArmEntity::getAccountId,split)
                  .eq(HepArmEntity::getDeleted,false)
                  .list();
          Set<String> ids = new HashSet<>();
          if(armList != null && armList.size() > 0){
              armList.forEach(arm->{
                  ids.add(arm.getOrgId());
              });
          }else{
              ids.add("fill");
          }
            request.setIds(ids);
        }
        IPage<AccountOrgResponse> accountOrgResponse = accountOrgApi.customAccountOrgList(request);
        //2、总人数剔除教师一个
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
     * @关联表: account_org、case_org_fee、account_org_geo、case_person、case_org、account_org_info
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/28 11:50
     */
    @DSTransactional
    public String addOrgnization(AccountOrgRequest request, String caseInstanceId, String ver, String caseIdentifier) {
        //1、创建机构
        String feeJson = request.getDescr();
        request.setDescr("");
        String orgId = accountOrgApi.createAccountOrg(request);
        //2、创建案例与机构关系
        String caseOrgId = idGenerator.nextIdStr();
        CaseOrgEntity entity = CaseOrgEntity.builder()
                .appId(request.getAppId())
                .caseOrgId(caseOrgId)
                .caseInstanceId(caseInstanceId)
                .orgId(orgId)
                .orgName(request.getOrgName())
                .scene(request.getProfile())
                .handbook(request.getOperationManual())
                .ver(ver)
                .caseIdentifier(caseIdentifier)
                .build();
        caseOrgService.save(entity);
        //3、创建机构费用明细
        List<CaseOrgFeeEntity> caseOrgList = JSONUtil.toList(feeJson, CaseOrgFeeEntity.class);
        caseOrgList.forEach(caseOrg -> {
            caseOrg.setAppId(request.getAppId());
            caseOrg.setCaseOrgId(caseOrgId);
            caseOrg.setCaseOrgFeeId(idGenerator.nextIdStr());
        });
        caseOrgFeeService.saveBatch(caseOrgList);
        //4、创建机构点位
        AccountOrgGeoRequest geoRequest = AccountOrgGeoRequest
                .builder()
                .orgId(orgId)
                .orgName(request.getOrgName())
                .orgLongitude(request.getOrgLongitude())
                .orgLatitude(request.getOrgLatitude())
                .build();
        accountOrgGeoApi.insertOrgGeo(geoRequest);
        return caseOrgId;
    }

    /**
     * @param
     * @return
     * @说明: 添加机构人物
     * @关联表: account_group、account_org、account_group、case_org、case_person、account_instance
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/04 14:30
     */
    @DSTransactional
    public Integer addPerson(Set<String> personIds, String caseInstanceId, String caseOrgId, String appId) {
        //1、通过案例机构ID找到机构ID
        CaseOrgEntity entity = caseOrgService.lambdaQuery()
                .eq(CaseOrgEntity::getCaseOrgId, caseOrgId)
                .eq(CaseOrgEntity::getDeleted, false)
                .eq(CaseOrgEntity::getAppId, appId)
                .one();
        Integer count = 0;
//        //2、复制人物，每个机构的人物都是机构独有的
//        Set<String> ids = new HashSet<>();
//        personIds.forEach(id->{
//            //2.1、获取用户信息及简介并创建新用户及简介
//            AccountUserResponse accountUser = accountUserApi.getUserByAccountId(id);
//            UserInstanceResponse userInstanceResponse = userInstanceApi.getUserInstanceByUserId(accountUser.getUserId());
//            UserExtinfoResponse userExtinfoResponse = userExtinfoApi.getUserExtinfoByUserId(accountUser.getUserId());
//            UserInstanceRequest userInstanceRequest = new UserInstanceRequest();
//            BeanUtils.copyProperties(userInstanceResponse,userInstanceRequest,new String[]{"id","accountId"});
//            String userId = userInstanceApi.insertUserInstance(userInstanceRequest);
//            UserExtinfoRequest userExtinfo = UserExtinfoRequest.builder()
//                    .userId(userId)
//                    .intro(userExtinfoResponse.getIntro())
//                    .build();
//            String extinfoId = userExtinfoApi.insertUserExtinfo(userExtinfo);
//            //2.2、获取该账户的所有信息
//            AccountInstanceResponse accountInstanceResponse = accountInstanceApi.getAccountInstanceByAccountId(id);
//            //2.3、复制账户信息
//            AccountInstanceRequest accountInstanceRequest = AccountInstanceRequest.builder()
//                    .appId(accountInstanceResponse.getAppId())
//                    .avatar(accountInstanceResponse.getAvatar())
//                    .status(accountInstanceResponse.getStatus())
//                    .source("机构人物")
//                    .principalType(accountInstanceResponse.getPrincipalType())
//                    .identifier(createCode(7))
//                    .accountName(randomWord(6))
//                    .build();
//            AccountInstanceResponse vo = accountInstanceApi.createAccountInstance(accountInstanceRequest);
//            //2.4、创建账户和用户之间的关联关系
//            AccountUserRequest accountUserRequest = AccountUserRequest.builder()
//                    .accountId(vo.getAccountId())
//                    .userId(userId)
//                    .appId(accountInstanceResponse.getAppId())
//                    .tentantId(accountInstanceResponse.getTenantId()).build();
//            this.accountUserApi.createAccountUser(accountUserRequest);
//            //todo 复制指标信息和突发事件
//            StringBuffer sb = new StringBuffer();
//            sb.append(vo.getAccountId());
//            sb.append(",");
//            sb.append(id);
//            ids.add(sb.toString());
//        });
        //3、uim中将人物放到对应小组
        /* runsix: TODO 优化 for http */
        for (String personId : personIds) {
            AccountInstanceResponse instanceResponse = accountInstanceApi.getAccountInstanceByAccountId(personId);
            AccountOrgResponse orgResponse = accountOrgApi.getAccountOrgByOrgId(entity.getOrgId(), appId);
            AccountGroupRequest request = AccountGroupRequest
                    .builder()
                    .orgId(entity.getOrgId())
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
            //4、沙盘中将人物放到案例小组
            String casePersonId = idGenerator.nextIdStr();
            CasePersonEntity person = CasePersonEntity.builder()
                    .casePersonId(casePersonId)
                    .caseInstanceId(caseInstanceId)
                    .caseOrgId(caseOrgId)
                    .accountId(personId)
                    .build();
//            caseIndicatorInstanceBiz.copyPersonIndicatorInstance(CaseCreateCopyToPersonRequestRs
//                .builder()
//                .appId(appId)
//                .principalId(personId)
//                .build());
            casePersonService.save(person);
        }
        return count;
    }

    /**
     * @param
     * @return
     * @说明: 机构人物列表
     * @关联表: account_group、account_org、account_group、case_org
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/04 14:30
     */
    public IPage<AccountGroupResponse> listPerson(AccountGroupRequest request, String caseOrgId) {
        //1、获取该案例机构对应的机构ID
        CaseOrgEntity entity = caseOrgService.lambdaQuery()
                .eq(CaseOrgEntity::getCaseOrgId, caseOrgId)
                .eq(CaseOrgEntity::getDeleted, false)
                .eq(CaseOrgEntity::getAppId, request.getAppId())
                .one();
        Set<String> orgIds = new HashSet<>();
        orgIds.add(entity.getOrgId());
        request.setOrgIds(orgIds);
        return accountGroupApi.customAccountGroupList(request);
    }

    /**
     * @param
     * @return
     * @说明: 查看机构基本信息
     * @关联表: account_org、case_org_fee、account_org_geo、account_org_info、case_org
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/04 16:04
     */
    public AccountOrgResponse getOrg(String caseOrgId, String appId) {
        //1、获取该案例机构对应的机构ID
        CaseOrgEntity entity = caseOrgService.lambdaQuery()
                .eq(CaseOrgEntity::getCaseOrgId, caseOrgId)
                .eq(CaseOrgEntity::getDeleted, false)
                .eq(CaseOrgEntity::getAppId, appId)
                .one();
        //2、获取机构实例
        AccountOrgResponse orgResponse = accountOrgApi.getAccountOrgByOrgId(entity.getOrgId(), appId);
        //3、获取机构基本信息
        AccountOrgInfoResponse orgInfoResponse = accountOrgApi.getAccountOrgInfoByOrgId(entity.getOrgId());
        orgResponse.setOperationManual(orgInfoResponse.getOperationManual());
        orgResponse.setIsEnable(orgInfoResponse.getIsEnable());
        //4、获取机构地理位置信息
        AccountOrgGeoResponse orgGeoResponse = accountOrgGeoApi.getAccountOrgInfoByOrgId(entity.getOrgId());
        orgResponse.setOrgLongitude(orgGeoResponse.getOrgLongitude());
        orgResponse.setOrgLatitude(orgGeoResponse.getOrgLatitude());
        //5、获取机构费用列表
        List<CaseOrgFeeEntity> caseOrgFeeList = caseOrgFeeService.lambdaQuery()
                .eq(CaseOrgFeeEntity::getCaseOrgId, entity.getCaseOrgId())
                .eq(CaseOrgFeeEntity::getDeleted, false)
                .list();
        orgResponse.setDescr(JSONUtil.toJsonStr(caseOrgFeeList));
        return orgResponse;
    }

    /**
     * @param
     * @return
     * @说明: 编辑机构基本信息
     * @关联表: account_org、case_org_fee、account_org_geo、account_org_info、case_org
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/05 09:00
     */
    @DSTransactional
    public Boolean editOrg(AccountOrgRequest request, String caseOrgId, String ver, String caseIdentifier) {
        //1、获取该案例机构对应的机构ID
        CaseOrgEntity entity = caseOrgService.lambdaQuery()
                .eq(CaseOrgEntity::getCaseOrgId, caseOrgId)
                .eq(CaseOrgEntity::getDeleted, false)
                .eq(CaseOrgEntity::getAppId, request.getAppId())
                .one();
        //2、更新机构实例
        request.setOrgId(entity.getOrgId());
        Boolean flag1 = accountOrgApi.updateAccountOrgByOrgId(request);
        //3、更新案例机构实例
        CaseOrgEntity entity1 = CaseOrgEntity.builder().orgName(request.getOrgName())
                .scene(request.getProfile())
                .handbook(request.getOperationManual())
                .ver(ver)
                .caseIdentifier(caseIdentifier)
                .id(entity.getId())
                .build();
        boolean orgFlag = caseOrgService.updateById(entity1);
        //4、更新机构地理信息
        if (request.getOrgLatitude() != null && request.getOrgLongitude() != null) {
            AccountOrgGeoRequest geoRequest = AccountOrgGeoRequest.builder()
                    .orgId(request.getOrgId())
                    .orgLatitude(request.getOrgLatitude())
                    .orgLongitude(request.getOrgLongitude())
                    .build();
            Boolean flag2 = accountOrgGeoApi.updateAccountOrgGeoByOrgId(geoRequest);
        }
        //5、更新机构费用信息
        Boolean flag3 = false;
        if (StringUtils.isNotEmpty(request.getDescr())) {
            List<CaseOrgFeeEntity> caseOrgList = JSONUtil.toList(request.getDescr(), CaseOrgFeeEntity.class);
            flag3 = caseOrgFeeService.updateBatchById(caseOrgList);
        }
        return flag3;
    }

    /**
     * @param
     * @return
     * @说明: 判断机构名称
     * @关联表: account_org
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/05 09:00
     */
    public Boolean checkOrg(String orgCode, String appId, String orgName) {
        return accountOrgApi.checkOrgIsExist(orgCode, appId, orgName);
    }

    /**
     * @param
     * @return
     * @说明: 删除机构基本信息
     * @关联表: account_org、case_org_fee、account_org_geo、account_org_info、case_org
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/05 09:00
     */
    @DSTransactional
    public Boolean deleteOrgs(Set<String> caseOrgIds,String caseInstanceId, String appId) {
        //1、获取该案例机构对应的机构ID
        List<CaseOrgEntity> entityList = caseOrgService.lambdaQuery()
                .in(caseOrgIds != null && caseOrgIds.size() > 0 ,CaseOrgEntity::getCaseOrgId, caseOrgIds)
                .eq(CaseOrgEntity::getCaseInstanceId,caseInstanceId)
                .eq(CaseOrgEntity::getDeleted, false)
                .eq(CaseOrgEntity::getAppId, appId)
                .list();
        if (entityList != null && entityList.size() > 0) {
            Set<String> orgIds = new HashSet<>();
            entityList.forEach(entity -> {
                orgIds.add(entity.getOrgId());
                //2、删除案例机构关系表
                LambdaUpdateWrapper<CaseOrgEntity> orgWrapper = Wrappers.lambdaUpdate(CaseOrgEntity.class);
                orgWrapper.set(CaseOrgEntity::getDeleted, true)
                        .eq(CaseOrgEntity::getCaseOrgId, entity.getCaseOrgId())
                        .eq(CaseOrgEntity::getCaseInstanceId, caseInstanceId);
                boolean flag1 = caseOrgService.update(orgWrapper);
                //3、删除案例人物关系表
                LambdaUpdateWrapper<CasePersonEntity> personWrapper = Wrappers.lambdaUpdate(CasePersonEntity.class);
                personWrapper.set(CasePersonEntity::getDeleted, true)
                        .eq(CasePersonEntity::getCaseOrgId, entity.getCaseOrgId())
                        .eq(CasePersonEntity::getCaseInstanceId, caseInstanceId);
                boolean flag2 = caseOrgService.update(orgWrapper);
                //4、删除组织机构费用表
                List<CaseOrgFeeEntity> feeList = caseOrgFeeService.lambdaQuery()
                        .eq(CaseOrgFeeEntity::getCaseOrgId, entity.getCaseOrgId())
                        .eq(CaseOrgFeeEntity::getDeleted, false)
                        .list();
                if (feeList != null && feeList.size() > 0) {
                    LambdaUpdateWrapper<CaseOrgFeeEntity> feeWrapper = Wrappers.lambdaUpdate(CaseOrgFeeEntity.class);
                    feeWrapper.set(CaseOrgFeeEntity::getDeleted, true)
                            .eq(CaseOrgFeeEntity::getCaseOrgId, entity.getCaseOrgId());
                    boolean flag = caseOrgFeeService.update(feeWrapper);
                    if (!flag) {
                        throw new CaseFeeException(EnumCaseFee.CASE_FEE_UPDATE_EXCEPTION);
                    }
                }
            });
            //5、删除组织机构
            accountOrgApi.batchDeleteAccountOrgsByOrgIds(orgIds);
            //6、删除组织机构地理位置
            accountOrgGeoApi.batchDeleteAccountOrgGeosByOrgIds(orgIds);
        }
        //7、todo 删除功能点
        return true;
    }

    /**
     * @param
     * @return
     * @说明: 删除机构人物
     * @关联表: account_group、case_person、case_org
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/05 10:00
     */
    /* runsix: TODO 优化这里是删除机构绑定人物的，有问题 */
    @DSTransactional
    public Boolean deletePersons(Set<String> caseOrgIds,String caseInstanceId, Set<String> accountIds,String appId) {
        //1、如果是自定义人物，需要删除人物实例
        Set<String> userIds = new HashSet<>();
        Set<String> newAccountIds = new HashSet<>();
        accountIds.forEach(accountId->{
            AccountInstanceResponse instanceResponse = accountInstanceApi.getAccountInstanceByAccountId(accountId);
            if(instanceResponse != null && instanceResponse.getSource().equals("机构人物")){
                userIds.add(accountUserApi.getUserByAccountId(accountId).getUserId());
                newAccountIds.add(accountId);
            }
        });
        if(newAccountIds != null && newAccountIds.size() > 0){
            accountInstanceApi.deleteAccountInstanceByAccountIds(accountIds);
        }
        if(userIds != null && userIds.size() > 0){
            userIds.forEach(userId -> {
                UserExtinfoResponse extinfoResponse = userExtinfoApi.getUserExtinfoByUserId(userId);
                userExtinfoApi.deleteUserExtinfoById(extinfoResponse.getId());
            });
        }
        //2、获取该案例机构对应的机构ID
        List<CaseOrgEntity> entityList = caseOrgService.lambdaQuery()
                .in(CaseOrgEntity::getCaseOrgId, caseOrgIds)
                .eq(CaseOrgEntity::getCaseInstanceId,caseInstanceId)
                .eq(CaseOrgEntity::getDeleted, false)
                .eq(CaseOrgEntity::getAppId, appId)
                .list();
        if (entityList != null && entityList.size() > 0) {
            Set<String> orgIds = new HashSet<>();
            entityList.forEach(entity -> {
                orgIds.add(entity.getOrgId());
                //2.1、删除案例机构下的成员
                LambdaUpdateWrapper<CasePersonEntity> personWrapper = Wrappers.lambdaUpdate(CasePersonEntity.class);
                personWrapper.set(CasePersonEntity::getDeleted, true)
                        .eq(CasePersonEntity::getCaseOrgId, entity.getCaseOrgId())
                        .eq(CasePersonEntity::getCaseInstanceId, caseInstanceId);
                boolean flag1 = casePersonService.update(personWrapper);
            });
            //2.2、获取该机构下的成员并删除
            for (String orgId : orgIds) {
                List<AccountGroupResponse> groupResponseList = accountGroupApi.getAccountGroupByOrgId(orgId);
                if (groupResponseList != null && groupResponseList.size() > 0) {
                    Set<String> ids = new HashSet<>();
                    groupResponseList.forEach(group -> {
                        ids.add(group.getId());
                    });
                    accountGroupApi.batchDeleteGroups(ids, accountIds);
                }
            }
        }
        return true;
    }

    /**
     * @param
     * @return
     * @说明: 同一案例中，人物不能被多个机构共享
     * @关联表: case_person
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/05 10:00
     */
    public Boolean checkInstancePerson(String caseOrgId, String caseInstanceId, String accountId) {
        CasePersonEntity entity = casePersonService.lambdaQuery()
                .eq(CasePersonEntity::getCaseOrgId, caseOrgId)
                .eq(CasePersonEntity::getCaseInstanceId,caseInstanceId)
                .eq(CasePersonEntity::getDeleted, false)
                .eq(CasePersonEntity::getAccountId, accountId)
                .one();
        if(entity != null){
            return false;
        }
        return true;
    }

    /**
     * @param
     * @return
     * @说明: 复制机构人物
     * @关联表: case_person
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/05 10:00
     */
    @DSTransactional
    public String copyPerson(String caseOrgId, String caseInstanceId, String accountId) {
        //1、机构内人物复制
        //1.1、获取用户信息及简介并创建新用户及简介
        AccountUserResponse accountUser = accountUserApi.getUserByAccountId(accountId);
        UserInstanceResponse userInstanceResponse = userInstanceApi.getUserInstanceByUserId(accountUser.getUserId());
        UserExtinfoResponse userExtinfoResponse = userExtinfoApi.getUserExtinfoByUserId(accountUser.getUserId());
        UserInstanceRequest userInstanceRequest = new UserInstanceRequest();
        BeanUtils.copyProperties(userInstanceResponse,userInstanceRequest,new String[]{"id","accountId"});
        String userId = userInstanceApi.insertUserInstance(userInstanceRequest);
        UserExtinfoRequest userExtinfo = UserExtinfoRequest.builder()
                .userId(userId)
                .intro(userExtinfoResponse.getIntro())
                .build();
        String extinfoId = userExtinfoApi.insertUserExtinfo(userExtinfo);
        //1.2、获取该账户的所有信息
        AccountInstanceResponse accountInstanceResponse = accountInstanceApi.getAccountInstanceByAccountId(accountId);
        //1.3、复制账户信息
        AccountInstanceRequest accountInstanceRequest = AccountInstanceRequest.builder()
                .appId(accountInstanceResponse.getAppId())
                .avatar(accountInstanceResponse.getAvatar())
                .status(accountInstanceResponse.getStatus())
                .source("机构人物")
                .principalType(accountInstanceResponse.getPrincipalType())
                .identifier(createCode(7))
                .accountName(randomWord(6))
                .build();
        AccountInstanceResponse vo = accountInstanceApi.createAccountInstance(accountInstanceRequest);
        //1.4、创建账户和用户之间的关联关系
        AccountUserRequest accountUserRequest = AccountUserRequest.builder()
                .accountId(vo.getAccountId())
                .userId(userId)
                .appId(accountInstanceResponse.getAppId())
                .tentantId(accountInstanceResponse.getTenantId()).build();
        this.accountUserApi.createAccountUser(accountUserRequest);
        //2、机构与人物关系复制一份
        String casePersonId = idGenerator.nextIdStr();
        CasePersonEntity person = CasePersonEntity.builder()
                .casePersonId(casePersonId)
                .caseInstanceId(caseInstanceId)
                .caseOrgId(caseOrgId)
                .accountId(vo.getAccountId())
                .build();
        casePersonService.save(person);
        return vo.getAccountId();
    }

    /**
     * @param
     * @return
     * @说明: 获取案例机构 分页
     * @关联表: case_org
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/06 10:00
     */
    public IPage<CaseOrgResponse> listOrgnization(CaseOrgRequest request) {
        //1、实验的时候获取已发布的机构,案例获取全部，不需要传status
        Set<String> orgIds = new HashSet<>();
        if(request.getStatus() != null && request.getStatus() == 1) {
            List<AccountOrgResponse> orgList = accountOrgApi.getValidAccountOrgList(request.getStatus());
            if(orgList != null && orgList.size() > 0) {
                orgList.forEach(org -> {
                    orgIds.add(org.getOrgId());
                });
            }
        }
        //2、查询
        LambdaQueryWrapper<CaseOrgEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(request.getOrgId()), CaseOrgEntity::getOrgId, request.getOrgId())
                .in(orgIds != null && orgIds.size() > 0,CaseOrgEntity::getOrgId,orgIds)
                .like(StringUtils.isNotEmpty(request.getOrgName()), CaseOrgEntity::getOrgName, request.getOrgName())
                .eq(StringUtils.isNotEmpty(request.getCaseInstanceId()), CaseOrgEntity::getCaseInstanceId, request.getCaseInstanceId())
                .orderByDesc(CaseOrgEntity::getDt);
        Page<CaseOrgEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        IPage<CaseOrgEntity> orgList = caseOrgService.page(page, queryWrapper);
        //复制属性
        IPage<CaseOrgResponse> pageVo = new Page<>();
        BeanUtils.copyProperties(orgList, pageVo, new String[]{"records"});
        List<CaseOrgResponse> voList = new ArrayList<>();
        if (orgList.getRecords() != null && orgList.getRecords().size() > 0) {
            orgList.getRecords().forEach(model -> {
                CaseOrgResponse vo = new CaseOrgResponse();
                BeanUtils.copyProperties(model, vo);
                vo.setId(model.getId().toString());
                //获取机构经纬度
                AccountOrgGeoResponse orgGeoResponse = accountOrgGeoApi.getAccountOrgInfoByOrgId(model.getOrgId());
                vo.setOrgLongitude(orgGeoResponse.getOrgLongitude());
                vo.setOrgLatitude(orgGeoResponse.getOrgLatitude());
                voList.add(vo);
            });
        }
        pageVo.setRecords(voList);
        return pageVo;
    }

    /**
     * @param
     * @return
     * @说明: 将自定义人物添加到案例机构中
     * @关联表: case_person、account_group
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/6/07 14:18
     */
    @DSTransactional
    public String addPersonToCaseOrg(String personId, String caseInstanceId, String caseOrgId, String appId) {
        //1、建立人物与案例机构关系
        String casePersonId = idGenerator.nextIdStr();
        CasePersonEntity person = CasePersonEntity.builder()
                .casePersonId(casePersonId)
                .caseInstanceId(caseInstanceId)
                .caseOrgId(caseOrgId)
                .accountId(personId)
                .build();
        casePersonService.save(person);
        //2、建立人物与组关系
        //2.1、通过案例机构ID找到机构ID
        CaseOrgEntity entity = caseOrgService.lambdaQuery()
                .eq(CaseOrgEntity::getCaseOrgId, caseOrgId)
                .eq(CaseOrgEntity::getDeleted, false)
                .eq(CaseOrgEntity::getAppId, appId)
                .one();
        //2.2、账户实例
        AccountInstanceResponse instanceResponse = accountInstanceApi.getAccountInstanceByAccountId(personId);
        //2.3、获取用户ID
        String userId = accountUserApi.getUserByAccountId(personId).getUserId();
        String groupId = accountGroupApi.insertAccountGroup(AccountGroupRequest.builder()
                .groupId(idGenerator.nextIdStr())
                .orgId(entity.getOrgId())
                .orgName(entity.getOrgName())
                .accountId(personId)
                .accountName(instanceResponse.getAccountName())
                .userId(userId)
                .appId(appId)
                .build());
        return person.getCasePersonId();
    }

    /**
     * @param
     * @return
     * @说明: 通过案例人物ID获取accountId
     * @关联表: case_person
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/6/07 14:18
     */
    public String getAccountIdByCasePerson(String casePersonId) {
        CasePersonEntity entity = casePersonService.lambdaQuery()
                .eq(CasePersonEntity::getCasePersonId, casePersonId)
                .eq(CasePersonEntity::getDeleted, false)
                .one();
        if(entity == null || ReflectUtil.isObjectNull(entity)){
            throw new AccountException(EnumAccountStatusCode.ACCOUNT_NOT_EXIST_EXCEPTION);
        }
        return entity.getAccountId();
    }

    /**
     * @param
     * @return
     * @说明: 生成 随机机构编码
     */
    public static String createCode(int n) {
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

    /**
     * 生成随机账号
     */
    public static String randomWord(int length) {
        Random random = new Random();
        StringBuilder word = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            word.append((char) ('a' + random.nextInt(26)));
        }

        return word.toString();
    }
}
