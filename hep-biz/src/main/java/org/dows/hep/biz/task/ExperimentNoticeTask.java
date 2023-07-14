package org.dows.hep.biz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.notify.NoticeParams;
import org.dows.hep.api.notify.Notifiable;

/**
 * 实验通知任务
 */
@Slf4j
@RequiredArgsConstructor
public class ExperimentNoticeTask implements Runnable {

    private final Notifiable notifiable;
    private final NoticeParams noticeParams;

    @Override
    public void run() {
        notifiable.notice(noticeParams);
    }

}
