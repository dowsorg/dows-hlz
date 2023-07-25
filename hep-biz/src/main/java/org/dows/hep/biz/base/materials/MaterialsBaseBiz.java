package org.dows.hep.biz.base.materials;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.AccountRoleApi;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountRoleResponse;
import org.dows.account.util.JwtUtil;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.materials.MaterialsESCEnum;
import org.dows.hep.api.base.materials.MaterialsRoleEnum;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.biz.base.person.PersonManageBiz;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MaterialsBaseBiz {
    private static final String LAST_VERSION = "SNAPSHOT";

    private final AccountRoleApi accountRoleApi;
    private final PersonManageBiz personManageBiz;
    private final IdGenerator idGenerator;

    public String getAppId() {
        return "3";
    }

    public String getIdStr() {
        return idGenerator.nextIdStr();
    }

    public String getLastVer() {
        return LAST_VERSION;
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

    public boolean isTeacher(String accountId) {
        AccountRoleResponse role = accountRoleApi.getAccountRoleByPrincipalId(accountId);
        String roleCode = Optional.ofNullable(role)
                .map(AccountRoleResponse::getRoleCode)
                .orElse("");
        if (MaterialsRoleEnum.TEACHER.name().equals(roleCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public boolean isStudent(String accountId) {
        AccountRoleResponse role = accountRoleApi.getAccountRoleByPrincipalId(accountId);
        String roleCode = Optional.ofNullable(role)
                .map(AccountRoleResponse::getRoleCode)
                .orElse("");
        if (MaterialsRoleEnum.STUDENT.name().equals(roleCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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
            log.error("资料中心获取创建人基本信息异常");
        }
        return userName;
    }

    public String convertDate2String(Date date) {
        if (Objects.isNull(date)) {
            throw new BizException(MaterialsESCEnum.PARAMS_NON_NULL);
        }

        DateTime dateTime = DateUtil.date(date);
        // 年月日
        String ymd = dateTime.toDateStr();
        // 星期
        String week = dateTime.dayOfWeekEnum().toChinese();
        // 小时：分
        String time = dateTime.toTimeStr();
        return ymd + week + " " + time;
    }
}
