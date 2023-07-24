package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentMonitorFollowupCheckRequestRs;
import org.dows.hep.api.enums.EnumNoticeType;
import org.dows.hep.api.notify.NoticeContent;
import org.dows.hep.api.notify.message.ExperimentFollowupMessage;
import org.dows.hep.api.notify.message.ExperimentMessage;
import org.dows.hep.biz.noticer.FollowupNoticer;
import org.dows.hep.biz.task.ExperimentNoticeTask;
import org.dows.hep.service.ExperimentIndicatorViewMonitorFollowupPlanRsService;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

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

        Integer intervalDay = experimentMonitorFollowupCheckRequestRs.getIntervalDay();
        /*NoticeContent<ExperimentFollowupMessage> noticeContent =*/
        NoticeContent noticeContent = NoticeContent.builder()
                .noticeType(EnumNoticeType.PushCurrentAccountNotice)
                .messageCode(MessageCode.MESS_CODE)
                //.accountId()
                .payload(ExperimentFollowupMessage.builder()

                        .build())
                .build();

//        ExperimentNoticeTask experimentNoticeTask = new ExperimentNoticeTask(followupNoticer,noticeContent,experimentTaskScheduleService,
//                experimentMonitorFollowupCheckRequestRs.getExperimentId(),experimentMonitorFollowupCheckRequestRs.get
//                );
//        taskScheduler.schedule(experimentNoticeTask,);

    }
}
