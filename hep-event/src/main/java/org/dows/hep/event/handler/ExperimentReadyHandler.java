package org.dows.hep.event.handler;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.api.user.experiment.request.ExperimentParticipatorRequest;
import org.dows.hep.api.user.experiment.request.ExptQuestionnaireAllotRequest;
import org.dows.hep.api.user.experiment.response.StartCutdownResponse;
import org.dows.hep.biz.task.ExperimentTaskScheduler;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * 实验就绪事件处理器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentReadyHandler extends AbstractEventHandler implements EventHandler<List<ExperimentParticipatorRequest>> {

    private final ExperimentParticipatorService experimentParticipatorService;
    private final ExperimentQuestionnaireBiz experimentQuestionnaireBiz;
    private final ExperimentTimerBiz experimentTimerBiz;
    private final ExperimentTaskScheduler experimentTaskScheduler;
    private final StartHandler startHandler;


    /**
     * todo 该处应该为发布一个事件，名称为 开始实验事件，在该事件中处理通知客户端和（分配试卷？？？？应该提前完成？）
     * applicationEventPublisher.publishEvent(new GroupMemberAllotEvent(participatorList));
     *  todo 分配试卷事件，可以合并后面需要优化//
     *  applicationEventPublisher.publishEvent(new ExptQuestionnaireAllotEvent(
     *                 ExptQuestionnaireAllotEventSource.builder()
     *                         .experimentInstanceId(participatorList.get(0).getExperimentInstanceId())
     *                         .experimentGroupId(participatorList.get(0).getExperimentGroupId())
     *                         .build()));
     */
    @Override
    public void exec(List<ExperimentParticipatorRequest> participatorRequestList) {
        String experimentInstanceId = participatorRequestList.get(0).getExperimentInstanceId();
        String experimentGroupId = participatorRequestList.get(0).getExperimentGroupId();
        String appId = participatorRequestList.get(0).getAppId();
        // 触发开始
        triggerStart(experimentInstanceId);
        // 分配题目
        allotQuestion(experimentInstanceId, experimentGroupId);
        // 通知客户端
        wsNotice(participatorRequestList, experimentInstanceId);
        // 设置rankingTimerTask
        //setRankingTimerTask(experimentInstanceId);
    }

    /**
     * 触发实验开始
     *
     * @param experimentInstanceId
     */
    private void triggerStart(String experimentInstanceId) {
        startHandler.exec(ExperimentRestartRequest.builder()
                .experimentInstanceId(experimentInstanceId)
                .paused(false)
                .currentTime(new Date())
                .build());
    }

    /**
     * 分发试卷|题目
     */
    private void allotQuestion(String experimentInstanceId, String experimentGroupId) {

        List<ExperimentParticipatorEntity> list = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentParticipatorEntity::getExperimentGroupId, experimentGroupId)
                .list();

        ArrayList<ExptQuestionnaireAllotRequest.ParticipatorWithQuestionnaire> allotList = new ArrayList<>();
        ExptQuestionnaireAllotRequest request = ExptQuestionnaireAllotRequest.builder()
                .experimentInstanceId(experimentInstanceId)
                .experimentGroupId(experimentGroupId)
                .build();
        list.forEach(ep -> {
            String experimentOrgIds = ep.getExperimentOrgIds();
            String accountId = ep.getAccountId();
            String[] orgIds = experimentOrgIds.split(",");

            ExptQuestionnaireAllotRequest.ParticipatorWithQuestionnaire participatorWithQuestionnaire = new ExptQuestionnaireAllotRequest.ParticipatorWithQuestionnaire();
            participatorWithQuestionnaire.setAccountId(accountId);
            participatorWithQuestionnaire.setExperimentOrgIds(List.of(orgIds));
            allotList.add(participatorWithQuestionnaire);
        });
        request.setAllotList(allotList);

        experimentQuestionnaireBiz.allotQuestionnaireMembers(request);
    }

    /**
     * ws通知客户端
     */
    private void wsNotice(List<ExperimentParticipatorRequest> participatorRequestList, String experimentInstanceId) {
        // 查询到对应的accountId
        Set<String> accountIds = new HashSet<>();
        participatorRequestList.forEach(participator -> {
            List<ExperimentParticipatorEntity> participatorList = experimentParticipatorService.lambdaQuery()
                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                    .eq(ExperimentParticipatorEntity::getDeleted, false)
                    .list();
            participatorList.forEach(participator1 -> {
                accountIds.add(participator1.getAccountId());
            });
        });


        // 通知实验所有小组
        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();

        // 过滤数据，只给学生发websocket
        Set<Channel> channels = userInfos.keySet();
        for (Channel channel : channels) {
            if (accountIds.contains(userInfos.get(channel).getAccountName())) {
                StartCutdownResponse startCutdownResponse = new StartCutdownResponse();
                startCutdownResponse.setType(EnumWebSocketType.START_EXPERIMENT_COUNTDOWN);
                //startCutdownResponse.setModelDescr(periods1.getModelDescr());
                startCutdownResponse.setExperimentInstanceId(experimentInstanceId);
                //startCutdownResponse.setPeriodInterval(periods1.getPeriodInterval());
                Response<StartCutdownResponse> ok = Response.ok(startCutdownResponse);
                HepClientManager.sendInfo(channel, MessageCode.MESS_CODE, ok);
            }
        }
    }


}
