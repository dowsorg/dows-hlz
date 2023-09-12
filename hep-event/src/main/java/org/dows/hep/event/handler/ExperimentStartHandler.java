package org.dows.hep.event.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.WsMessageResponse;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.biz.event.EventScheduler;
import org.dows.hep.biz.request.ExperimentTaskParamsRequest;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.TimeUtil;
import org.dows.hep.entity.ExperimentPersonInsuranceEntity;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentPersonInsuranceService;
import org.dows.hep.service.ExperimentTaskScheduleService;
import org.dows.hep.task.ExperimentCalcTask;
import org.dows.hep.task.ExperimentFinishTask;
import org.dows.hep.task.handler.ExperimentFinishTaskHandler;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * todo
 * 实验开始事件
 * 1.websocket 通知客户端，解除客户端操作限制
 * 2.服务端该实验记录ExperimentTimer
 * 3.恢复相关的任务，事件等的定时器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentStartHandler extends AbstractEventHandler implements EventHandler<ExperimentRestartRequest> {

    private final ExperimentTaskScheduleService experimentTaskScheduleService;

    private final ExperimentPersonInsuranceService experimentPersonInsuranceService;

    private final ExperimentFinishTaskHandler experimentFinishTaskHandler;

    @Override
    public void exec(ExperimentRestartRequest experimentRestartRequest) {
        // 待更新集合
        List<ExperimentTimerEntity> updateExperimentTimerEntities = new ArrayList<>();
        // 查询实验期数
        List<ExperimentTimerEntity> experimentTimerEntityList = experimentTimerBiz
                .getPeriodsTimerList(experimentRestartRequest.getExperimentInstanceId());

        // 方案设计模式不需要设计时器，只有标准模式或沙盘模式才需要设计时器//null == experimentRestartRequest.getPeriods() && 或者实验是准备中时
        if (null == experimentRestartRequest.getPeriods()) {
            // 按期数分组
            Map<Integer, List<ExperimentTimerEntity>> experimentTimerMap = experimentTimerEntityList.stream()
                    .collect(Collectors.groupingBy(ExperimentTimerEntity::getPeriod));
            experimentTimerMap.forEach((k, v) -> {
                ExperimentTimerEntity experimentTimerEntity = v.stream()
                        .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                        .orElse(null);
                if (experimentTimerEntity != null) {
                    // 暂停开始时间
                    long pst = experimentTimerEntity.getPauseTime().getTime();
                    // 持续时间 = 暂停结束时间 - 暂停开始时间
                    long duration = experimentRestartRequest.getCurrentTime().getTime() - pst;

                    experimentTimerEntity.setStartTime(DateUtil.date(experimentTimerEntity.getStartTime().getTime() + duration));
                    experimentTimerEntity.setEndTime(DateUtil.date(experimentTimerEntity.getEndTime().getTime() + duration));
                    // 记录暂停时长
                    experimentTimerEntity.setPauseDuration(duration);
                    experimentTimerEntity.setPeriodTimer(0L);
                    // 修改实验状态，真正开始实验
                    experimentTimerEntity.setState(EnumExperimentState.ONGOING.getState());
                    experimentTimerEntity.setPaused(experimentRestartRequest.getPaused());
                    // 重新开始时间[暂停推迟后的开始时间]
                    long rs = experimentRestartRequest.getCurrentTime().getTime() + experimentTimerEntity.getPeriodInterval();
                    experimentTimerEntity.setRestartTime(DateUtil.date(rs));
                    updateExperimentTimerEntities.add(experimentTimerEntity);
                }
            });
            experimentTimerBiz.saveOrUpdateExperimentTimeExperimentState(experimentRestartRequest.getExperimentInstanceId(),
                    updateExperimentTimerEntities, EnumExperimentState.ONGOING);
            /**
             * todo 可优化缓存
             * HepContext hepContext = HepContext.getExperimentContext(experimentRestartRequest.getExperimentInstanceId());
             * hepContext.setState(ExperimentStateEnum.ONGOING);
             */
        } else/* if(experimentRestartRequest.getModel() == ExperimentModeEnum.SAND.getCode())*/ {
            // 获取当前时间
            long ct = experimentRestartRequest.getCurrentTime().getTime();
            //todo 计时器
            log.info("执行开始操作....");
            // 找出当前期数计时器集合且暂停次数为最大的
            ExperimentTimerEntity updateExperimentTimer = experimentTimerEntityList.stream()
                    .filter(t -> t.getPeriod() == experimentRestartRequest.getPeriods())
                    .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                    .orElse(null);
            if (updateExperimentTimer == null) {
                throw new ExperimentException("实验计时器不存在！");
            }
            if (!updateExperimentTimer.getPaused()) {
                throw new ExperimentException("当前实验已开始，请勿重复执行开始！");
            }
            // 暂停持续时间 = 当前时间（暂停结束时间） - 暂停开始时间
            long supendDuration = ct - updateExperimentTimer.getPauseTime().getTime();
            // 暂退推迟后的结束时间 = 元本期结束时间+暂停时间
            long deferEndTime = updateExperimentTimer.getEndTime().getTime() + supendDuration;
            // 设当前期数的暂停时长
            updateExperimentTimer.setPauseDuration(supendDuration);
            updateExperimentTimer.setEndTime(DateUtil.date(deferEndTime));

            // 重新开始时间[暂停结束时间]
            updateExperimentTimer.setRestartTime(experimentRestartRequest.getCurrentTime());

            updateExperimentTimer.setPeriodDuration(updateExperimentTimer.getPeriodDuration());
            updateExperimentTimer.setPaused(false);
            updateExperimentTimer.setState(EnumExperimentState.ONGOING.getState());

            // 更新当前期之后数据
            for (ExperimentTimerEntity currentPeriod : experimentTimerEntityList) {
                if (currentPeriod.getPeriod() > experimentRestartRequest.getPeriods()) {
                    // 重新设置当前期数的下一期开始时间，结束时间等
                    currentPeriod.setStartTime(DateUtil.date(currentPeriod.getStartTime().getTime() + supendDuration));
                    currentPeriod.setEndTime(DateUtil.date(currentPeriod.getEndTime().getTime() + supendDuration));
                }
                currentPeriod.setState(EnumExperimentState.ONGOING.getState());
                currentPeriod.setPaused(false);
                // 加入待更新集合
                updateExperimentTimerEntities.add(currentPeriod);
            }
            // 加入待更新集合
            updateExperimentTimerEntities.add(updateExperimentTimer);
            // 批量更新期数定时器
            boolean b = experimentTimerBiz
                    .saveOrUpdateExperimentTimeExperimentState(experimentRestartRequest.getExperimentInstanceId(),
                            updateExperimentTimerEntities, EnumExperimentState.ONGOING);

            if (b) {
                WsMessageResponse wsMessageResponse = new WsMessageResponse(EnumWebSocketType.EXPT_RESTART, experimentRestartRequest);
                // 通知客户端
                ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfosByExperimentId(experimentRestartRequest.getExperimentInstanceId());
                Set<Channel> channels = userInfos.keySet();
                for (Channel channel : channels) {
                    HepClientManager.sendInfoRetry(channel, MessageCode.MESS_CODE, Response.ok(wsMessageResponse), idGenerator.nextIdStr(), null);
                }
            }

            // 所有保险也会暂停，需要重新延长开始时间
            List<ExperimentPersonInsuranceEntity> insuranceEntityList = experimentPersonInsuranceService.lambdaQuery()
                    .eq(ExperimentPersonInsuranceEntity::getExperimentInstanceId, experimentRestartRequest.getExperimentInstanceId())
                    .eq(ExperimentPersonInsuranceEntity::getDeleted, false)
                    .list();
            if (insuranceEntityList != null && insuranceEntityList.size() > 0) {
                insuranceEntityList.forEach(insurance -> {
                    insurance.setExpdate(TimeUtil.addTimeByLong(insurance.getExpdate(), supendDuration));
                });
                experimentPersonInsuranceService.updateBatchById(insuranceEntityList);
            }
        }

        List<ExperimentGroupResponse> experimentGroupResponses = experimentGroupBiz
                .listGroup(experimentRestartRequest.getExperimentInstanceId());
        // 重置定时任务
        resetTimeTask(experimentRestartRequest, updateExperimentTimerEntities, experimentGroupResponses);
        // 突发事件检测
        final String appId = ShareBiz.checkAppId(null, experimentRestartRequest.getExperimentInstanceId());
        EventScheduler.Instance().scheduleTimeBasedEvent(appId, experimentRestartRequest.getExperimentInstanceId(), 5);
        //随访计划
        EventScheduler.Instance().scheduleFollowUpPlan(appId, experimentRestartRequest.getExperimentInstanceId(), 5);
        if (ConfigExperimentFlow.SWITCH2SysEvent) {
            EventScheduler.Instance().scheduleSysEvent(appId, experimentRestartRequest.getExperimentInstanceId(), 3);
        }
    }

    /**
     * 重置定时任务
     *
     * @param experimentRestartRequest
     * @param updateExperimentTimerEntities
     * @param experimentGroupResponses
     */
    private void resetTimeTask(ExperimentRestartRequest experimentRestartRequest,
                               List<ExperimentTimerEntity> updateExperimentTimerEntities,
                               List<ExperimentGroupResponse> experimentGroupResponses) {
        if (ConfigExperimentFlow.SWITCH2SysEvent) {
            return;
        }

        /**
         * 重设实验结束任务
         */
        ExperimentTimerEntity lastPeriods = experimentTimerBiz
                .getLastPeriods(experimentRestartRequest.getExperimentInstanceId(), EnumExperimentState.FINISH);

        List<ExperimentTimerEntity> entityList = experimentTimerBiz
                .getPeriodsTimerList(experimentRestartRequest.getExperimentInstanceId());

        //保存任务进计时器表，防止重启后服务挂了，一个任务每个实验每一期只能有一条数据
        ExperimentTaskScheduleEntity finishEntity = new ExperimentTaskScheduleEntity();
        ExperimentTaskScheduleEntity finishTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
                .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentFinishTask.getDesc())
                .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentRestartRequest.getExperimentInstanceId())
                .eq(ExperimentTaskScheduleEntity::getPeriods, lastPeriods.getPeriod())
                .one();
        if (finishTaskScheduleEntity != null && !ReflectUtil.isObjectNull(finishTaskScheduleEntity)) {
            BeanUtil.copyProperties(finishTaskScheduleEntity, finishEntity);
            finishEntity.setExecuteTime(DateUtil.date(entityList.get(entityList.size() - 1).getEndTime()));
            finishEntity.setTaskParams(JSON.toJSONString(ExperimentTaskParamsRequest.builder()
                    .experimentInstanceId(experimentRestartRequest.getExperimentInstanceId())
                    .period(lastPeriods.getPeriod())
                    .build()));
            finishEntity.setExecuted(false);
        } else {
            finishEntity = ExperimentTaskScheduleEntity.builder()
                    .experimentTaskTimerId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentRestartRequest.getExperimentInstanceId())
                    .taskBeanCode(EnumExperimentTask.experimentFinishTask.getDesc())
                    .taskParams(JSON.toJSONString(ExperimentTaskParamsRequest.builder()
                            .experimentInstanceId(experimentRestartRequest.getExperimentInstanceId())
                            .period(lastPeriods.getPeriod())
                            .build()))
                    .periods(lastPeriods.getPeriod())
                    .appId("3")
                    .executeTime(DateUtil.date(entityList.get(entityList.size() - 1).getEndTime()))
                    .executed(false)
                    .build();
        }
        experimentTaskScheduleService.saveOrUpdate(finishEntity);

        // 执行任务
       /* ExperimentFinishTask experimentFinishTask = new ExperimentFinishTask(experimentInstanceService,
                experimentParticipatorService, experimentTimerService, experimentTaskScheduleService, calculatorDispatcher,
                experimentRestartRequest.getExperimentInstanceId(), lastPeriods.getPeriod());*/
        ExperimentFinishTask experimentFinishTask = new ExperimentFinishTask(experimentRestartRequest.getExperimentInstanceId(),
                lastPeriods.getPeriod(), experimentFinishTaskHandler);

        taskScheduler.schedule(experimentFinishTask, entityList.get(entityList.size() - 1).getEndTime());
        /**
         * 每小组重设每期结束任务
         */
        for (ExperimentGroupResponse experimentGroupRespons : experimentGroupResponses) {
            for (ExperimentTimerEntity updateExperimentTimerEntity : updateExperimentTimerEntities) {
                //保存任务进计时器表，防止重启后服务挂了，一个任务每个实验每一期只能有一条数据
                ExperimentTaskScheduleEntity calcEntity = new ExperimentTaskScheduleEntity();
                ExperimentTaskScheduleEntity calcTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
                        .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentCalcTask.getDesc())
                        .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentRestartRequest.getExperimentInstanceId())
                        .eq(ExperimentTaskScheduleEntity::getExperimentGroupId, experimentGroupRespons.getExperimentGroupId())
                        .eq(ExperimentTaskScheduleEntity::getPeriods, lastPeriods.getPeriod())
                        .one();
                String taskParams1 = JSON.toJSONString(ExperimentTaskParamsRequest.builder()
                        .experimentInstanceId(experimentRestartRequest.getExperimentInstanceId())
                        .experimentGroupId(experimentGroupRespons.getExperimentGroupId())
                        .period(updateExperimentTimerEntity.getPeriod())
                        .build());
                if (calcTaskScheduleEntity != null && !ReflectUtil.isObjectNull(calcTaskScheduleEntity)) {
                    BeanUtil.copyProperties(calcTaskScheduleEntity, calcEntity);
                    calcEntity.setExecuteTime(DateUtil.date(entityList.get(entityList.size() - 1).getEndTime()));
                    calcEntity.setTaskParams(taskParams1);
                    calcEntity.setExecuted(false);
                } else {
                    calcEntity = new ExperimentTaskScheduleEntity()
                            .builder()
                            .experimentTaskTimerId(idGenerator.nextIdStr())
                            .experimentInstanceId(experimentRestartRequest.getExperimentInstanceId())
                            .experimentGroupId(experimentGroupRespons.getExperimentGroupId())
                            .taskBeanCode(EnumExperimentTask.experimentCalcTask.getDesc())
                            .taskParams(taskParams1)
                            .periods(updateExperimentTimerEntity.getPeriod())
                            .appId("3")
                            .executeTime(DateUtil.date(entityList.get(entityList.size() - 1).getEndTime()))
                            .executed(false)
                            .build();
                }
                experimentTaskScheduleService.saveOrUpdate(calcEntity);

                // 执行任务
                ExperimentCalcTask experimentCalcTask = new ExperimentCalcTask(
                        experimentTimerBiz,
                        calculatorDispatcher,
                        experimentTaskScheduleService,
                        experimentRestartRequest.getExperimentInstanceId(),
                        experimentGroupRespons.getExperimentGroupId(),
                        updateExperimentTimerEntity.getPeriod());

                taskScheduler.schedule(experimentCalcTask, entityList.get(entityList.size() - 1).getEndTime());
            }

        }
    }

}

