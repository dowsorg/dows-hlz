package org.dows.hep.biz.event.sysevent.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSysEventEntity;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : wuzl
 * @date : 2023/8/24 10:46
 */

@Data
@Accessors(chain = true)
public class SysEventRow {

    public SysEventRow(ExperimentSysEventEntity entity){
        this.entity=entity;
        this.dealType=EnumSysEventDealType.of(entity.getEventType());
        this.triggeringTime=ShareUtil.XDate.localDT4Date(entity.getTriggeringTime());
        this.triggeredTime=ShareUtil.XDate.localDT4Date(entity.getTriggeredTime());
    }
    @Schema(title = "系统事件")
    private final ExperimentSysEventEntity entity;

    @Schema(title = "处理类型")
    private EnumSysEventDealType dealType;

    @Schema(title = "触发中时间")
    private LocalDateTime triggeringTime;

    @Schema(title = "已触发时间")
    private LocalDateTime triggeredTime;

    @Schema(title = "下个事件")
    private SysEventRow next;
    @Schema(title = "重试次数")
    private final AtomicInteger retryTimes=new AtomicInteger();

    private final ReentrantLock lock=new ReentrantLock();


    public String getEventId() {
        return null == entity ? "" : entity.getExperimentSysEventId();

    }
    public boolean isDealt(){
        if(null==entity){
            return true;
        }
        return EnumSysEventState.DEALT.getCode().equals(entity.getState());
    }
    public boolean canTrigger(LocalDateTime now,boolean repeatFlag) {
        if (null == entity) {
            return false;
        }
        if (!repeatFlag && null != entity.getTriggeredTime()) {
            return false;
        }
        return null != this.triggeringTime && this.triggeringTime.compareTo(now) <= 0;
    }

    public void setTrigging(ExperimentTimePoint timePoint){
        entity.setTriggeringTime(ShareUtil.XDate.localDT2Date(timePoint.getRealTime()))
                .setTriggeringGameDay(timePoint.getGameDay());
        this.setTriggeringTime(timePoint.getRealTime());
    }

    public void setTriggerd(ExperimentTimePoint timePoint){
        entity.setTriggeredTime(ShareUtil.XDate.localDT2Date(timePoint.getRealTime()))
                .setTriggeredGameDay(timePoint.getGameDay())
                .setTriggeredPeriod(timePoint.getPeriod())
                .setState(EnumSysEventState.TRIGGERED.getCode());
        this.setTriggeredTime(timePoint.getRealTime());

    }

}
