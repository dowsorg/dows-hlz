package org.dows.hep.biz.user.experiment;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.enums.EnumExperimentMode;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.event.IntervalEvent;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.api.user.experiment.response.IntervalResponse;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentSettingService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:实验:实验计时器
 * @date 2023年4月23日 上午9:44:34
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ExperimentTimerBiz {

    private final ExperimentTimerService experimentTimerService;
    private final ExperimentInstanceService experimentInstanceService;
    private final ExperimentParticipatorService experimentParticipatorService;
    private final ExperimentSettingService experimentSettingService;
    private final ApplicationEventPublisher applicationEventPublisher;


    /**
     * @param
     * @return
     * @说明: 获取实验倒计时
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public IntervalResponse countdown(String experimentInstanceId) {
        StringBuilder sb=new StringBuilder();
        IntervalResponse intervalResponse = new IntervalResponse();
        try {
            sb.append(String.format("input:%s@%s ", experimentInstanceId,LocalDateTime.now()));
            Long sct = System.currentTimeMillis();

            List<ExperimentSettingEntity> list = experimentSettingService.lambdaQuery()
                    .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                    .list();

            if (list.size() == 0) {
                throw new ExperimentException("实验ID对应的实验不存在！");
            }

            /**
             * 方案设计模式
             * 说明： 方案设计不是所有小组同时进入实验，所以学生端不适用此处的倒计时
             */
            ExperimentSettingEntity schemeSettingEntity = list.stream()
                    .filter(e -> e.getConfigKey().equals(ExperimentSetting.SchemeSetting.class.getName()))
                    .findFirst()
                    .orElse(null);
            if (schemeSettingEntity != null) {
                ExperimentSetting.SchemeSetting schemeSetting =
                        JSONUtil.toBean(schemeSettingEntity.getConfigJsonVals(), ExperimentSetting.SchemeSetting.class);
                if (sct > schemeSetting.getSchemeEndTime().getTime()) {
                    intervalResponse.setSchemeTotalTime(0L);
                } else {
                    // 方案设计倒计时
                    Long schemeTime = schemeSetting.getSchemeEndTime().getTime() - System.currentTimeMillis();
                    intervalResponse.setSchemeTotalTime(schemeTime);
                }
                intervalResponse.setModel(EnumExperimentMode.SCHEME.getCode());
            }

            /**
             * 沙盘模式
             */
            ExperimentSettingEntity sandSettingEntity = list.stream()
                    .filter(e -> e.getConfigKey().equals(ExperimentSetting.SandSetting.class.getName()))
                    .findFirst()
                    .orElse(null);
            if (sandSettingEntity != null) {
                ExperimentSetting.SandSetting sandSetting =
                        JSONUtil.toBean(sandSettingEntity.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
                // 每期持续时长
                Map<String, Integer> durationMap = sandSetting.getDurationMap();
                // 期数
                Map<String, Integer> periodMap = sandSetting.getPeriodMap();
                // 对set排序一下
                List<String> keys = periodMap.keySet().stream().sorted().collect(Collectors.toList());
                // 每期对应的mock比列
                Map<String, Double> mockRateMap = new HashMap<>();
                int totalDay = 0;
                for (String s : keys) {
                    Integer duration = durationMap.get(s);
                    Integer period = periodMap.get(s);
                    totalDay += period;
                    double mockRate = Double.valueOf(period) / Double.valueOf(duration * 60);
                    mockRateMap.put(s, mockRate);
                }
                // 公共数据
                intervalResponse.setExperimentInstanceId(experimentInstanceId);
                intervalResponse.setSandTotalTime(Long.valueOf(totalDay));
                intervalResponse.setSandTimeUnit("天");
                intervalResponse.setModel(EnumExperimentMode.SAND.getCode());
                intervalResponse.setDurationMap(durationMap);
                intervalResponse.setMockRateMap(mockRateMap);
                intervalResponse.setPeriodMap(periodMap);
                // 查询所有记录
                List<ExperimentTimerEntity> experimentTimerEntityList = this.getPeriodsTimerList(experimentInstanceId);
                // 过滤每期暂停次数最大的
                List<ExperimentTimerEntity> experimentTimerEntities = new ArrayList<>();
                experimentTimerEntityList.stream()
                        .collect(Collectors.groupingBy(ExperimentTimerEntity::getPeriod))
                        .forEach((k, v) -> {
                            ExperimentTimerEntity et = v.stream()
                                    .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                                    .get();
                            experimentTimerEntities.add(et);
                        });
                /**
                 * 暂停中
                 * 如果有暂停则优先处理暂停
                 */
                ExperimentTimerEntity experimentTimerEntity = experimentTimerEntityList.stream()
                        .filter(e -> e.getPaused() == true)
                        .findFirst()
                        .orElse(null);
                if (null != experimentTimerEntity) {
                    // 剩余时间
                    long rs = experimentTimerEntity.getEndTime().getTime() - experimentTimerEntity.getPauseTime().getTime();
                    // 持续时间
                    long ds = experimentTimerEntity.getPeriodDuration() - rs;
                    intervalResponse.setSandRemnantSecond(rs / 1000);
                    intervalResponse.setSandDurationSecond(ds / 1000);
                    intervalResponse.setState(experimentTimerEntity.getState());
                    intervalResponse.setPeriod(experimentTimerEntity.getPeriod());
                    return intervalResponse;
                }

                /**
                 * 进行中（已开始...)
                 * 找出每期暂停次数最大的记录
                 */
                for (int i = 0; i < experimentTimerEntities.size(); i++) {
                    ExperimentTimerEntity et = experimentTimerEntities.get(i);
                    intervalResponse.setModel(et.getModel());
                    intervalResponse.setPeriod(et.getPeriod());
                    intervalResponse.setState(et.getState());
                    intervalResponse.setAppId(et.getAppId());
                    // 间隔期|倒计时
                    if (sct < et.getStartTime().getTime() - et.getPeriodInterval()) {// 实验未开始
                        intervalResponse.setPeriod(null);
                        sb.append(String.format(" EXPTFLOW-PRE period:%s", et.getPeriod()));
                        break;
                    } else if (sct >= et.getStartTime().getTime() - et.getPeriodInterval() && sct < et.getStartTime().getTime()) { // 一期开始倒计时
                        intervalResponse.setCountdownType(0);
                        intervalResponse.setCountdown(et.getStartTime().getTime() - sct);
                        sb.append(String.format(" EXPTFLOW-START period:%s", et.getPeriod()));
                        break;
                    } else if (sct >= et.getStartTime().getTime() && sct <= et.getEndTime().getTime()) {// 期数中
                        // 本期剩余时间 = 暂停推迟后的结束时间 - 当前时间
                        long rs = et.getEndTime().getTime() - sct + 1;
                        long ds = et.getPeriodDuration() - rs;
                        intervalResponse.setSandRemnantSecond(rs / 1000);
                        intervalResponse.setSandDurationSecond(ds / 1000);
                        sb.append(String.format(" EXPTFLOW-RUN period:%s", et.getPeriod()));
                        break;
                    } else if (sct >= et.getEndTime().getTime() && sct <= et.getEndTime().getTime() + et.getPeriodInterval()) {// // 一期结束倒计时
                        intervalResponse.setCountdown(et.getEndTime().getTime() + et.getPeriodInterval() - sct);
                        intervalResponse.setCountdownType(1);
                        sb.append(String.format(" EXPTFLOW-GAP period:%s", et.getPeriod()));
                        // 发布保险报销事件
                        applicationEventPublisher.publishEvent(new IntervalEvent(intervalResponse));
                        break;
                    } else {
                        /* runsix:留着打日志使用 */
                    }
                }

                /**
                 * 已结束
                 */
                ExperimentTimerEntity finshTimer = experimentTimerEntityList.stream()
                        .filter(e -> e.getState() == EnumExperimentState.FINISH.getState())
                        .max(Comparator.comparingInt(ExperimentTimerEntity::getPeriod))
                        .orElse(null);
                if (null != finshTimer) {
                    Long sum = experimentTimerEntities.stream()
                            .map(ExperimentTimerEntity::getPeriodDuration).reduce(Long::sum)
                            .get();
                    intervalResponse.setSandDurationSecond(sum / 1000);
                    intervalResponse.setState(finshTimer.getState());
                    intervalResponse.setPeriod(finshTimer.getPeriod());
                    return intervalResponse;
                }
            }
            // 如果都不为空，则为标准模式
            if (schemeSettingEntity != null && sandSettingEntity != null) {
                intervalResponse.setModel(EnumExperimentMode.STANDARD.getCode());
            }
            return intervalResponse;
        }catch (Exception ex){
            log.error(String.format("EXPTFLOW-QUERY error.",ex.getMessage()));
            sb.append(" error:");
            sb.append(ex.getMessage());
            sb.append("\r\n");
            throw ex;
        }finally {
            sb.append(" rst:");
            sb.append(JSONUtil.toJsonStr(intervalResponse));
            log.info(String.format("EXPTFLOW-QUERY %s.countDown@%s[%s] %s", this.getClass().getName(), LocalDateTime.now(),Thread.currentThread().getName(),sb));
            sb.setLength(0);
        }

    }


    /**
     * 获取当前实验所有期数计时器列表并按期数递增排序
     * ExperimentRestartRequest experimentRestartRequest
     *
     * @param experimentInstanceId
     * @return
     */
    public List<ExperimentTimerEntity> getPeriodsTimerList(String experimentInstanceId) {
        //List<ExperimentTimerEntity> experimentTimerEntities = new ArrayList<>();

        List<ExperimentTimerEntity> experimentTimerEntityList = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .orderByAsc(ExperimentTimerEntity::getPeriod)
                .orderByAsc(ExperimentTimerEntity::getPauseCount)
                .list();
        /**
         * todo 优化时放开
         */
        /*experimentTimerEntityList.stream()
                .collect(Collectors.groupingBy(ExperimentTimerEntity::getPeriod))
                .forEach((k, v) -> {
                    ExperimentTimerEntity et = v.stream()
                            .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                            .get();
                    experimentTimerEntities.add(et);
                });*/
        //return experimentTimerEntities;

        return experimentTimerEntityList;
    }


    /**
     * 获取间隔的开始时间和结束时间
     * ExperimentRestartRequest experimentRestartRequest
     *
     * @param experimentInstanceId
     * @return
     */
    public List<ExperimentTimerEntity> getPeriodTimers(String experimentInstanceId) {
        List<ExperimentTimerEntity> experimentTimerEntities = new ArrayList<>();

        List<ExperimentTimerEntity> experimentTimerEntityList = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .orderByAsc(ExperimentTimerEntity::getPeriod)
                .orderByAsc(ExperimentTimerEntity::getPauseCount)
                .list();
        /**
         * todo 优化时放开
         */
        experimentTimerEntityList.stream()
                .collect(Collectors.groupingBy(ExperimentTimerEntity::getPeriod))
                .forEach((k, v) -> {
                    ExperimentTimerEntity et = v.stream()
                            .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                            .get();
//                    long st = et.getEndTime().getTime() + et.getPeriodInterval();
                    experimentTimerEntities.add(et);
                });

        return experimentTimerEntities;
    }


    /**
     * 根据实验状态获取最后期数的计时器
     *
     * @param experimentInstanceId
     * @return
     */
    public ExperimentTimerEntity getLastPeriods(String experimentInstanceId, EnumExperimentState enumExperimentState) {
        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .ne(ExperimentTimerEntity::getState, enumExperimentState.getState())
                .list();
        ExperimentTimerEntity experimentTimerEntity = list.stream()
                .max(Comparator.comparingInt(ExperimentTimerEntity::getPeriod))
                .get();

        return experimentTimerEntity;
    }

    /**
     * 更新定时器及实验状态（experimentInstance,experimentParticipator）
     *
     * @param updateExperimentTimerEntities
     * @return
     */
    @DSTransactional
    public boolean saveOrUpdateExperimentTimeExperimentState(String experimentInstanceId,
                                                             List<ExperimentTimerEntity> updateExperimentTimerEntities,
                                                             EnumExperimentState enumExperimentState) {
        boolean b = experimentTimerService.saveOrUpdateBatch(updateExperimentTimerEntities);
        if (!b) {
            throw new ExperimentException(" 更新计时器实验状态发生异常！");
        }
        boolean update = experimentInstanceService.lambdaUpdate()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .set(ExperimentInstanceEntity::getState, enumExperimentState.getState())
                .update();
        if (!update) {
            throw new ExperimentException(" 更新实验实例状态发生异常！");
        }
        boolean update1 = experimentParticipatorService.lambdaUpdate()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .set(ExperimentParticipatorEntity::getState, enumExperimentState.getState())
                .update();

        if (!update1) {
            throw new ExperimentException(" 更新实验参与者实验状态发生异常！");
        }
        return b;
    }


    /**
     * 获取当前实验期数信息[每期开始，结束，间隔等]
     *
     * @param appId
     * @param experimentInstanceId
     * @return
     */
    public ExperimentPeriodsResonse getExperimentCurrentPeriods(String appId, String experimentInstanceId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<ExperimentTimerEntity> list = this.getPeriodsTimerList(experimentInstanceId);
        ExperimentTimerEntity experimentTimerEntity = list.stream()
                .filter(e -> currentTimeMillis >= e.getStartTime().getTime() && currentTimeMillis <= e.getEndTime().getTime())
                .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                .orElse(null);

        if (null == experimentTimerEntity) {
            log.error("获取实验期数异常,当前时间不存在对应的实验期数");
            StringBuilder stringBuilder = new StringBuilder();
            list.stream().forEach(k -> {
                stringBuilder.append(k.getPeriod()).append("期开始时间：")
                        .append(DateUtil.date(k.getStartTime()))
                        .append(" ");
            });
            throw new ExperimentException("获取实验期数异常,当前时间不存在对应的实验期数,当前实验期数信息：" + stringBuilder);
        }
        // 获取当前期数
        ExperimentPeriodsResonse experimentPeriodsResonse = new ExperimentPeriodsResonse();
        experimentPeriodsResonse.setCurrentPeriod(experimentTimerEntity.getPeriod());
        experimentPeriodsResonse.setExperimentInstanceId(experimentTimerEntity.getExperimentInstanceId());

        List<ExperimentPeriodsResonse.ExperimentPeriods> experimentPeriods = BeanConvert
                .beanConvert(list, ExperimentPeriodsResonse.ExperimentPeriods.class);
        experimentPeriodsResonse.setExperimentPeriods(experimentPeriods);

        return experimentPeriodsResonse;
    }

    /**
     * 获取实验所有除暂停记录外的每一期的开始时间和结束时间
     *
     * @param experimentInstanceId
     */
    public Map<Integer, ExperimentTimerEntity> getExperimentPeriodsStartAnsEndTime(String experimentInstanceId) {
        Map<Integer, ExperimentTimerEntity> map = new LinkedHashMap<>();
        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .orderByAsc(ExperimentTimerEntity::getPeriod)
                .list();
        Map<Integer, List<ExperimentTimerEntity>> collect = list.stream()
                .collect(Collectors.groupingBy(ExperimentTimerEntity::getPeriod));
        // 找出每期暂停次数最大的为准
        collect.forEach((k, v) -> {
            ExperimentTimerEntity experimentTimerEntity = v.stream()
                    .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                    .get();
            map.put(k, experimentTimerEntity);
        });
        return map;
    }
}