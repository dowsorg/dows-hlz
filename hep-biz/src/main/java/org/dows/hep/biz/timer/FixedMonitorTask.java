package org.dows.hep.biz.timer;

import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.util.TimeUtil;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.OperateFollowupTimerEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.OperateFollowupTimerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author jx
 * @date 2023/6/9 14:21
 */
@Component
@RequiredArgsConstructor
public class FixedMonitorTask {
    private final OperateFollowupTimerService operateFollowupTimerService;
    private final ExperimentInstanceService experimentInstanceService;


    @Scheduled(cron = "*/15 * * * * ?")
    public void execute() {
        //1、获取正在运行中的实验列表
        List<ExperimentInstanceEntity> instanceList = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getState,1)
                .eq(ExperimentInstanceEntity::getDeleted,false)
                .list();
        if(instanceList != null && instanceList.size() > 0){
            instanceList.forEach(instance->{
                List<OperateFollowupTimerEntity> timerEntityList = operateFollowupTimerService.lambdaQuery()
                        .eq(OperateFollowupTimerEntity::getDeleted,false)
                        .eq(OperateFollowupTimerEntity::getExperimentInstanceId,instance.getExperimentInstanceId())
                        .eq(OperateFollowupTimerEntity::getIsRegister,false)
                        .list();
                if(timerEntityList != null && timerEntityList.size() > 0){
                    timerEntityList.forEach(timerEntity->{
                        //2、早于当前时间的则发送websocket推送消息
                        try {
                            Boolean flag = TimeUtil.isBeforeTime(timerEntity.getTodoTime(),new Date());
                            if(flag){
                                //todo websocket发送消息
                            }
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        }
    }
}
