package org.dows.hep.biz.event.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEventEntity;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/6/17 23:51
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonBasedEventCollection {
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "事件列表")
    private List<PersonBasedEventGroup> eventGroups;

    @JsonIgnore
    private final ReentrantLock lock=new ReentrantLock();

    public List<List<PersonBasedEventCollection.PersonBasedEventGroup>> splitGroups(int splitNum, Set<String> personIds){
        if(ShareUtil.XObject.isEmpty(eventGroups)) {
            return Collections.emptyList();
        }
        lock.lock();
        try{
            if(ShareUtil.XObject.isEmpty(personIds)) {
                return ShareUtil.XCollection.split(eventGroups, splitNum);
            }else{
                return ShareUtil.XCollection.split(eventGroups.stream()
                        .filter(i->personIds.contains(i.getExperimentPersonId()))
                        .collect(Collectors.toList()),splitNum);
            }
        }finally {
            lock.unlock();
        }
    }
    public void removeGroup(PersonBasedEventCollection.PersonBasedEventGroup group){
        if(ShareUtil.XObject.anyEmpty(eventGroups,group)) {
            return;
        }
        lock.lock();
        try{
            eventGroups.remove(group);
        }finally {
            lock.unlock();
        }
    }

    @Data
    @Builder
    @Accessors(chain = true)
    public static class PersonBasedEventGroup {


        @Schema(title = "实验人物id")
        private String experimentPersonId;

        @Schema(title = "事件列表")
        private List<ExperimentEventEntity> eventItems;

        @Schema(title = "重试次数")
        private final AtomicInteger retryTimes=new AtomicInteger();

        @JsonIgnore
        private final ReentrantLock lock=new ReentrantLock();

    }
}
