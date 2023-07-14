package org.dows.hep.biz.noticer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.notify.NoticeParams;
import org.dows.hep.api.notify.Notifiable;
import org.springframework.stereotype.Component;

/**
 * 期数结束消息通知器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PeriodEndNoticer extends AbstractPeriodNoticer implements Notifiable {

    @Override
    public void notice(NoticeParams noticeParams) {
        doNotice(noticeParams);
    }
}
