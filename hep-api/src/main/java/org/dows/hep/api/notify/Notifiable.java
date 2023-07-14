package org.dows.hep.api.notify;

public interface Notifiable {
    String genMsg(NoticeParams noticeParams);

    void notice();
}
