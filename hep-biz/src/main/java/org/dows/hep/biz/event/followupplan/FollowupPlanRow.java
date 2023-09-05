package org.dows.hep.biz.event.followupplan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentFollowupPlanEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : wuzl
 * @date : 2023/9/2 19:16
 */

@Accessors(chain = true)
public class FollowupPlanRow {

    public FollowupPlanRow(ExperimentFollowupPlanEntity entity){
        this.entity=entity;
        this.doingTime=ShareUtil.XDate.localDT4Date(entity.getDoingTime());
        this.doneTime=ShareUtil.XDate.localDT4Date(entity.getDoneTime());
    }
    @Getter
    @Schema(title = "随访计划")
    private final ExperimentFollowupPlanEntity entity;

    @Getter
    @Schema(title = "触发中时间")
    private LocalDateTime doingTime;

    @Getter
    @Schema(title = "已触发时间")
    private LocalDateTime doneTime;

    @Getter
    @Schema(title = "下次随访天数")
    private Integer nextTodoDay;

    @Getter
    @Schema(title = "重试次数")
    private final AtomicInteger retryTimes=new AtomicInteger();

    public Integer getTodoDay(){
        return entity.getTodoDay();
    }
    public FollowupPlanRow saveNextTodoDay(){
        entity.setTodoDay(this.nextTodoDay);
        return this;
    }
    public FollowupPlanRow setNextTodoDay(int day){
        nextTodoDay=day;
        return this;
    }

    public boolean isTriggering() {
        return null == this.getDoingTime() || null == this.getDoneTime();
    }
    public boolean canTrigger(LocalDateTime now) {
        return null != this.getDoingTime() && this.getDoingTime().compareTo(now) <= 0;
    }

    public FollowupPlanRow setTriggering(LocalDateTime ldt){
        doingTime=ldt;
        entity.setDoingTime(ShareUtil.XDate.localDT2Date(ldt));
        return this;
    }

    public FollowupPlanRow setTriggered(ExperimentTimePoint timePoint){
        doneTime=timePoint.getRealTime();
        entity.setDoneTime(ShareUtil.XDate.localDT2Date(timePoint.getRealTime()))
                .setDoneDay(timePoint.getGameDay())
                .setDoneTimes(Optional.ofNullable( entity.getDoneTimes()).orElse(0)+1);
        return this;
    }

    public ExperimentFollowupPlanEntity toSaveEntity(){
        return CopyWrapper.create(ExperimentFollowupPlanEntity::new).endFrom(getEntity())
                .setTodoDay(this.getNextTodoDay());
    }

}
