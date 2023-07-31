package org.dows.hep.biz.user.experiment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.AccountRoleApi;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountRoleResponse;
import org.dows.account.util.JwtUtil;
import org.dows.hep.api.base.materials.MaterialsRoleEnum;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.biz.base.person.PersonManageBiz;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * @author fhb
 * @description
 * @date 2023/5/30 15:50
 */
@Slf4j
@AllArgsConstructor
@Service
public class ExperimentBaseBiz {
    private final AccountRoleApi accountRoleApi;
    private final PersonManageBiz personManageBiz;
    public String getAppId() {
        return "3";
    }
    public String getAccountId(HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        return map.get("accountId").toString();
    }

    public String getAccountName(HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        return map.get("accountName").toString();
    }

    // todo uim 提供批量操作
    public String getUserName(String accountId) {
        String userName = "ERROR";
        try {
            AccountInstanceResponse personalInformation = personManageBiz.getPersonalInformation(accountId, getAppId());
            userName = Optional.ofNullable(personalInformation)
                    .map(AccountInstanceResponse::getUserName)
                    .orElse("");
        } catch (Exception e) {
            log.error("实验中获取账号 {} 用户名异常", accountId);
        }
        return userName;
    }

    public boolean isAdministrator(String accountId) {
        AccountRoleResponse role = accountRoleApi.getAccountRoleByPrincipalId(accountId);
        String roleCode = Optional.ofNullable(role)
                .map(AccountRoleResponse::getRoleCode)
                .orElse("");
        if (MaterialsRoleEnum.ADMIN.name().equals(roleCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
