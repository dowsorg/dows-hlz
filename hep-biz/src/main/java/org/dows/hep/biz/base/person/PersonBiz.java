package org.dows.hep.biz.base.person;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import org.dows.account.api.AccountGroupApi;
import org.dows.account.api.AccountInstanceApi;
import org.dows.account.api.AccountOrgApi;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.user.api.api.UserExtinfoApi;
import org.dows.user.api.request.UserExtinfoRequest;
import org.dows.user.api.response.UserExtinfoResponse;
import org.springframework.stereotype.Service;
import java.util.Map;

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
        AccountInstanceResponse instance = accountInstanceApi.getPersonalInformationByAccountId(accountId,appId);
        //2、获取账户所属机构
        String orgId = accountGroupApi.getAccountGroupByAccountId(instance.getAccountId()).getOrgId();
        if(StringUtils.isNotEmpty(orgId)) {
            AccountOrgResponse org = accountOrgApi.getAccountOrgByOrgId(orgId, appId);
            instance.setOrgName(org.getOrgName());
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
     * @关联表: ??
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/20 19:37
     */
    public AccountInstanceResponse createTeacherOrStudent(AccountInstanceRequest request) {
        return accountInstanceApi.createAccountInstance(request);
    }
}
