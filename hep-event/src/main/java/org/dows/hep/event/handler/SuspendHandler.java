package org.dows.hep.event.handler;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * todo
 * 实验暂停事件，
 * 1.websocket 通知客户端，禁止操作，同时根据实验ID,服务端拦截器中禁用该实验ID的客户端请求
 * 2.服务端记录/更新实验暂停时间，ExperimentTimer
 * 3.停止相关的任务和事件的计时器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SuspendHandler extends AbstractEventHandler implements EventHandler<ExperimentRestartRequest> {


    @Override
    public void exec(ExperimentRestartRequest experimentRestartRequest) {
        //todo 暂停定时器
        log.info("暂停计时器....");

        // 待更新集合
        List<ExperimentTimerEntity> updateExperimentTimerEntities = new ArrayList<>();
        // 查询实验期数列表
        List<ExperimentTimerEntity> experimentTimerEntityList = experimentTimerBiz.getCurrentPeriods(experimentRestartRequest);

        /*if (experimentRestartRequest.getPeriods() == null) {*/
        if (experimentRestartRequest.getPeriods() == null) {
            // todo 更新所有计时器时间
            for (ExperimentTimerEntity experimentTimerEntity : experimentTimerEntityList) {
                ExperimentTimerEntity updExperimentTimerEntity = new ExperimentTimerEntity();
                //ExperimentTimerEntity updExperimentTimerEntity = experimentTimerEntity;
                updExperimentTimerEntity.setExperimentTimerId(idGenerator.nextIdStr());
                updExperimentTimerEntity.setPauseCount(experimentTimerEntity.getPauseCount() + 1);
                updExperimentTimerEntity.setExperimentInstanceId(experimentTimerEntity.getExperimentInstanceId());
                updExperimentTimerEntity.setPeriodInterval(experimentTimerEntity.getPeriodInterval());
                updExperimentTimerEntity.setPeriod(experimentTimerEntity.getPeriod());
                updExperimentTimerEntity.setAppId(experimentTimerEntity.getAppId());
                updExperimentTimerEntity.setModel(experimentTimerEntity.getModel());
                updExperimentTimerEntity.setState(experimentTimerEntity.getState());
                updExperimentTimerEntity.setStartTime(experimentTimerEntity.getStartTime());
                updExperimentTimerEntity.setEndTime(experimentTimerEntity.getEndTime());
                updExperimentTimerEntity.setPauseStartTime(experimentRestartRequest.getCurrentTime());
                updExperimentTimerEntity.setPaused(experimentRestartRequest.getPaused());
                updateExperimentTimerEntities.add(updExperimentTimerEntity);
            }
            // 保存或更新实验计时器
            boolean b = experimentTimerBiz.saveOrUpdateExperimentTimeExperimentState(experimentRestartRequest.getExperimentInstanceId(),
                    updateExperimentTimerEntities,ExperimentStateEnum.PREPARE);
            //1、更改缓存
            /**
             * todo 需要调整
             */
            /*HepContext hepContext = HepContext.getExperimentContext(experimentRestartRequest.getExperimentInstanceId());
            hepContext.setState(ExperimentStateEnum.PREPARE);*/

        } else /*if(experimentRestartRequest.getModel() == ExperimentModeEnum.SAND.getCode())*/ {
            // 找出当前期数计时器集合
            List<ExperimentTimerEntity> collect = experimentTimerEntityList.stream()
                    .filter(t -> t.getPeriod() == experimentRestartRequest.getPeriods())
                    .collect(Collectors.toList());

            //暂停次数为最大的
            ExperimentTimerEntity experimentTimerEntity = collect.stream()
                    .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                    .orElse(null);
            if (experimentTimerEntity == null) {
                throw new ExperimentException("实验计时器不存在！");
            }
            if (experimentTimerEntity.getPaused()) {
                throw new ExperimentException("当前实验已暂停，请勿重复执行暂停！");
            }
            ExperimentTimerEntity addExperimentTimer = ExperimentTimerEntity.builder()
                    .experimentTimerId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentTimerEntity.getExperimentInstanceId())
                    .startTime(experimentTimerEntity.getStartTime())
                    .endTime(experimentTimerEntity.getEndTime())
                    .period(experimentTimerEntity.getPeriod())
                    .periodInterval(experimentTimerEntity.getPeriodInterval())
                    .appId(experimentTimerEntity.getAppId())
                    .model(experimentTimerEntity.getModel())
                    .state(experimentTimerEntity.getState())
                    .pauseCount(experimentTimerEntity.getPauseCount() + 1)
                    .paused(true)
                    .pauseStartTime(experimentRestartRequest.getCurrentTime())
                    .build();
            updateExperimentTimerEntities.add(addExperimentTimer);
            // 保存或更新实验计时器
            boolean b = experimentTimerBiz.saveOrUpdateExperimentTimeExperimentState(experimentRestartRequest.getExperimentInstanceId(),
                    updateExperimentTimerEntities,ExperimentStateEnum.SUSPEND);
            if (b) {
                // 设置当前实验上下文信息
                ExperimentContext experimentContext = new ExperimentContext();
                experimentContext.setExperimentId(experimentRestartRequest.getExperimentInstanceId());
                experimentContext.setState(ExperimentStateEnum.SUSPEND);
                ExperimentContext.set(experimentContext);

                // 通知客户端
                ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();
                Set<Channel> channels = userInfos.keySet();
                for (Channel channel : channels) {
                    HepClientManager.sendInfo(channel, MessageCode.MESS_CODE, experimentRestartRequest);
                }
            }
        }


    }
}