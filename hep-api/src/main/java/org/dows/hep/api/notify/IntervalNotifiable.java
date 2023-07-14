package org.dows.hep.api.notify;

/**
 * 间隔通知器
 */
public interface IntervalNotifiable extends Notifiable{
    void startNoitce();

    void endNotice();
}
