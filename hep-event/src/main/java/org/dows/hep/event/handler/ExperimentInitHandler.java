package org.dows.hep.event.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.request.AccountGroupRequest;
import org.dows.account.response.AccountGroupResponse;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.base.evaluate.EvaluateEnabledEnum;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.api.tenant.experiment.request.CreateExperimentRequest;
import org.dows.hep.api.tenant.experiment.request.ExperimentGroupSettingRequest;
import org.dows.hep.api.user.organization.request.CaseOrgRequest;
import org.dows.hep.api.user.organization.response.CaseOrgResponse;
import org.dows.hep.biz.base.org.OrgBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentCaseInfoManageBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentManageBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentQuestionnaireManageBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentSchemeManageBiz;
import org.dows.hep.biz.task.ExperimentBeginTimerTask;
import org.dows.hep.biz.task.ExperimentTaskScheduler;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 实验初始化
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentInitHandler extends AbstractEventHandler implements EventHandler<ExperimentGroupSettingRequest> {
    private final ExperimentCaseInfoManageBiz experimentCaseInfoManageBiz;
    private final ExperimentSchemeManageBiz experimentSchemeManageBiz;
    private final ExperimentQuestionnaireManageBiz experimentQuestionnaireManageBiz;
    private final OrgBiz orgBiz;
    private final ExperimentManageBiz experimentManageBiz;
    //todo 记得优化@jx
    // 实验实例
    private final ExperimentInstanceService experimentInstanceService;

    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验计时器
    private final ExperimentTimerService experimentTimerService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final ExperimentTaskScheduler experimentTaskScheduler;

    @Override
    public void exec(ExperimentGroupSettingRequest request) {
        String experimentInstanceId = request.getExperimentInstanceId();
        String caseInstanceId = request.getCaseInstanceId();
        // 设置实验开始定时器
        setExperimentBeginTimerTask(request);
        // 初始化实验 `设置小组` 个数
        createGroupEvent(request);
        // 初始化实验 `复制机构和人物`
        copyExperimentPersonAndOrgEvent(request);
        // 初始化实验 `社区基本信息`
        experimentCaseInfoManageBiz.preHandleCaseInfo(experimentInstanceId, caseInstanceId);
        // 初始化实验 `方案设计` 数据
        experimentSchemeManageBiz.preHandleExperimentScheme(experimentInstanceId, caseInstanceId);
        // 初始化实验 `知识答题` 数据
        experimentQuestionnaireManageBiz.preHandleExperimentQuestionnaire(experimentInstanceId, caseInstanceId);
    }

    /**
     * 实验开始定时器
     *
     * @param experimentGroupSettingRequest
     */
    public void setExperimentBeginTimerTask(ExperimentGroupSettingRequest experimentGroupSettingRequest) {
        ExperimentBeginTimerTask experimentBeginTimerTask = new ExperimentBeginTimerTask(
                experimentInstanceService, experimentParticipatorService, experimentTimerService, applicationEventPublisher,
                experimentGroupSettingRequest.getExperimentInstanceId());

        /**
         * 设定定时任务
         * todo 设定一个TimeTask,通过timer到时间执行一次，考虑重启情况，写数据库，针对出现的情况，更具时间重新schedule,先用事件处理，后期优化
         */
        experimentTaskScheduler.schedule(experimentBeginTimerTask, experimentGroupSettingRequest.getStartTime());
    }

    /**
     * todo 如果实验重启，这里在其他地方就获取不到，需要调整
     *
     * @param experimentGroupSettingRequest
     */
    public void createGroupEvent(ExperimentGroupSettingRequest experimentGroupSettingRequest) {
        ExperimentContext experimentContext = new ExperimentContext();
        experimentContext.setExperimentId(experimentGroupSettingRequest.getExperimentInstanceId());
        experimentContext.setExperimentName(experimentGroupSettingRequest.getExperimentName());
        experimentContext.setState(ExperimentStateEnum.UNBEGIN);
        //设置小组个数
        experimentContext.setGroupCount(experimentGroupSettingRequest.getGroupSettings().size());
        ExperimentContext.set(experimentContext);
    }

    /**
     * copy 实验人物计事件
     */
    public void copyExperimentPersonAndOrgEvent(ExperimentGroupSettingRequest experimentGroupSettingRequest) {
        // 复制人物与机构到实验中
        ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentGroupSettingRequest.getExperimentInstanceId())
                .eq(ExperimentInstanceEntity::getDeleted, false)
                .one();
        IPage<CaseOrgResponse> caseOrgResponseIPage = orgBiz.listOrgnization(CaseOrgRequest.builder().pageNo(1).pageSize(10)
                .caseInstanceId(experimentInstanceEntity.getCaseInstanceId())
                .status(EvaluateEnabledEnum.ENABLED.getCode())
                .build());
        List<CaseOrgResponse> responseList = caseOrgResponseIPage.getRecords();
        List<CreateExperimentRequest> requestList = new ArrayList<>();
        if (responseList != null && responseList.size() > 0) {
            responseList.forEach(response -> {
                //1、通过案例机构ID找到机构ID下面的人物
                IPage<AccountGroupResponse> groupResponseIPage = orgBiz.listPerson(AccountGroupRequest.builder()
                        .status(EvaluateEnabledEnum.ENABLED.getCode())
                        .appId(experimentGroupSettingRequest.getAppId())
                        .pageNo(1)
                        .pageSize(999)
                        .build(), response.getCaseOrgId());
                List<AccountGroupResponse> accountGroupResponses = groupResponseIPage.getRecords();
                List<AccountInstanceResponse> instanceResponses = new ArrayList<>();
                if (accountGroupResponses != null && accountGroupResponses.size() > 0) {
                    accountGroupResponses.forEach(accountGroup -> {
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
