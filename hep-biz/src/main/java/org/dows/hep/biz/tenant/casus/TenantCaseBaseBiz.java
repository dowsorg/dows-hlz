package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.AccountRoleApi;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountRoleResponse;
import org.dows.account.util.JwtUtil;
import org.dows.hep.api.base.materials.MaterialsRoleEnum;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.biz.base.person.PersonManageBiz;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantCaseBaseBiz {
    private static final String LAST_VERSION = "SNAPSHOT";
    private final IdGenerator idGenerator;
    private final AccountRoleApi accountRoleApi;
    private final PersonManageBiz personManageBiz;

    public String getAppId() {
        return "3";
    }

    public String getIdStr() {
        return idGenerator.nextIdStr();
    }

    public String getLastVer() {
        return LAST_VERSION;
    }

    public String getCaseCategoryPid() {
        return "0";
    }

    public String getVer(Date date) {
        return String.valueOf((date == null ? new Date() : date).getTime());
    }

    public <S, T> Page<T> convertPage(Page<S> source, Class<T> target) {
        Page<T> result = BeanUtil.copyProperties(source, Page.class);

        List<S> records = source.getRecords();
        if (records == null || records.isEmpty()) {
            return new Page<>();
        }

        List<T> ts = new ArrayList<>();
        records.forEach(item -> {
            T t = BeanUtil.copyProperties(item, target);
            ts.add(t);
        });

        result.setRecords(ts);
        return result;
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

    public String getUserName(String accountId) {
        String userName = "ERROR";
        try {
            AccountInstanceResponse personalInformation = personManageBiz.getPersonalInformation(accountId, getAppId());
            userName = Optional.ofNullable(personalInformation)
                    .map(AccountInstanceResponse::getUserName)
                    .orElse("");
        } catch (Exception e) {
            log.error("案例列表获取创建人基本信息异常");
        }
        return userName;
    }

}
