package org.dows.hep.biz.noticer;

import org.dows.hep.api.enums.EnumNoticeType;
import org.dows.hep.api.notify.NoticeParams;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;

public class AbstractPeriodNoticer {

    protected void doNotice(NoticeParams noticeParams) {
        // 获取通知消息
        EnumNoticeType noticeType = noticeParams.getNoticeType();
        if (noticeType == EnumNoticeType.BoardCastSysNotice) {
            HepClientManager.broadcastSysMsg(MessageCode.MESS_CODE, noticeParams);
        }
        if (noticeType == EnumNoticeType.BoardCastSysEvent) {
            HepClientManager.broadcastSysMsg(MessageCode.MESS_CODE, noticeParams);
        }

    }
}
