package org.dows.hep.event.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.biz.event.EventScheduler;
import org.dows.hep.biz.task.ExperimentCalcTask;
import org.dows.hep.biz.task.ExperimentFinishTask;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentTaskScheduleService;
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


    @Override
    public void exec(ExperimentRestartRequest experimentRestartRequest) {
        // 待更新集合
        List<ExperimentTimerEntity> updateExperimentTimerEntities = new ArrayList<>();
        // 查询实验期数
        List<ExperimentTimerEntity> experimentTimerEntityList = experimentTimerBiz
                .getCurrentPeriods(experimentRestartRequest.getExperimentInstanceId());

        List<ExperimentGroupResponse> experimentGroupResponses = experimentGroupBiz
                .listGroup(experimentRestartRequest.getExperimentInstanceId());

        // 按期数分组
        Map<Integer, List<ExperimentTimerEntity>> experimentTimerMap = experimentTimerEntityList.stream()
                .collect(Collectors.groupingBy(ExperimentTimerEntity::getPeriod));

        // 方案设计模式不需要设计时器，只有标准模式或沙盘模式才需要设计时器//null == experimentRestartRequest.getPeriods() &&
        if (null == experimentRestartRequest.getPeriods()) {
            experimentTimerMap.forEach((k, v) -> {
                ExperimentTimerEntity experimentTimerEntity = v.stream()
                        .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                        .orElse(null);
                if (experimentTimerEntity != null) {
                    // 暂停开始时间
                    long pst = experimentTimerEntity.getPauseStartTime().getTime();
                    // 持续时间 = 暂停结束时间 - 暂停开始时间
                    long duration = experimentRestartRequest.getCurrentTime().getTime() - pst;
                    experimentTimerEntity.setStartTime(experimentTimerEntity.getStartTime() + duration);
                    experimentTimerEntity.setEndTime(experimentTimerEntity.getEndTime() + duration);
                    experimentTimerEntity.setDuration(duration);
                    // 修改实验状态，真正开始实验
                    experimentTimerEntity.setState(EnumExperimentState.ONGOING.getState());
                    experimentTimerEntity.setPaused(experimentRestartRequest.getPaused());
                    experimentTimerEntity.setPauseEndTime(experimentRestartRequest.getCurrentTime());
                    updateExperimentTimerEntities.add(experimentTimerEntity);
                }
            });
            experimentTimerBiz.saveOrUpdateExperimentTimeExperimentState(experimentRestartRequest.getExperimentInstanceId(),
                    updateExperimentTimerEntities, EnumExperimentState.ONGOING);
            //1、更改缓存
            /**
             * todo 需要调整
             */
            /*HepContext hepContext = HepContext.getExperimentContext(experimentRestartRequest.getExperimentInstanceId());
            hepContext.setState(ExperimentStateEnum.ONGOING);*/
        } else/* if(experimentRestartRequest.getModel() == ExperimentModeEnum.SAND.getCode())*/ {
            //todo 计时器
            log.info("执行开始操作....");

            // 找出当前期数计时器集合
            List<ExperimentTimerEntity> collect = experimentTimerEntityList.stream()
                    .filter(t -> t.getPeriod() == experimentRestartRequest.getPeriods())
                    .collect(Collectors.toList());
            //暂停次数为最大的
            ExperimentTimerEntity updateExperimentTimer = collect.stream()
                    .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                    .orElse(null);
            if (updateExperimentTimer == null) {
                throw new ExperimentException("实验计时器不存在！");
            }

            if (!updateExperimentTimer.getPaused()) {
                throw new ExperimentException("当前实验已开始，请勿重复执行开始！");
            }
            // 暂停开始时间
            long pst = updateExperimentTimer.getPauseStartTime().getTime();
            // 持续时间 = 暂停结束时间 - 暂停开始时间
            long duration = experimentRestartRequest.getCurrentTime().getTime() - pst;
            // 设当前期数的暂停时长
            updateExperimentTimer.setDuration(duration);
            updateExperimentTimer.setPaused(false);
            // 本期结束时间 = 元本期结束时间+暂停时间
            updateExperimentTimer.setEndTime(updateExperimentTimer.getEndTime() + duration);
            // 设置暂停结束时间
            updateExperimentTimer.setPauseEndTime(experimentRestartRequest.getCurrentTime());
            updateExperimentTimer.setPeriodInterval(updateExperimentTimer.getPeriodInterval());
            // 加入待更新集合
            updateExperimentTimerEntities.add(updateExperimentTimer);
            // 剔除当前期数
            experimentTimerEntityList.removeAll(collect);
            if (experimentTimerEntityList.size() > 0) {
                // 从新排序，确保当前期数后的期数自增
                experimentTimerEntityList = experimentTimerEntityList.stream()
                        .sorted(Comparator.comparingInt(ExperimentTimerEntity::getPeriod))
                        .collect(Collectors.toList());
                for (ExperimentTimerEntity currentPeriod : experimentTimerEntityList) {
                    if (currentPeriod.getPeriod() >= experimentRestartRequest.getPeriods()) {
                        // 重新设置当前期数的下一期开始时间，结束时间等
                        currentPeriod.setStartTime(currentPeriod.getStartTime() + duration);
                        currentPeriod.setEndTime(currentPeriod.getEndTime() + duration);
                    }
                }
                // 加入待更新集合
                updateExperimentTimerEntities.addAll(experimentTimerEntityList);
            }
            // 批量更新期数定时器
            boolean b = experimentTimerBiz.saveOrUpdateExperimentTimeExperimentState(experimentRestartRequest.getExperimentInstanceId(),
                    updateExperimentTimerEntities, EnumExperimentState.ONGOING);

            if (b) {
                // 通知客户端
                ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();
                Set<Channel> channels = userInfos.keySet();
                for (Channel channel : channels) {
                    HepClientManager.sendInfo(channel, MessageCode.MESS_CODE, experimentRestartRequest);
                }
            }
        }
        // 重置定时任务
        resetTimeTask(experimentRestartRequest, updateExperimentTimerEntities, experimentGroupResponses);
        // 突发事件检测
        EventScheduler.Instance().scheduleTimeBasedEvent(null,experimentRestartRequest.getExperimentInstanceId(),5);

    }

    /**
     * 重置定时任务
     * @param experimentRestartRequest
     * @param updateExperimentTimerEntities
     * @param experimentGroupResponses
     */
    private void resetTimeTask(ExperimentRestartRequest experimentRestartRequest,
                           List<ExperimentTimerEntity> updateExperimentTimerEntities,
                           List<ExperimentGroupResponse> experimentGroupResponses) {
        /**
         * 重设实验结束任务
         */
        ExperimentTimerEntity lastPeriods = experimentTimerBiz
                .getLastPeriods(experimentRestartRequest.getExperimentInstanceId(), EnumExperimentState.FINISH);

        //保存任务进计时器表，防止重启后服务挂了，一个任务每个实验每一期只能有一条数据
        ExperimentTaskScheduleEntity finishEntity = new ExperimentTaskScheduleEntity();
        ExperimentTaskScheduleEntity finishTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
                .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentFinishTask.getDesc())
                .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentRestartRequest.getExperimentInstanceId())
                .eq(ExperimentTaskScheduleEntity::getPeriods, lastPeriods.getPeriod())
                .one();
        String taskParams = "{\"experimentInstanceId\":\"" + experimentRestartRequest.getExperimentInstanceId()
                + "\",\"period\":" + lastPeriods.getPeriod() + "}";
        if (finishTaskScheduleEntity != null && !ReflectUtil.isObjectNull(finishTaskScheduleEntity)) {
            BeanUtil.copyProperties(finishTaskScheduleEntity, finishEntity);
            finishEntity.setExecuteTime(DateUtil.date(lastPeriods.getEndTime()));
            finishEntity.setTaskParams(taskParams);
            finishEntity.setExecuted(false);
        } else {
            finishEntity = new ExperimentTaskScheduleEntity()
                    .builder()
                    .experimentTaskTimerId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentRestartRequest.getExperimentInstanceId())
                    .taskBeanCode(EnumExperimentTask.experimentFinishTask.getDesc())
                    .taskParams(taskParams)
                    .periods(lastPeriods.getPeriod())
                    .appId("3")
                    .executeTime(DateUtil.date(lastPeriods.getEndTime()))
                    .executed(false)
                    .build();
        }
        experimentTaskScheduleService.saveOrUpdate(finishEntity);

        // 执行任务
        ExperimentFinishTask experimentFinishTask = new ExperimentFinishTask(experimentInstanceService,
                experimentParticipatorService, experimentTimerService, experimentTaskScheduleService,experimentScoreCalculator,
                experimentRestartRequest.getExperimentInstanceId(),lastPeriods.getPeriod());

        taskScheduler.schedule(experimentFinishTask, DateUtil.date(lastPeriods.getEndTime()));
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
                        .eq(ExperimentTaskScheduleEntity::getExperimentGroupId,experimentGroupRespons.getExperimentGroupId())
                        .eq(ExperimentTaskScheduleEntity::getPeriods, lastPeriods.getPeriod())
                        .one();
                String taskParams1 = "{\"experimentInstanceId\":\"" + experimentRestartRequest.getExperimentInstanceId() + "\",\"experimentGroupId\":\""
                        + experimentGroupRespons.getExperimentGroupId() + "\",\"period\":" + updateExperimentTimerEntity.getPeriod() + "}";
                if (calcTaskScheduleEntity != null && !ReflectUtil.isObjectNull(calcTaskScheduleEntity)) {
                    BeanUtil.copyProperties(calcTaskScheduleEntity, calcEntity);
                    calcEntity.setExecuteTime(DateUtil.date(updateExperimentTimerEntity.getEndTime()));
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
                            .executeTime(DateUtil.date(updateExperimentTimerEntity.getEndTime()))
                            .executed(false)
                            .build();
                }
                experimentTaskScheduleService.saveOrUpdate(calcEntity);

                // 执行任务
                ExperimentCalcTask experimentCalcTask = new ExperimentCalcTask(
                        experimentTimerBiz,
                        experimentScoreCalculator,
                        experimentTaskScheduleService,
                        experimentRestartRequest.getExperimentInstanceId(),
                        experimentGroupRespons.getExperimentGroupId(),
                        updateExperimentTimerEntity.getPeriod());

                taskScheduler.schedule(experimentCalcTask, DateUtil.date(updateExperimentTimerEntity.getEndTime()));
            }

        }
    }

}

