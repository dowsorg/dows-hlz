package org.dows.hep.event.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.request.AccountGroupRequest;
import org.dows.account.response.AccountGroupResponse;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.hep.api.tenant.experiment.request.CreateExperimentRequest;
import org.dows.hep.api.tenant.experiment.request.ExperimentGroupSettingRequest;
import org.dows.hep.api.user.organization.request.CaseOrgRequest;
import org.dows.hep.api.user.organization.response.CaseOrgResponse;
import org.dows.hep.biz.base.org.OrgBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentManageBiz;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jx
 * @date 2023/6/21 11:30
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CopyExperimentPersonAndOrgHandler extends AbstractEventHandler implements EventHandler<ExperimentGroupSettingRequest> {
    private final ExperimentManageBiz experimentManageBiz;

    private final ExperimentInstanceService experimentInstanceService;

    private final OrgBiz orgBiz;

    @Override
    public void exec(ExperimentGroupSettingRequest experimentGroupSettingRequest) {
        // 复制人物与机构到实验中
        ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId,experimentGroupSettingRequest.getExperimentInstanceId())
                .eq(ExperimentInstanceEntity::getDeleted,false)
                .one();
        IPage<CaseOrgResponse> caseOrgResponseIPage = orgBiz.listOrgnization(CaseOrgRequest.builder().pageNo(1).pageSize(10)
                .caseInstanceId(experimentInstanceEntity.getCaseInstanceId())
                .status(1)
                .build());
        List<CaseOrgResponse> responseList = caseOrgResponseIPage.getRecords();
        List<CreateExperimentRequest> requestList = new ArrayList<>();
        if(responseList != null && responseList.size() > 0){
            responseList.forEach(response->{
                //1、通过案例机构ID找到机构ID下面的人物
                IPage<AccountGroupResponse> groupResponseIPage =orgBiz.listPerson(AccountGroupRequest.builder()
                        .status(1)
                        .appId(experimentGroupSettingRequest.getAppId())
                        .pageNo(1)
                        .pageSize(10)
                        .build(),response.getCaseOrgId());
                List<AccountGroupResponse> accountGroupResponses = groupResponseIPage.getRecords();
                List<AccountInstanceResponse> instanceResponses = new ArrayList<>();
                if(accountGroupResponses != null && accountGroupResponses.size() > 0){
                    accountGroupResponses.forEach(accountGroup->{
                        AccountInstanceResponse instanceResponse = AccountInstanceResponse.builder()
                                .accountId(accountGroup.getAccountId())
                                .build();
                        instanceResponses.add(instanceResponse);
                    });
                }
                CreateExperimentRequest request = CreateExperimentRequest.builder()
                        .experimentInstanceId(experimentGroupSettingRequest.getExperimentInstanceId())
                        .caseOrgId(response.getCaseOrgId())
                        .appId(experimentGroupSettingRequest.getAppId())
                        .teachers(instanceResponses)
                        .build();
                requestList.add(request);
            });
        }
        experimentManageBiz.copyExperimentPersonAndOrg(requestList);
    }
}
