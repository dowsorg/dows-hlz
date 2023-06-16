package org.dows.hep.biz.tenant.experiment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.AccountRoleApi;
import org.dows.account.response.AccountRoleResponse;
import org.dows.hep.api.base.materials.MaterialsRoleEnum;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentManageBaseBiz {
    private final IdGenerator idGenerator;
    private final AccountRoleApi accountRoleApi;

    public String getIdStr() {
        return idGenerator.nextIdStr();
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
