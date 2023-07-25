package org.dows.hep.biz.user.experiment;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.enums.EnumExperimentMode;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.request.ExperimentPersonRequest;
import org.dows.hep.api.user.experiment.response.CountDownResponse;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.biz.user.person.PersonStatiscBiz;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentSettingService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.stereotype.Service;

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
    private final PersonStatiscBiz personStatiscBiz;


    /**
     * 获取当前实验期数信息[每期开始，结束，间隔等]
     *
     * @param experimentInstanceId
     * @return
     */
    public CountDownResponse userCountdown(String experimentInstanceId) {
        long ct = System.currentTimeMillis();
        // 获取当前期数
        CountDownResponse countDownResponse = new CountDownResponse();
        List<ExperimentSettingEntity> experimentSettingEntities = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .list();
        ExperimentSettingEntity sandSettingModel = experimentSettingEntities.stream()
                .filter(e -> e.getConfigKey().equals(ExperimentSetting.SandSetting.class.getName()))
                .findFirst()
                .orElse(null);
        if (sandSettingModel != null) {
            ExperimentSetting.SandSetting sandSetting =
                    JSONUtil.toBean(sandSettingModel.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
            // 每期持续时长
            countDownResponse.setDurationMap(sandSetting.getDurationMap());
            // 期数
            countDownResponse.setPeriodMap(sandSetting.getPeriodMap());
        }

        List<ExperimentTimerEntity> list = this.getPeriodsTimerList(experimentInstanceId);
        // 优先处理暂停
        ExperimentTimerEntity experimentTimerEntity = list.stream()
                .filter(e -> e.getPaused() == true)
                .findFirst()
                .orElse(null);
        if (null != experimentTimerEntity) {
            Long second = (experimentTimerEntity.getPauseTime().getTime()
                    - experimentTimerEntity.getStartTime().getTime()
                    + experimentTimerEntity.getPeriodInterval()) / 1000;
            countDownResponse.setSandDurationSecond(second);
            // 当前期时长
            Integer duration = countDownResponse.getDurationMap().get(experimentTimerEntity.getPeriod().toString());
            // 设置剩余时间
            countDownResponse.setSandRemnantSecond(duration * 60 - second);
            //countDownResponse.setSandDuration(Double.valueOf(second));
            countDownResponse.setState(experimentTimerEntity.getState());
            countDownResponse.setModel(experimentTimerEntity.getModel());
            countDownResponse.setPeriod(experimentTimerEntity.getPeriod());
            countDownResponse.setExperimentInstanceId(experimentTimerEntity.getExperimentInstanceId());
            return countDownResponse;
        }

        for (int i = 0; i < list.size(); i++) {
            ExperimentTimerEntity pre = list.get(i);
            ExperimentTimerEntity next;
            // 最后一期
            if (i == list.size() - 1) {
                // 本期持续时间 = 当前时间-本期开始时间-暂停持续时间
                // long ds = sct - v.getStartTime() - v.getDuration();
                countDownResponse.setCountdown(pre.getStartTime().getTime() - ct);
                //countDownResponse.setSandDuration(Double.valueOf(pre.getEndTime() - ct));
                //countDownResponse.setSandDurationSecond((pre.getEndTime() - ct) / 1000);
                countDownResponse.setSandRemnantSecond((pre.getEndTime().getTime() - ct - pre.getPeriodInterval()) / 1000);
                countDownResponse.setModel(pre.getModel());
                countDownResponse.setPeriod(pre.getPeriod());
                countDownResponse.setState(pre.getState());
                break;
            }
            next = list.get(i + 1);
            // 两期之间
            if (ct >= pre.getEndTime().getTime() && ct < next.getStartTime().getTime()) {
                ct = next.getStartTime().getTime() - ct;
                countDownResponse.setCountdown(ct);
                // todo 兜底计算，在两期之间计算上一期数据,异步
                break;
            } else if (ct <= pre.getStartTime().getTime()) { // 小组分配结束
                countDownResponse.setCountdown(pre.getStartTime().getTime() - ct);
                countDownResponse.setModel(pre.getModel());
                countDownResponse.setPeriod(pre.getPeriod());
                countDownResponse.setState(pre.getState());
                break;
            } else if (ct >= pre.getStartTime().getTime() && ct <= pre.getEndTime().getTime()) { //  开始之后
                //countDownResponse.setSandDuration(Double.valueOf(pre.getEndTime().getTime() - ct));
                // 本期持续时间 = 当前时间-本期开始时间-暂停持续时间
                //long ds = sct - pre.getStartTime() - pre.getDuration();
                countDownResponse.setSandRemnantSecond(pre.getEndTime().getTime() - ct - pre.getPeriodInterval());
                //countDownResponse.setSandDurationSecond(Double.valueOf(pre.getEndTime() - ct));
                countDownResponse.setModel(pre.getModel());
                countDownResponse.setPeriod(pre.getPeriod());
                countDownResponse.setState(pre.getState());
                break;
            }
        }
        return countDownResponse;
    }


    private Map<String, ExperimentSettingEntity> getSettingByKey(String experimentInstanceId) {
        Map<String, ExperimentSettingEntity> map = new HashMap<>();
        List<ExperimentSettingEntity> list = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .list();
        ExperimentSettingEntity experimentSettingEntity1 = list.stream()
                .filter(e -> e.getConfigKey().equals(ExperimentSetting.SchemeSetting.class.getName()))
                .findFirst()
                .orElse(null);

        ExperimentSettingEntity experimentSettingEntity2 = list.stream()
                .filter(e -> e.getConfigKey().equals(ExperimentSetting.SandSetting.class.getName()))
                .findFirst()
                .orElse(null);
        return map;
    }

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
    public CountDownResponse countdown(String experimentInstanceId) {
        Long sct = System.currentTimeMillis();
        CountDownResponse countDownResponse = new CountDownResponse();
        List<ExperimentSettingEntity> list = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .list();

        if (list.size() == 0) {
            throw new ExperimentException("实验ID对应的实验不存在！");
        }

        /**
         * 方案设计模式
         */
        ExperimentSettingEntity schemeSettingEntity = list.stream()
                .filter(e -> e.getConfigKey().equals(ExperimentSetting.SchemeSetting.class.getName()))
                .findFirst()
                .orElse(null);
        if (schemeSettingEntity != null) {
            ExperimentSetting.SchemeSetting schemeSetting =
                    JSONUtil.toBean(schemeSettingEntity.getConfigJsonVals(), ExperimentSetting.SchemeSetting.class);
            if (sct > schemeSetting.getSchemeEndTime().getTime()) {
                countDownResponse.setSchemeTotalTime(0L);
            } else {
                // 方案设计倒计时
                Long schemeTime = schemeSetting.getSchemeEndTime().getTime() - System.currentTimeMillis();
                countDownResponse.setSchemeTotalTime(schemeTime);
            }
            countDownResponse.setModel(EnumExperimentMode.SCHEME.getCode());
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
            countDownResponse.setExperimentInstanceId(experimentInstanceId);
            countDownResponse.setSandTotalTime(Long.valueOf(totalDay));
            countDownResponse.setSandTimeUnit("天");
            countDownResponse.setModel(EnumExperimentMode.SAND.getCode());
            countDownResponse.setDurationMap(durationMap);
            countDownResponse.setMockRateMap(mockRateMap);
            countDownResponse.setPeriodMap(periodMap);
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
                countDownResponse.setSandRemnantSecond(rs / 1000);
                countDownResponse.setSandDurationSecond(ds / 1000);
                countDownResponse.setState(experimentTimerEntity.getState());
                countDownResponse.setPeriod(experimentTimerEntity.getPeriod());
                return countDownResponse;
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
                countDownResponse.setSandDurationSecond(sum / 1000);
                countDownResponse.setState(finshTimer.getState());
                countDownResponse.setPeriod(finshTimer.getPeriod());
                return countDownResponse;
            }
            /**
             * 进行中
             * 找出每期暂停次数最大的记录
             */
            for (int i = 0; i < experimentTimerEntities.size(); i++) {
                ExperimentTimerEntity et = experimentTimerEntities.get(i);
                // 间隔期|倒计时
                if (sct < et.getStartTime().getTime()) {
                    countDownResponse.setCountdown(et.getStartTime().getTime() - sct);
                    countDownResponse.setModel(et.getModel());
                    countDownResponse.setPeriod(et.getPeriod());
                    countDownResponse.setState(et.getState());
                    if(et.getPeriod() >1){
                        personStatiscBiz.refundFunds(ExperimentPersonRequest.builder()
                                                     .experimentInstanceId(experimentInstanceId)
                                                     .appId("3")
                                                     .periods(et.getPeriod() - 1)
                                                     .build());
                    }
                    break;
                } else if (sct >= et.getStartTime().getTime() && sct <= et.getEndTime().getTime()) {// 期数中
                    // 本期剩余时间 = 暂停推迟后的结束时间 - 当前时间
                    long rs = et.getEndTime().getTime() - sct + 1;
                    long ds = et.getPeriodDuration() - rs;
                    countDownResponse.setSandRemnantSecond(rs / 1000);
                    countDownResponse.setSandDurationSecond(ds / 1000);
                    countDownResponse.setState(et.getState());
                    countDownResponse.setPeriod(et.getPeriod());
                    break;
                }
            }
        }
        // 如果都不为空，则为标准模式
        if (schemeSettingEntity != null && sandSettingEntity != null) {
            countDownResponse.setModel(EnumExperimentMode.STANDARD.getCode());
        }
        return countDownResponse;
    }


    /**
     * 获取当前实验所有期数计时器列表并按期数递增排序
     * ExperimentRestartRequest experimentRestartRequest
     *
     * @param experimentInstanceId
     * @return
     */
    public List<ExperimentTimerEntity> getPeriodsTimerList(String experimentInstanceId) {
        List<ExperimentTimerEntity> experimentTimerEntityList = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .orderByAsc(ExperimentTimerEntity::getPeriod)
                .orderByAsc(ExperimentTimerEntity::getPauseCount)
                .list();
        return experimentTimerEntityList;
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

        List<ExperimentTimerEntity> list = this.getPeriodsTimerList(experimentInstanceId);
        long currentTimeMillis = System.currentTimeMillis();
        ExperimentTimerEntity experimentTimerEntity = list.stream()
                .filter(e -> e.getStartTime().getTime() <= currentTimeMillis && currentTimeMillis <= e.getEndTime().getTime())
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

    /**
     * 获取当前实验计时器
     *
     * @param experimentInstanceId
     */
    public ExperimentTimerEntity getCurrentExperimentTimer(String experimentInstanceId, Long currentTimestamp) {
        return getExperimentPeriodsStartAnsEndTime(experimentInstanceId).values().stream()
                .filter(e -> e.getStartTime().getTime() >= currentTimestamp && currentTimestamp <= e.getEndTime().getTime())
                .findFirst()
                .orElse(null);
    }


}