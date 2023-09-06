package org.dows.hep.biz.base.person;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.account.api.*;
import org.dows.account.biz.enums.EnumAccountStatusCode;
import org.dows.account.biz.exception.AccountException;
import org.dows.account.request.AccountGroupInfoRequest;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.request.AccountUserRequest;
import org.dows.account.response.*;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.base.indicator.request.CaseCreateCopyToPersonRequestRs;
import org.dows.hep.api.base.person.request.PersonInstanceRequest;
import org.dows.hep.api.base.person.response.PersonInstanceResponse;
import org.dows.hep.api.tenant.casus.request.CasePersonIndicatorFuncRequest;
import org.dows.hep.biz.base.indicator.CaseIndicatorInstanceBiz;
import org.dows.hep.biz.base.org.OrgBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseEventBiz;
import org.dows.hep.entity.CasePersonEntity;
import org.dows.hep.entity.CasePersonIndicatorFuncEntity;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.HepArmEntity;
import org.dows.hep.service.CasePersonIndicatorFuncService;
import org.dows.hep.service.CasePersonService;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.HepArmService;
import org.dows.sequence.api.IdGenerator;
import org.dows.user.api.api.UserExtinfoApi;
import org.dows.user.api.api.UserInstanceApi;
import org.dows.user.api.request.UserExtinfoRequest;
import org.dows.user.api.request.UserInstanceRequest;
import org.dows.user.api.response.UserExtinfoResponse;
import org.dows.user.api.response.UserInstanceResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
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

    private final IdGenerator idGenerator;

    private final CasePersonIndicatorFuncService casePersonIndicatorFuncService;

    private final HepArmService hepArmService;

    private final CaseIndicatorInstanceBiz caseIndicatorInstanceBiz;

    private final CasePersonService casePersonService;

    private final ExperimentInstanceService experimentInstanceService;

    private final TenantCaseEventBiz tenantCaseEventBiz;

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
    @DSTransactional
    public Integer deletePersons(Set<String> accountIds) {
        //1、如果用户是否存在机构信息，存在则无法删除
        List<CasePersonEntity> personEntityList = casePersonService.lambdaQuery()
                .in(CasePersonEntity::getAccountId,accountIds)
                .eq(CasePersonEntity::getDeleted,false)
                .list();
        if(personEntityList != null && personEntityList.size() > 0){
//            personEntityList.forEach(personEntity ->{
//                casePersonService.lambdaUpdate()
//                        .set(CasePersonEntity::getDeleted,true)
//                        .eq(CasePersonEntity::getId,personEntity.getId())
//                        .update();
//            });
            throw new AccountException("存在用户被机构所引用，无法删除");
        }
        //2、通过账户ID找到用户ID
        Set<String> userIds = new HashSet<>();
        accountIds.forEach(accountId -> {
            userIds.add(accountUserApi.getUserByAccountId(accountId).getUserId());
        });
        //3、删除账户实例
        Integer count = accountInstanceApi.deleteAccountInstanceByAccountIds(accountIds);
        //4、删除小组信息
//        accountIds.forEach(accountId ->{
//            List<AccountGroupResponse> groupResponseList = accountGroupApi.getAccountGroupListByAccountId(accountId,"3");
//            if(groupResponseList != null && groupResponseList.size() > 0){
//                Set<String> ids = new HashSet<>();
//                groupResponseList.forEach(groupResponse -> {
//                    ids.add(groupResponse.getId());
//                });
//                accountGroupApi.batchDeleteGroups(ids);
//            }
//        });
        //5、删除用户扩展信息
        userIds.forEach(userId -> {
            UserExtinfoResponse extinfoResponse = userExtinfoApi.getUserExtinfoByUserId(userId);
            userExtinfoApi.deleteUserExtinfoById(extinfoResponse.getId());
        });
        return count;
    }

    /**
     * @param
     * @return
     * @说明: 查看人物基本信息
     * @关联表: AccountInstance、AccountUser、UserInstance、UserExtinfo、IndicatorFunc、CasePersonIndicatorFunc
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
                .userName(userInstance.getName())
                .status(accounInstance.getStatus())
                .extra(userInstance.getNickName())
                .intro(extinfoResponse.getIntro())
                .avatar(accounInstance.getAvatar())
                .build();
        //5、获取用户其他图示管理图片
        List<CasePersonIndicatorFuncRequest> funcList = new ArrayList<>();
        List<CasePersonIndicatorFuncEntity> casePersonIndicatorFuncList = casePersonIndicatorFuncService.lambdaQuery()
                .eq(CasePersonIndicatorFuncEntity::getCasePersonId, accountId)
                .eq(CasePersonIndicatorFuncEntity::getDeleted, false)
                .list();
        if (casePersonIndicatorFuncList != null && casePersonIndicatorFuncList.size() > 0) {
            casePersonIndicatorFuncList.forEach(casePersonIndicatorFuncEntity -> {
                CasePersonIndicatorFuncRequest request = new CasePersonIndicatorFuncRequest();
                BeanUtils.copyProperties(casePersonIndicatorFuncEntity, request);
                funcList.add(request);
            });
        }
        response.setEntityList(funcList);
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
    @DSTransactional
    public Boolean editPerson(PersonInstanceRequest request) {
        //1、修改账户
        AccountInstanceRequest accountInstanceRequest = AccountInstanceRequest.builder()
                .accountId(request.getAccountId().toString())
                .userName(request.getUserName())
                .nickName(request.getExtra())
                .appId(request.getAppId())
                .avatar(request.getAvatar())
                .build();
        String userId = accountInstanceApi.updateAccountInstanceByAccountId(accountInstanceRequest);
        //2、修改用户扩展信息
        if (StringUtils.isNotEmpty(request.getIntro())) {
            UserExtinfoResponse extinfoResponse = userExtinfoApi.getUserExtinfoByUserId(userId);
            UserExtinfoRequest extinfoRequest = new UserExtinfoRequest();
            BeanUtils.copyProperties(extinfoResponse, extinfoRequest,new String[]{"intro"});
            extinfoRequest.setIntro(request.getIntro());
            userExtinfoApi.updateUserExtinfoById(extinfoRequest);
        }
        //3、修改用户功能点
        List<CasePersonIndicatorFuncRequest> funcList = request.getEntityList();
        List<CasePersonIndicatorFuncEntity> entities = new ArrayList<>();
        if (CollUtil.isNotEmpty(funcList)) {
            funcList.forEach(func -> {
                CasePersonIndicatorFuncEntity casePersonIndicatorFuncEntity = casePersonIndicatorFuncService.lambdaQuery()
                        .eq(CasePersonIndicatorFuncEntity::getCasePersonIndicatorFuncId, func.getCasePersonIndicatorFuncId())
                        .eq(CasePersonIndicatorFuncEntity::getDeleted, false)
                        .one();
                CasePersonIndicatorFuncEntity entity = new CasePersonIndicatorFuncEntity();
                BeanUtils.copyProperties(func, entity);
                entity.setId(casePersonIndicatorFuncEntity.getId());
                entities.add(entity);
            });
        }
        return casePersonIndicatorFuncService.updateBatchById(entities);
    }

    /**
     * @param
     * @return
     * @说明: 编辑人物状态
     * @关联表: AccountInstance
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月31日 下午16:22:34
     */
    @DSTransactional
    public String editPersonStatus(PersonInstanceRequest request) {
        //1、修改账户
        AccountInstanceRequest accountInstanceRequest = AccountInstanceRequest.builder()
                .accountId(request.getAccountId().toString())
                .status(request.getStatus())
                .appId(request.getAppId())
                .build();
        String userId = accountInstanceApi.updateAccountInstanceByAccountId(accountInstanceRequest);
        return userId;
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
    @DSTransactional
    public PersonInstanceResponse copyPerson(String accountId,String source) throws ExecutionException, InterruptedException {
        //1、获取用户信息及简介并创建新用户及简介
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
        //2、获取该账户的所有信息
        AccountInstanceResponse accountInstanceResponse = accountInstanceApi.getAccountInstanceByAccountId(accountId);
        //3、复制账户信息
        AccountInstanceRequest accountInstanceRequest = AccountInstanceRequest.builder()
                .appId(accountInstanceResponse.getAppId())
                .avatar(accountInstanceResponse.getAvatar())
                .status(accountInstanceResponse.getStatus())
                .source(source)
                .principalType(accountInstanceResponse.getPrincipalType())
                .identifier(orgBiz.createCode(7))
                .accountName(randomWord(6))
                .build();
        AccountInstanceResponse vo = accountInstanceApi.createAccountInstance(accountInstanceRequest);
        //4、创建账户和用户之间的关联关系
        AccountUserRequest accountUserRequest = AccountUserRequest.builder()
                .accountId(vo.getAccountId())
                .userId(userId)
                .appId(accountInstanceResponse.getAppId())
                .tentantId(accountInstanceResponse.getTenantId()).build();
        this.accountUserApi.createAccountUser(accountUserRequest);
        //5、复制指标
        caseIndicatorInstanceBiz.copyPersonIndicatorInstance(CaseCreateCopyToPersonRequestRs
                .builder()
                .appId(accountInstanceResponse.getAppId())
                .principalId(vo.getAccountId())
                .build());
        //6.复制事件
        tenantCaseEventBiz.copyCaseEvent4Person(accountInstanceResponse.getAppId(),vo.getAccountId(),accountId,
                accountInstanceResponse.getUserName());
        return PersonInstanceResponse.builder().accountId(vo.getAccountId())
                .build();
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
    public Map<String, Object> login(AccountInstanceRequest request, HttpServletRequest request1) {
        return accountInstanceApi.login(request,request1);
    }

    /**
     * @param
     * @return
     * @说明: 登出
     * @关联表: account_instance
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/9/6 13:20
     */
    public void loginOut(AccountInstanceRequest request, HttpServletRequest request1) {
        accountInstanceApi.loginOut(request,request1);
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
    public Boolean resetPwd(String oldPassword,AccountInstanceRequest request) {
        //1、判断与原密码是否相等
        if(StringUtils.isNotEmpty(oldPassword)) {
            AccountInstanceResponse instance = accountInstanceApi.getAccountInstanceByAccountName(request.getAccountName(), request.getAppId());
            if (instance != null && !ReflectUtil.isObjectNull(instance)) {
                if (!new BCryptPasswordEncoder().matches(oldPassword,instance.getPassword())) {
                    throw new AccountException(EnumAccountStatusCode.ACCOUNT_PASSWORD_NOT_MATCH_EXCEPTION);
                }
            }
        }
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
        //3、根据账户ID获取用户ID
        AccountUserResponse accountUser = accountUserApi.getUserByAccountId(accountId);
        //3、获取用户拓展信息
        UserExtinfoResponse extinfo = userExtinfoApi.getUserExtinfoByUserId(accountUser.getUserId());
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
        //3、如果有实验用户，更新用户姓名
        List<ExperimentInstanceEntity> instanceEntityList = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getAccountId,request.getAccountId())
                .eq(ExperimentInstanceEntity::getDeleted,false)
                .list();
        if(instanceEntityList != null && instanceEntityList.size() > 0){
            List<ExperimentInstanceEntity> resultList = new ArrayList<>();
            instanceEntityList.forEach(instance->{
                ExperimentInstanceEntity instanceEntity = ExperimentInstanceEntity.builder()
                        .id(instance.getId())
                        .appointorName(request.getUserName())
                        .build();
                resultList.add(instanceEntity);
            });
            experimentInstanceService.updateBatchById(resultList);
        }
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
    public IPage<AccountInstanceResponse> listTeacherOrStudent(AccountInstanceRequest request,String accountId) {
        Set<String> accountIds = new HashSet<>();
        //1、如果是教师，只能查看该教师下面的班级
        if(StringUtils.isNotEmpty(accountId)){
            List<HepArmEntity> armList = hepArmService.lambdaQuery()
                    .eq(HepArmEntity::getAccountId,accountId)
                    .eq(HepArmEntity::getDeleted,false)
                    .list();
            if(armList != null && armList.size() > 0){
                Set<String> orgIds = new HashSet<>();
                armList.forEach(arm->{
                    orgIds.add(arm.getOrgId());
                });
                //1.1、根据机构ID找到对应的成员
                if(orgIds != null && orgIds.size() > 0){
                    orgIds.forEach(orgId->{
                        List<AccountGroupResponse> groupList = accountGroupApi.getAccountGroupByOrgId(orgId);
                        if(groupList != null && groupList.size() > 0){
                            groupList.forEach(group->{
                                accountIds.add(group.getAccountId());
                            });
                        }
                    });
                }
            }
        }else {
            //2、管理员获取所有accountIds
            List<AccountInstanceResponse> responses = accountInstanceApi.getAccountInstanceList(AccountInstanceRequest.builder().appId(request.getAppId()).build());
            responses.forEach(res -> {
                accountIds.add(res.getAccountId());
            });
        }
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
        //1、更改账户实例
        String userId = accountInstanceApi.updateAccountInstanceByAccountId(request);
        //2、更改负责人信息
        List<AccountGroupInfoResponse> groupInfoList = accountGroupInfoApi.getGroupInfoListByAccountId(request.getAccountId());
        if(groupInfoList != null && groupInfoList.size() > 0){
            groupInfoList.forEach(groupInfo->{
                AccountGroupInfoRequest request1 = AccountGroupInfoRequest
                        .builder()
                        .owner(request.getUserName())
                        .orgId(groupInfo.getOrgId())
                        .build();
                accountGroupInfoApi.updateAccountGroupInfo(request1);
            });
        }
        //3、如果有实验用户，更新用户姓名
        List<ExperimentInstanceEntity> instanceEntityList = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getAccountId,request.getAccountId())
                .eq(ExperimentInstanceEntity::getDeleted,false)
                .list();
        if(instanceEntityList != null && instanceEntityList.size() > 0){
            List<ExperimentInstanceEntity> resultList = new ArrayList<>();
            instanceEntityList.forEach(instance->{
                ExperimentInstanceEntity instanceEntity = ExperimentInstanceEntity.builder()
                        .id(instance.getId())
                        .appointorName(request.getUserName())
                        .build();
                resultList.add(instanceEntity);
            });
            experimentInstanceService.updateBatchById(resultList);
        }
        return userId;
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
        //4、转移机构表更换负责人
        List<HepArmEntity> entityList = hepArmService.lambdaQuery()
                .in(HepArmEntity::getOrgId,request.getOrgIds())
                .eq(HepArmEntity::getDeleted,false)
                .eq(HepArmEntity::getAccountId,ownId)
                .list();
        if(entityList != null && entityList.size() > 0){
            entityList.forEach(entity->{
                entity.setAccountId(request.getAccountId());
            });
            hepArmService.updateBatchById(entityList);
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
                Set<String> studentIds = new HashSet<>();
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
                        //1.4、获取该班级的学生，单独删
                        List<AccountGroupResponse> groupResponseList = accountGroupApi.getAccountGroupByOrgId(org.getOrgId());
                        if(groupResponseList != null && groupResponseList.size() > 0){
                            groupResponseList.forEach(groupResponse->{
                                if(!groupResponse.getAccountId().equals(accountId)){
                                    studentIds.add(groupResponse.getAccountId());
                                }
                            });
                        }

                    });
                    //1.5、删除上述机构下的所有成员及机构相关信息
                    accountOrgApi.batchDeleteAccountOrgsByOrgIds(orgIdsList);
                    //1.6、删除与业务表的映射关系
                    //1.7、删除业务映射关系
                    List<HepArmEntity> hepArmList = hepArmService.lambdaQuery()
                            .eq(HepArmEntity::getAccountId, accountId)
                            .eq(HepArmEntity::getDeleted, false)
                            .list();
                    if(hepArmList != null && hepArmList.size() > 0) {
                        LambdaUpdateWrapper<HepArmEntity> armWrapper = Wrappers.lambdaUpdate(HepArmEntity.class);
                        armWrapper.set(HepArmEntity::getDeleted, true)
                                .eq(HepArmEntity::getAccountId, accountId);
                        boolean flag3 = hepArmService.update(armWrapper);
                        if (!flag3) {
                            throw new AccountException(EnumAccountStatusCode.ACCOUNT_UPDATE_FAIL_EXCEPTION);
                        }
                    }
                }
                //1.6、删除老师账号相关信息
                accountInstanceApi.deleteAccountInstanceByAccountIds(Arrays.asList(accountId).stream().collect(Collectors.toSet()));
                //1.7、删除学生账号相关信息
                accountInstanceApi.deleteAccountInstanceByAccountIds(studentIds);
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
    public PersonInstanceResponse addPerson(AccountInstanceRequest request) throws ExecutionException, InterruptedException {
        //1、创建随机账号
        request.setAccountName(randomWord(6));
        //2、新增用户信息
        UserInstanceRequest user = new UserInstanceRequest();
        BeanUtils.copyProperties(request, user);
        user.setName(request.getUserName());
        //3、保存其他图示
        user.setNickName(request.getExtra());
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
        caseIndicatorInstanceBiz.copyPersonIndicatorInstance(CaseCreateCopyToPersonRequestRs
            .builder()
            .appId(request.getAppId())
            .principalId(vo.getAccountId())
            .build());
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
            word.append((char) ('a' + random.nextInt(26)));
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
        //获取关键指标
        Map<String, List<String>> mapCoreIndicators= caseIndicatorInstanceBiz.getCoreByAccountIdList(accountIds);
        //3、复制
        List<PersonInstanceResponse> personInstanceResponseList = new ArrayList<>();
        List<AccountInstanceResponse> accountInstanceList = accountInstancePage.getRecords();
        accountInstanceList.forEach(accountInstance -> {
            //4、获取健康指数
            String healthPoint = caseIndicatorInstanceBiz.v2GetHealthPoint(accountInstance.getAccountId());
            PersonInstanceResponse personInstance = PersonInstanceResponse.builder()
                    .accountId(accountInstance.getAccountId())
                    .accountName(accountInstance.getAccountName())
                    .userName(accountInstance.getUserName())
                    .status(accountInstance.getStatus())
                    .avatar(accountInstance.getAvatar())
                    .intro(accountInstance.getIntro())
                    .healthPoint(healthPoint)
                    .coreIndicators(mapCoreIndicators.get(accountInstance.getAccountId()))
                    .build();
            personInstanceResponseList.add(personInstance);
        });
        IPage<PersonInstanceResponse> personInstancePage = new Page<>();
        BeanUtils.copyProperties(accountInstancePage, personInstancePage, new String[]{"records"});
        personInstancePage.setRecords(personInstanceResponseList);
        return personInstancePage;
    }

    /**
     * @param
     * @return
     * @说明: 创建 人物功能点
     * @关联表:
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/27 10:24
     */
    @DSTransactional
    public Boolean addOtherBackground(List<CasePersonIndicatorFuncRequest> list) {
        List<CasePersonIndicatorFuncEntity> funcList = new ArrayList<>();
        list.forEach(model -> {
            CasePersonIndicatorFuncEntity entity = new CasePersonIndicatorFuncEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setCasePersonIndicatorFuncId(idGenerator.nextIdStr());
            funcList.add(entity);
        });
        return casePersonIndicatorFuncService.saveBatch(funcList);
    }
}