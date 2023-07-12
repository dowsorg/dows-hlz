package org.dows.hep.biz.tenant.experiment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.AccountRoleApi;
import org.dows.account.response.AccountRoleResponse;
import org.dows.hep.api.base.materials.MaterialsRoleEnum;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.biz.user.experiment.ExperimentGroupBiz;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentManageBaseBiz {
    private final IdGenerator idGenerator;
    private final AccountRoleApi accountRoleApi;
    private final ExperimentGroupBiz experimentGroupBiz;

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

    public List<String> listExperimentGroupIds(String experimentInstanceId) {
        List<ExperimentGroupResponse> experimentGroupResponses = listExptGroup(experimentInstanceId);
        return experimentGroupResponses.stream().map(ExperimentGroupResponse::getExperimentGroupId).toList();
    }

    public List<ExperimentGroupResponse> listExptGroup(String exptInstanceId) {
        return experimentGroupBiz.listGroup(exptInstanceId);
    }
}
