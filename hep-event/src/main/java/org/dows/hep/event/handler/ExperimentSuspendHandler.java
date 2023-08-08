package org.dows.hep.event.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.WsMessageResponse;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumWebSocketType;
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
public class ExperimentSuspendHandler extends AbstractEventHandler implements EventHandler<ExperimentRestartRequest> {


    @Override
    public void exec(ExperimentRestartRequest experimentRestartRequest) {
        //todo 暂停定时器
        log.info("暂停计时器....");

        // 待更新集合
        List<ExperimentTimerEntity> updateExperimentTimerEntities = new ArrayList<>();
        // 获取当前实验所有期数计时器列表并按期数递增排序
        List<ExperimentTimerEntity> experimentTimerEntityList = experimentTimerBiz
                .getPeriodsTimerList(experimentRestartRequest.getExperimentInstanceId());
        // 如果期数为空，则可能为设计模式，或分配小组机构等场景
        if (experimentRestartRequest.getPeriods() == null) {
            // todo 更新所有计时器时间
            for (ExperimentTimerEntity experimentTimerEntity : experimentTimerEntityList) {
                ExperimentTimerEntity updExperimentTimerEntity = new ExperimentTimerEntity();
                updExperimentTimerEntity.setId(experimentTimerEntity.getId());
                updExperimentTimerEntity.setPauseCount(experimentTimerEntity.getPauseCount() + 1);
                updExperimentTimerEntity.setState(EnumExperimentState.PREPARE.getState());
                updExperimentTimerEntity.setPeriodDuration(experimentTimerEntity.getPeriodDuration());
                updExperimentTimerEntity.setPeriodTimer(0L);
                updExperimentTimerEntity.setPauseTime(experimentRestartRequest.getCurrentTime());
                updExperimentTimerEntity.setPaused(experimentRestartRequest.getPaused());
                updateExperimentTimerEntities.add(updExperimentTimerEntity);
            }
            // 保存或更新实验计时器
            experimentTimerBiz.saveOrUpdateExperimentTimeExperimentState(experimentRestartRequest.getExperimentInstanceId(),
                    updateExperimentTimerEntities, EnumExperimentState.PREPARE);
            /**
             * todo 可优化缓存
             */
        } else {
            // 获取当前时间
            long ct = experimentRestartRequest.getCurrentTime().getTime();
            // 找出当前期数计时器集合，且暂停次数为最大的
            ExperimentTimerEntity experimentTimerEntity = experimentTimerEntityList.stream()
                    .filter(t -> t.getPeriod() == experimentRestartRequest.getPeriods())
                    .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                    .orElse(null);
            if (experimentTimerEntity == null) {
                throw new ExperimentException("实验计时器不存在！");
            }
            if (experimentTimerEntity.getPaused()) {
                throw new ExperimentException("当前实验已暂停，请勿重复执行暂停！");
            }
            // 如果当前时间不在本期开始和结束之间
            if (ct <= experimentTimerEntity.getStartTime().getTime() || ct >= experimentTimerEntity.getEndTime().getTime()) {
                throw new ExperimentException(String.format("无法为当前{%s}期执行暂停,该期开始时间为:{%s}，结束时间:{%s}",
                        experimentTimerEntity.getPeriod(),
                        DateUtil.formatDateTime(DateUtil.date(experimentTimerEntity.getStartTime())),
                        DateUtil.formatDateTime(DateUtil.date(experimentTimerEntity.getEndTime()))));
            }

            // 期数持续时长 = 当前时间-暂停时间-间隔时间
            long timer = ct - experimentTimerEntity.getRestartTime().getTime()
                    + experimentTimerEntity.getPeriodTimer();
            // 暂停时新增暂停记录
            ExperimentTimerEntity addExperimentTimer = ExperimentTimerEntity.builder()
                    .experimentTimerId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentTimerEntity.getExperimentInstanceId())
                    .startTime(experimentTimerEntity.getStartTime())
                    .endTime(experimentTimerEntity.getEndTime())
                    .periodDuration(experimentTimerEntity.getPeriodDuration())
                    .period(experimentTimerEntity.getPeriod())
                    .periodInterval(experimentTimerEntity.getPeriodInterval())
                    .appId(experimentTimerEntity.getAppId())
                    .model(experimentTimerEntity.getModel())
                    .pauseCount(experimentTimerEntity.getPauseCount() + 1)
                    //.deferEndTime(experimentTimerEntity.getDeferEndTime())
                    .restartTime(experimentTimerEntity.getRestartTime())

                    .pauseTime(experimentRestartRequest.getCurrentTime())

                    .state(EnumExperimentState.SUSPEND.getState())
                    .paused(true)
                    .periodTimer(timer)
                    .build();
            updateExperimentTimerEntities.add(addExperimentTimer);
            // 保存或更新实验计时器状态为暂停
            boolean b = experimentTimerBiz.saveOrUpdateExperimentTimeExperimentState(experimentRestartRequest.getExperimentInstanceId(),
                    updateExperimentTimerEntities, EnumExperimentState.SUSPEND);
            if (b) {
                /**
                 * 设置当前实验上下文信息
                 * ExperimentContext experimentContext = new ExperimentContext();
                 * experimentContext.setExperimentId(experimentRestartRequest.getExperimentInstanceId());
                 * experimentContext.setState(EnumExperimentState.SUSPEND);
                 * ExperimentContext.set(experimentContext);
                 */
                WsMessageResponse wsMessageResponse = new WsMessageResponse(EnumWebSocketType.EXPT_SUSPEND, experimentRestartRequest);
                // 通知客户端
                ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();
                Set<Channel> channels = userInfos.keySet();
                for (Channel channel : channels) {
                    HepClientManager.sendInfoRetry(channel, MessageCode.MESS_CODE, Response.ok(wsMessageResponse),idGenerator.nextIdStr(),null);
                }
                log.info("暂停实验:{}", JSONUtil.toJsonStr(wsMessageResponse));
            }
        }
        // 重置定时任务
        taskScheduler.resetSchedule();

    }
}