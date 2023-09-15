package org.dows.hep.biz.event;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExperimentMode;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.biz.dao.ExperimentInstanceDao;
import org.dows.hep.biz.spel.SpelCacheExecutor;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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
    private final static long DELAYSeconds4UserEvent =150;

    private final static long DELAYSeconds4SysEvent=30;

    private EventStarter(){
        s_instnace=this;
    }
    @Autowired
    private ExperimentInstanceDao experimentInstanceDao;

    private volatile boolean startedFlag=false;
    private static final String APPId="3";


    public void start(){
        /*if(startedFlag){
            return;
        }*/
        final Set<String> sysIds=new HashSet<>();
        final Set<String> userIds=new HashSet<>();
        try {
            List<ExperimentInstanceEntity> rowsExperiment = experimentInstanceDao.getRunningExperiment(
                    APPId , EnumExperimentState.UNBEGIN.getState(),  EnumExperimentState.SUSPEND.getState(),
                    DateUtil.offsetDay(new Date(),-2).toJdkDate(),
                    ExperimentInstanceEntity::getAppId,
                    ExperimentInstanceEntity::getExperimentInstanceId,
                    ExperimentInstanceEntity::getModel,
                    ExperimentInstanceEntity::getState);

            rowsExperiment.forEach(i -> {
                sysIds.add(i.getExperimentInstanceId());
                EventScheduler.Instance().scheduleSysEvent(i.getAppId(), i.getExperimentInstanceId(),DELAYSeconds4SysEvent);
                if(null!=i.getModel() &&i.getModel().equals(EnumExperimentMode.SAND.getCode())
                    &&i.getState()==EnumExperimentState.ONGOING.getState()){
                    userIds.add(i.getExperimentInstanceId());
                    EventScheduler.Instance().scheduleTimeBasedEvent(i.getAppId(), i.getExperimentInstanceId(), DELAYSeconds4UserEvent);
                    EventScheduler.Instance().scheduleFollowUpPlan(i.getAppId(), i.getExperimentInstanceId(), DELAYSeconds4UserEvent);
                }
            });
            SpelCacheExecutor.Instance().start(userIds.stream().toList());

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
