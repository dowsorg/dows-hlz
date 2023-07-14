package org.dows.hep.biz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumNoticeType;
import org.dows.hep.api.notify.Notifiable;
import org.dows.hep.api.notify.NoticeParams;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;

/**
 * 实验通知任务
 */
@Slf4j
@RequiredArgsConstructor
public class ExperimentNoticeTask implements Runnable {

    private final Notifiable notifiable;
    private final NoticeParams noticeParams;
    //

    @Override
    public void run() {
        // 获取通知消息
        String msg = notifiable.genMsg(noticeParams);
        EnumNoticeType noticeType = noticeParams.getNoticeType();
        if(noticeType == EnumNoticeType.BoardCastSysNotice) {
            HepClientManager.broadcastSysMsg(MessageCode.MESS_CODE, msg);
        }
        if(noticeType == EnumNoticeType.BoardCastSysEvent) {
            HepClientManager.broadcastSysMsg(MessageCode.MESS_CODE, msg);
        }
    }


}
