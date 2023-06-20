package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.event.source.ExptInitEventSource;
import org.dows.hep.biz.tenant.experiment.ExperimentCaseInfoManageBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentQuestionnaireManageBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentSchemeManageBiz;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExptInitHandler extends AbstractEventHandler implements EventHandler<ExptInitEventSource> {
    private final ExperimentCaseInfoManageBiz experimentCaseInfoManageBiz;
    private final ExperimentSchemeManageBiz experimentSchemeManageBiz;
    private final ExperimentQuestionnaireManageBiz experimentQuestionnaireManageBiz;

    @Override
    public void exec(ExptInitEventSource request) {
        String experimentInstanceId = request.getExperimentInstanceId();
        String caseInstanceId = request.getCaseInstanceId();

        // 初始化实验 `社区基本信息`
        experimentCaseInfoManageBiz.preHandleCaseInfo(experimentInstanceId, caseInstanceId);
        // 初始化实验 `方案设计` 数据
        experimentSchemeManageBiz.preHandleExperimentScheme(experimentInstanceId, caseInstanceId);
        // 初始化实验 `知识答题` 数据
        experimentQuestionnaireManageBiz.preHandleExperimentQuestionnaire(experimentInstanceId, caseInstanceId);
    }
}
