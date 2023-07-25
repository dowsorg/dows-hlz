package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentMonitorFollowupCheckRequestRs;
import org.dows.hep.api.enums.EnumNoticeType;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.notify.NoticeContent;
import org.dows.hep.api.notify.message.ExperimentFollowupMessage;
import org.dows.hep.biz.noticer.FollowupNoticer;
import org.dows.hep.biz.task.ExperimentNoticeTask;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentIndicatorViewMonitorFollowupPlanRsService;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * 实验检测对方事件处理
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentFollowupHandler extends AbstractEventHandler implements EventHandler<ExperimentMonitorFollowupCheckRequestRs> {

    private final ExperimentIndicatorViewMonitorFollowupPlanRsService experimentIndicatorViewMonitorFollowupPlanRsService;

    private final FollowupNoticer followupNoticer;


    @Override
    public void exec(ExperimentMonitorFollowupCheckRequestRs experimentMonitorFollowupCheckRequestRs) throws ExecutionException, InterruptedException {

        NoticeContent noticeContent = NoticeContent.builder()
                .noticeType(EnumNoticeType.PushCurrentAccountNotice)
                .messageCode(MessageCode.MESS_CODE)
                .accountId(Arrays.asList(experimentMonitorFollowupCheckRequestRs.getOperatorId()))
                .payload(ExperimentFollowupMessage.builder()
                        .flag(true)
                        .build())
                .build();

        ExperimentNoticeTask experimentNoticeTask = new ExperimentNoticeTask(
                experimentMonitorFollowupCheckRequestRs.getExperimentId(),
                experimentMonitorFollowupCheckRequestRs.getExperimentGroupId(),
                experimentMonitorFollowupCheckRequestRs.getPeriods(),
                followupNoticer, noticeContent, experimentTaskScheduleService
        );
        // 实验中的随访时间间隔比例
        Integer intervalDay = experimentMonitorFollowupCheckRequestRs.getIntervalDay();
        /**
         * 每期都可能有监测随访
         */
        ExperimentTimerEntity currentExperimentTimer = experimentTimerBiz
                .getCurrentExperimentTimer(experimentMonitorFollowupCheckRequestRs.getExperimentId(),
                        System.currentTimeMillis());
        // 当前期剩余时间
        long residueTime = currentExperimentTimer.getEndTime().getTime() - System.currentTimeMillis();
        // 一天的毫秒数
        long dayMs = intervalDay * 24 * 60 * 1000;
        if (residueTime >= residueTime) {
            throw new ExperimentException("当前期数内剩余时间不够模拟该时长");
        }

        //residueTime/dayMs
        // 模拟期数内剩余天数时长
        long mockMs = dayMs / residueTime;
        // 确定
        if (mockMs >= residueTime) {
            throw new ExperimentException("当前期数内剩余时间不够模拟该时长");
        }

        //taskScheduler.schedule(experimentNoticeTask, );


    }
}
