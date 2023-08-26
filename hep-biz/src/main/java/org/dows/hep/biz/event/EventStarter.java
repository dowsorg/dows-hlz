package org.dows.hep.biz.event;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExperimentMode;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.biz.dao.ExperimentInstanceDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : wuzl
 * @date : 2023/7/10 11:10
 */
@Component
@Slf4j
public class EventStarter implements ApplicationListener<ApplicationStartedEvent> {

    private static volatile EventStarter s_instnace;
    public static EventStarter Instance(){
        return s_instnace;
    }
    private final static long DELAYSeconds4UserEvent =180;

    private final static long DELAYSeconds4SysEvent=15;

    private EventStarter(){
        s_instnace=this;
    }
    @Autowired
    private ExperimentInstanceDao experimentInstanceDao;

    private volatile boolean startedFlag=false;


    public void start(){
        if(startedFlag){
            return;
        }
        final Set<String> sysIds=new HashSet<>();
        final Set<String> userIds=new HashSet<>();
        try {
            List<ExperimentInstanceEntity> rowsExperiment = experimentInstanceDao.getRunningExperiment(
                    null, EnumExperimentState.ONGOING.getState(), EnumExperimentState.SUSPEND.getState(),
                    DateUtil.offsetDay(new Date(),-5).toJdkDate(),
                    ExperimentInstanceEntity::getAppId,
                    ExperimentInstanceEntity::getExperimentInstanceId,
                    ExperimentInstanceEntity::getModel,
                    ExperimentInstanceEntity::getState);
            final LocalDateTime nextTime4User=LocalDateTime.now().plusSeconds(DELAYSeconds4UserEvent);
            final LocalDateTime nextTime4Sys=LocalDateTime.now().plusSeconds(DELAYSeconds4UserEvent);
            rowsExperiment.forEach(i -> {
                sysIds.add(i.getExperimentInstanceId());
                EventScheduler.Instance().scheduleSysEvent(new ExperimentCacheKey(i.getAppId(), i.getExperimentInstanceId()),nextTime4Sys);
                if(null!=i.getModel() &&i.getModel().equals(EnumExperimentMode.SAND.getCode())){
                    userIds.add(i.getExperimentInstanceId());
                }
                EventScheduler.Instance().scheduleTimeBasedEvent(new ExperimentCacheKey(i.getAppId(), i.getExperimentInstanceId()), nextTime4User);
            });
            log.info(String.format("EventStarter.start succ. cntSys:%s cntUser:%s sysIds:%s userIds:%s",
                    sysIds.size(),userIds.size(),
                    String.join(",", sysIds),String.join(",", userIds)));
            startedFlag=true;
        }catch (Exception ex){
            log.error(String.format("EventStarter.start error. cntSys:%s cntUser:%s sysIds:%s userIds:%s",
                    sysIds.size(),userIds.size(),
                    String.join(",", sysIds),String.join(",", userIds)),ex);
        }

    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        this.start();
    }
}
