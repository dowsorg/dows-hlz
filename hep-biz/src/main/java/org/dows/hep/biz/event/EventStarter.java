package org.dows.hep.biz.event;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.biz.dao.ExperimentInstanceDao;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/10 11:10
 */
@Component
@Slf4j
public class EventStarter implements ApplicationListener<ContextRefreshedEvent> {

    private static volatile EventStarter s_instnace;
    public static EventStarter Instance(){
        return s_instnace;
    }
    private final static long DELAYSeconds=180;

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
        int cnt=0;
        try {
            List<ExperimentInstanceEntity> rowsExperiment = experimentInstanceDao.getRunningExperiment(
                    null, EnumExperimentState.ONGOING.getState(), EnumExperimentState.SUSPEND.getState(),
                    ExperimentInstanceEntity::getAppId,
                    ExperimentInstanceEntity::getExperimentInstanceId,
                    ExperimentInstanceEntity::getState);
            cnt= rowsExperiment.size();
            rowsExperiment.forEach(i -> {
                EventScheduler.Instance().scheduleTimeBasedEvent(i.getAppId(), i.getExperimentInstanceId(), DELAYSeconds);
            });
            log.info(String.format("EventStarter.start succ. cnt:%s id:%s",rowsExperiment.size(),
                    String.join(",", ShareUtil.XCollection.map(rowsExperiment, ExperimentInstanceEntity::getExperimentInstanceId))));
            startedFlag=true;
        }catch (Exception ex){
            log.error(String.format( "EventStarter.start err. cnt:%s", cnt),ex);
        }

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.start();
    }
}
