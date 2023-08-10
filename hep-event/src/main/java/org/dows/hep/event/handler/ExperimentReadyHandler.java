package org.dows.hep.event.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.enums.EnumExperimentNotice;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.enums.EnumNoticeType;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.api.notify.NoticeContent;
import org.dows.hep.api.notify.message.ExperimentPeriodMessage;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.api.user.experiment.request.ExperimentParticipatorRequest;
import org.dows.hep.api.user.experiment.response.StartCutdownResponse;
import org.dows.hep.biz.noticer.PeriodEndNoticer;
import org.dows.hep.biz.noticer.PeriodStartNoticer;
import org.dows.hep.biz.request.ExperimentTaskParamsRequest;
import org.dows.hep.biz.task.ExperimentNoticeTask;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireBiz;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTaskScheduleService;
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
    private final ExperimentStartHandler experimentStartHandler;
    private final PeriodStartNoticer periodStartNoticer;
    private final PeriodEndNoticer periodEndNoticer;
    private final ExperimentTaskScheduleService experimentTaskScheduleService;


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
        // 通知客户端
        wsNotice(participatorRequestList, experimentInstanceId);
        // 设置rankingTimerTask
        //setRankingTimerTask(experimentInstanceId);
        // 设置通知任务
        setNoticeTask(experimentInstanceId, experimentGroupId);

    }

    /**
     * 设置通知任务
     *
     * @param experimentInstanceId
     * @param experimentGroupId
     */
    private void setNoticeTask(String experimentInstanceId, String experimentGroupId) {

        Map<Integer, ExperimentTimerEntity> experimentPeriodsStartAnsEndTime =
                experimentTimerBiz.getExperimentPeriodsStartAnsEndTime(experimentInstanceId);
        // 总期数
        int periods = experimentPeriodsStartAnsEndTime.size();
        experimentPeriodsStartAnsEndTime.forEach((k, v) -> {
            NoticeContent noticeContent = NoticeContent.builder()
                    .payload(ExperimentPeriodMessage.builder()
                            .currentPeriod(v.getPeriod())
                            .endTime(DateUtil.date(v.getEndTime()))
                            .startTime(DateUtil.date(v.getStartTime()))
                            .experimentGroupId(experimentGroupId)
                            .periods(periods)
                            .experimentInstanceId(experimentInstanceId)
                            .build())
                    .noticeType(EnumNoticeType.BoardCastSysEvent)
                    .build();

            extracted(experimentInstanceId, v, noticeContent);

            // 期数开始通知任务
            ExperimentNoticeTask experimentPeriodStartNoticeTask = new ExperimentNoticeTask(
                    experimentInstanceId,
                    experimentGroupId,
                    v.getPeriod(),
                    periodStartNoticer,
                    noticeContent,
                    experimentTaskScheduleService
            );
            taskScheduler.schedule(experimentPeriodStartNoticeTask, v.getStartTime());

            extracted1(experimentInstanceId, v, noticeContent);

            // 期数结束通知任务
            ExperimentNoticeTask experimentPeriodEndNoticeTask = new ExperimentNoticeTask(
                    experimentInstanceId,
                    experimentGroupId,
                    v.getPeriod(),
                    periodEndNoticer,
                    noticeContent,
                    experimentTaskScheduleService
            );
            taskScheduler.schedule(experimentPeriodEndNoticeTask, v.getEndTime());
        });
    }

    private void extracted1(String experimentInstanceId, ExperimentTimerEntity v, NoticeContent noticeContent) {
        //保存任务进计时器表，防止重启后服务挂了，一个任务每个实验每一期只能有一条数据
        ExperimentTaskScheduleEntity endEntity = new ExperimentTaskScheduleEntity();
        ExperimentTaskScheduleEntity endTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
                .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentPeriodEndNoticeTask.getDesc())
                .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentTaskScheduleEntity::getPeriods, v.getPeriod())
                .one();
        String taskParam = JSON.toJSONString(ExperimentTaskParamsRequest.builder()
                .experimentInstanceId(experimentInstanceId)
                .period(v.getPeriod())
                .noticeParams(JSON.toJSONString(noticeContent))
                .noticeType(EnumExperimentNotice.endNotice.getCode())
                .build());
        if (endTaskScheduleEntity != null && !ReflectUtil.isObjectNull(endTaskScheduleEntity)) {
            BeanUtil.copyProperties(endTaskScheduleEntity, endEntity);
            endEntity.setExecuteTime(v.getStartTime());
            endEntity.setExecuted(false);
        } else {
            endEntity = new ExperimentTaskScheduleEntity()
                    .builder()
                    .experimentTaskTimerId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstanceId)
                    .taskBeanCode(EnumExperimentTask.experimentPeriodEndNoticeTask.getDesc())
                    .taskParams(taskParam)
                    .periods(v.getPeriod())
                    .appId(v.getAppId())
                    .executeTime(v.getEndTime())
                    .executed(false)
                    .build();
        }
        experimentTaskScheduleService.saveOrUpdate(endEntity);
    }

    private void extracted(String experimentInstanceId, ExperimentTimerEntity v, NoticeContent noticeContent) {
        //保存任务进计时器表，防止重启后服务挂了，一个任务每个实验每一期只能有一条数据
        ExperimentTaskScheduleEntity startEntity = new ExperimentTaskScheduleEntity();
        ExperimentTaskScheduleEntity startTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
                .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentPeriodStartNoticeTask.getDesc())
                .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentTaskScheduleEntity::getPeriods, v.getPeriod())
                .one();
        String taskParam = JSON.toJSONString(ExperimentTaskParamsRequest.builder()
                .experimentInstanceId(experimentInstanceId)
                .period(v.getPeriod())
                .noticeParams(JSON.toJSONString(noticeContent))
                .noticeType(EnumExperimentNotice.startNotice.getCode())
                .build());
        if (startTaskScheduleEntity != null && !ReflectUtil.isObjectNull(startTaskScheduleEntity)) {
            BeanUtil.copyProperties(startTaskScheduleEntity, startEntity);
            startEntity.setExecuteTime(v.getStartTime());
            startEntity.setExecuted(false);
        } else {
            startEntity = new ExperimentTaskScheduleEntity()
                    .builder()
                    .experimentTaskTimerId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstanceId)
                    .taskBeanCode(EnumExperimentTask.experimentPeriodStartNoticeTask.getDesc())
                    .taskParams(taskParam)
                    .periods(v.getPeriod())
                    .appId(v.getAppId())
                    .executeTime(v.getStartTime())
                    .executed(false)
                    .build();
        }
        experimentTaskScheduleService.saveOrUpdate(startEntity);
    }

    /**
     * 触发实验开始
     *
     * @param experimentInstanceId
     */
    private void triggerStart(String experimentInstanceId) {
        experimentStartHandler.exec(ExperimentRestartRequest.builder()
                .experimentInstanceId(experimentInstanceId)
                .paused(false)
                .currentTime(new Date())
                .build());
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
