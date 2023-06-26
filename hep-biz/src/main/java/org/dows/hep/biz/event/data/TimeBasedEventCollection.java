package org.dows.hep.biz.event.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.enums.EnumEventTriggerSpan;
import org.dows.hep.biz.cache.ICacheClear;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEventEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : wuzl
 * @date : 2023/6/17 23:51
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TimeBasedEventCollection implements ICacheClear {
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "下次触发时间")
    private LocalDateTime nextTriggerTime;

    @Schema(title = "事件列表")
    private List<TimeBasedEventGroup> eventGroups;

    @JsonIgnore
    private final ReentrantLock lock=new ReentrantLock();

    public List<List<TimeBasedEventGroup>> splitGroups(int splitNum){
        if(ShareUtil.XObject.isEmpty(eventGroups)) {
            return Collections.emptyList();
        }
        lock.lock();
        try{
            return ShareUtil.XCollection.split(eventGroups,splitNum);
        }finally {
            lock.unlock();
        }
    }
    public void removeTriggered(){
        if(ShareUtil.XObject.isEmpty(eventGroups)) {
            return;
        }
        lock.lock();
        try{
            for(int i=eventGroups.size()-1;i>=0;i--){
                TimeBasedEventCollection.TimeBasedEventGroup group=eventGroups.get(i);
                if(ShareUtil.XObject.notEmpty( group.getTriggeredTime())){
                    eventGroups.remove(i);
                }
            }
        }finally {
            lock.unlock();
        }
    }
    public void removeGroups(List<TimeBasedEventGroup> groups){
        if(ShareUtil.XObject.anyEmpty(eventGroups,groups)) {
            return;
        }
        lock.lock();
        try{
            groups.forEach(i->eventGroups.remove(i));
        }finally {
            lock.unlock();
        }
    }
    public void clear(){
        if(ShareUtil.XObject.isEmpty(eventGroups)){
            return;
        }
        eventGroups.forEach(i->{
            if(ShareUtil.XObject.isEmpty(i.getEventItems())) return;
            i.getEventItems().clear();
        });
        eventGroups.clear();
    }
    @Data
    @Builder
    @Accessors(chain = true)
    public static class TimeBasedEventGroup {
        @Schema(title = "案例人物id")
        private String casePersonId;

        @Schema(title = "案例事件id")
        private String caseEventId;

        @Schema(title = "触发类型 0-条件触发 1-第一期 2-第二期...5-第5期")
        private Integer triggerType;

        @Schema(title = "触发时间段 1-前期 2-中期 3-后期")
        private EnumEventTriggerSpan triggerSpan;

        @Schema(title = "当前待触发时间")
        private LocalDateTime triggeringTime;
        @Schema(title = "初始待触发时间")
        private LocalDateTime rawTriggeringTime;

        @Schema(title = "已触发时间")
        private volatile LocalDateTime triggeredTime;

        @Schema(title = "待触发游戏内天数")
        private Integer triggeringGameDay;

        @Schema(title = "事件列表")
        private List<ExperimentEventEntity> eventItems;
    }


}
