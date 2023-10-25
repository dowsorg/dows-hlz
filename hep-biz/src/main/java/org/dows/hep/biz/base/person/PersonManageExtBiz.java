package org.dows.hep.biz.base.person;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dows.account.api.AccountInstanceApi;
import org.dows.account.api.AccountUserApi;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.request.AccountUserRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountUserResponse;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.person.response.PersonInstanceResponse;
import org.dows.hep.biz.base.indicator.CaseIndicatorInstanceExtBiz;
import org.dows.hep.biz.base.org.OrgBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseEventExtBiz;
import org.dows.user.api.api.UserExtinfoApi;
import org.dows.user.api.api.UserInstanceApi;
import org.dows.user.api.request.UserExtinfoRequest;
import org.dows.user.api.request.UserInstanceRequest;
import org.dows.user.api.response.UserExtinfoResponse;
import org.dows.user.api.response.UserInstanceResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.dows.hep.biz.base.org.OrgBiz.createCode;
import static org.dows.hep.biz.base.person.PersonManageBiz.randomWord;
import static org.dows.hep.biz.constant.CaseBizConstants.*;

/**
 * TODO:
 * 人物管理
 * 机构人员
 * 管理类
 *
 * @description: lifel 2023/10/9
 */
@Service
@RequiredArgsConstructor
public class PersonManageExtBiz {
    private final AccountUserApi accountUserApi;
    private final AccountInstanceApi accountInstanceApi;
    private final UserInstanceApi userInstanceApi;
    private final UserExtinfoApi userExtinfoApi;
    private final CaseIndicatorInstanceExtBiz caseIndicatorInstanceExtBiz;
    private final TenantCaseEventExtBiz tenantCaseEventExtBiz;
    private final OrgBiz orgBiz;

    /**
     * 案例机构人物复制
     * 返回机构人物新id
     */
    @DSTransactional
    public String duplicateCaseOrgPerson(String caseOrgId, String caseInstanceId, String accountId, boolean suffix ) {
        PersonInstanceResponse personInstanceResponse = duplicatePerson(accountId, ORG_PERSON,suffix);
        if (personInstanceResponse == null) {
            throw new BizException("复制人物异常");
        }
        String newAccountId = personInstanceResponse.getAccountId();
        orgBiz.addPersonToCaseOrg(newAccountId, caseInstanceId, caseOrgId, APPId);
        return newAccountId;
    }

    /**
     * 人物管理复制
     */
    @DSTransactional
    public PersonInstanceResponse duplicatePerson(String accountId, String source,boolean suffix) {
        //为空则人物是 人物管理复制
        if (StringUtils.isBlank(source)) {
            source = PERSON_MANAGE;
        }
        //1.账号用户关系
        AccountUserResponse accountUser = accountUserApi.getUserByAccountId(accountId);
        String oldUserId = accountUser.getUserId();
        //2.账号用户基本信息
        UserInstanceResponse userInstanceResponse = userInstanceApi.getUserInstanceByUserId(oldUserId);
        UserInstanceRequest userInstanceRequest = new UserInstanceRequest();
        BeanUtils.copyProperties(userInstanceResponse, userInstanceRequest, new String[]{"id", "accountId"});
        //人名带后缀
        if (suffix){
            userInstanceRequest.setName(userInstanceRequest.getName() + NAME_SUFFIX);
        }
        userInstanceRequest.setDt(new Date());
        String newUserid = userInstanceApi.insertUserInstance(userInstanceRequest);
        UserExtinfoResponse userExtinfoResponse = userExtinfoApi.getUserExtinfoByUserId(oldUserId);
        UserExtinfoRequest userExtInfo = UserExtinfoRequest.builder()
                .userId(newUserid)
                .intro(userExtinfoResponse.getIntro())
                .build();
        userExtinfoApi.insertUserExtinfo(userExtInfo);
        //3、获取该账户的所有信息
        AccountInstanceResponse accountInstanceResponse = accountInstanceApi.getAccountInstanceByAccountId(accountId);
        //4、复制账户信息并创建，身份标识在uim一起新建
        AccountInstanceRequest accountInstanceRequest = AccountInstanceRequest.builder()
                .appId(accountInstanceResponse.getAppId())
                .avatar(userInstanceResponse.getAvatar())
                .status(accountInstanceResponse.getStatus())
                .source(source)
                .principalType(accountInstanceResponse.getPrincipalType())
                .identifier(createCode(7))
                .accountName(randomWord(6))
                .build();
        AccountInstanceResponse newAccountInstance = accountInstanceApi.createAccountInstance(accountInstanceRequest);
        String newAccountId = newAccountInstance.getAccountId();
        //5、创建账户和用户之间的关联关系
        AccountUserRequest accountUserRequest = AccountUserRequest.builder()
                .accountId(newAccountId)
                .userId(newUserid)
                .appId(accountInstanceResponse.getAppId())
                .tentantId(accountInstanceResponse.getTenantId()).build();
        this.accountUserApi.createAccountUser(accountUserRequest);

        try {
            //6.先复制突发事件，返回突发事件关联指标公式id
            Map<String, String> kOldReasonIdVNewReasonIdMap = tenantCaseEventExtBiz.duplicateCaseEventForPerson(APPId, accountId, newAccountId, userInstanceRequest.getName());
            //7、再复制指标并关联事件公式
            caseIndicatorInstanceExtBiz.duplicatePersonIndicator(APPId, accountId, newAccountId, kOldReasonIdVNewReasonIdMap);

        } catch (ExecutionException | InterruptedException e) {
            throw new BizException("复制人物指标或者突发事件异常");
        }
        return PersonInstanceResponse.builder().accountId(newAccountId)
                .build();
    }
}
