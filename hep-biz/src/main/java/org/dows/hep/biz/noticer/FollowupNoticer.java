package org.dows.hep.biz.noticer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.notify.NoticeContent;
import org.dows.hep.api.notify.Notifiable;
import org.springframework.stereotype.Component;

/**
 * 检测随访通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FollowupNoticer extends AbstractPeriodNoticer implements Notifiable {
    @Override
    public void notice(NoticeContent noticeContent) {

    }
}
