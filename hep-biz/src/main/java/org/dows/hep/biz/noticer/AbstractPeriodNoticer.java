package org.dows.hep.biz.noticer;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumNoticeType;
import org.dows.hep.api.notify.NoticeContent;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;

@Slf4j
public class AbstractPeriodNoticer {

    protected void doNotice(NoticeContent noticeContent) {
        // 获取通知消息
        EnumNoticeType noticeType = noticeContent.getType();
        if (noticeType == EnumNoticeType.BoardCastSysNotice) {
            log.info("系统消息通知客户端：{}", JSONUtil.toJsonStr(noticeContent));
            HepClientManager.broadcastSysMsg(MessageCode.MESS_CODE, noticeContent);
        }
        if (noticeType == EnumNoticeType.BoardCastSysEvent) {
            log.info("系统事件通知客户端：{}", JSONUtil.toJsonStr(noticeContent));
            HepClientManager.broadcastSysMsg(MessageCode.MESS_CODE, noticeContent);
        }

    }
}
