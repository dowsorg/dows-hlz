package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.RsAgeRequest;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.base.indicator.request.RsInitMoneyRequest;
import org.dows.hep.api.base.indicator.request.RsSexRequest;
import org.dows.hep.api.base.indicator.response.GroupAverageHealthPointResponse;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.api.enums.EnumStatus;
import org.dows.hep.api.user.experiment.request.ExperimentIndicatorInstanceRequest;
import org.dows.hep.api.user.experiment.response.EchartsDataResonse;
import org.dows.hep.biz.eval.EvalPersonCache;
import org.dows.hep.biz.eval.EvalPersonOnceHolder;
import org.dows.hep.biz.eval.QueryPersonBiz;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.EchartsUtils;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.entity.ExperimentScoringEntity;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorInstanceRsBiz {
    private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
    private final ExperimentPersonService experimentPersonService;
    private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
    private final ExperimentScoringService experimentScoringService;
    private final ExperimentTimerBiz experimentTimerBiz;
    private final ExperimentGroupService experimentGroupService;

    private final QueryPersonBiz queryPersonBiz;


    public String getHealthPoint(Integer periods, String experimentPersonId) {
        if (ConfigExperimentFlow.SWITCH2EvalCache) {
            return queryPersonBiz.getHealthPoint(periods, experimentPersonId);
        }
        String healthPoint = "1";
        ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = experimentIndicatorInstanceRsService.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
                .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.HEALTH_POINT.getType())
                .one();
        String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = experimentIndicatorValRsService.lambdaQuery()
                .eq(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceId)
                .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                .one();
        if (Objects.nonNull(experimentIndicatorValRsEntity)) {
            healthPoint = experimentIndicatorValRsEntity.getCurrentVal();
        }
        return healthPoint;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean changeMoney(RsChangeMoneyRequest rsChangeMoneyRequest) {
        return saveChangeMoney(getChangeMoney(rsChangeMoneyRequest));
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveChangeMoney(ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity) {
        if (ConfigExperimentFlow.SWITCH2EvalCache) {
            return queryPersonBiz.saveChangeMoney(experimentIndicatorValRsEntity);
        }
        if (null == experimentIndicatorValRsEntity) {
            return false;
        }
        return experimentIndicatorValRsService.saveOrUpdate(experimentIndicatorValRsEntity);
    }

    public ExperimentIndicatorValRsEntity getChangeMoney(RsChangeMoneyRequest rsChangeMoneyRequest) {
        if (ConfigExperimentFlow.SWITCH2EvalCache) {
            return queryPersonBiz.getChangeMoney(rsChangeMoneyRequest);
        }
        String appId = rsChangeMoneyRequest.getAppId();
        String experimentId = rsChangeMoneyRequest.getExperimentId();
        String experimentPersonId = rsChangeMoneyRequest.getExperimentPersonId();
        Integer periods = rsChangeMoneyRequest.getPeriods();
        BigDecimal moneyChange = rsChangeMoneyRequest.getMoneyChange();
        ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = experimentIndicatorInstanceRsService.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
                .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.MONEY.getType())
                .one();
        String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = experimentIndicatorValRsService.lambdaQuery()
                .eq(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceId)
                .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                .one();
        String min = experimentIndicatorValRsEntity.getMin();
        String max = experimentIndicatorValRsEntity.getMax();
        String moneyCurrentVal = experimentIndicatorValRsEntity.getCurrentVal();
        BigDecimal newMoneyCurrentVal = BigDecimal.valueOf(Double.parseDouble(moneyCurrentVal)).add(moneyChange);
        if (rsChangeMoneyRequest.isAssertEnough()) {
            AssertUtil.trueThenThrow(newMoneyCurrentVal.compareTo(BigDecimal.ZERO) < 0)
                    .throwMessage("您的资金不足了");
        }
        if (newMoneyCurrentVal.compareTo(BigDecimal.valueOf(Double.parseDouble(min))) < 0) {
            newMoneyCurrentVal = BigDecimal.valueOf(Double.parseDouble(min));
        } else if (newMoneyCurrentVal.compareTo(BigDecimal.valueOf(Double.parseDouble(max))) > 0) {
            newMoneyCurrentVal = BigDecimal.valueOf(Double.parseDouble(max));
        }
        experimentIndicatorValRsEntity.setCurrentVal(newMoneyCurrentVal.setScale(2, RoundingMode.HALF_UP).toString());
        return experimentIndicatorValRsEntity;
    }

    /**
     * @param
     * @return
     * @说明: 实验体检人次统计
     * @关联表: experiment_indicator_instance、case_indicator_instance、case_indicator_rule、experiment_person
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/7/13 09:31
     */
    public List<EchartsDataResonse> statAgeRate(ExperimentIndicatorInstanceRequest experimentIndicatorInstanceRequest) {
        List<EchartsDataResonse> statList = new ArrayList<>();
        //1、根据实验实例ID、小组ID以及机构ID获取对应的人物列表
        LambdaQueryWrapper<ExperimentPersonEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExperimentPersonEntity::getExperimentInstanceId, experimentIndicatorInstanceRequest.getExperimentInstanceId())
                .eq(ExperimentPersonEntity::getExperimentGroupId, experimentIndicatorInstanceRequest.getExperimentGroupId())
                .eq(ExperimentPersonEntity::getExperimentOrgId, experimentIndicatorInstanceRequest.getExperimentOrgId())
                .eq(ExperimentPersonEntity::getDeleted, false)
                .orderByDesc(ExperimentPersonEntity::getDt);
        List<ExperimentPersonEntity> personEntities = experimentPersonService.list(queryWrapper);
        //2、查询上述人物在实验人物指标表中的指标信息
        //2.1、先获取上述人物的年龄数据总数
        List<String> personIdList = personEntities.stream().map(e -> e.getExperimentPersonId()).collect(Collectors.toList());
        if (personIdList != null && personIdList.size() > 0) {
            List<ExperimentIndicatorInstanceRsEntity> indicatorInstanceRsEntities = experimentIndicatorInstanceRsService.lambdaQuery()
                    .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.AGE.getType())
                    .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, personIdList)
                    .eq(ExperimentIndicatorInstanceRsEntity::getDeleted, false)
                    .list();
            if (indicatorInstanceRsEntities == null || indicatorInstanceRsEntities.size() == 0) {
                //2.2、年龄段为空赋值0
                List<String> ageList = Arrays.asList(new String[]{"0-6岁儿童", "7-17岁少年", "18-40岁青年", "41-59岁中年", "60岁以上老年"});
                statList = EchartsUtils.fillEmptydata(statList, ageList);
            } else {
                //2.3、计算年龄数据总数
                int sum = indicatorInstanceRsEntities.size();
                //2.4、计算每个阶段人数比例
                Integer age1 = 0;
                Integer age2 = 0;
                Integer age3 = 0;
                Integer age4 = 0;
                Integer age5 = 0;
                for (ExperimentIndicatorInstanceRsEntity indicatorInstanceRsEntity : indicatorInstanceRsEntities) {
                    //2.5、判断是否为数字
                    Pattern pattern = Pattern.compile("[0-9]*");
                    Matcher isNum = pattern.matcher(indicatorInstanceRsEntity.getDef());
                    if (isNum.matches()) {
                        //2.6、判断属于某个区间
                        if (EchartsUtils.inNumRange(Integer.parseInt(indicatorInstanceRsEntity.getDef()), "[0,6]")) {
                            age1 += 1;
                        }
                        if (EchartsUtils.inNumRange(Integer.parseInt(indicatorInstanceRsEntity.getDef()), "[7,17]")) {
                            age2 += 1;
                        }
                        if (EchartsUtils.inNumRange(Integer.parseInt(indicatorInstanceRsEntity.getDef()), "[18,40]")) {
                            age3 += 1;
                        }
                        if (EchartsUtils.inNumRange(Integer.parseInt(indicatorInstanceRsEntity.getDef()), "[41,59]")) {
                            age4 += 1;
                        }
                        if (EchartsUtils.inNumRange(Integer.parseInt(indicatorInstanceRsEntity.getDef()), "[60,)")) {
                            age5 += 1;
                        }
                    }
                }
                //2.7、计算比例
                EchartsDataResonse stat1 = new EchartsDataResonse("0-6岁儿童", Long.valueOf(sum), String.format("%.2f", (float) (long) age1 / sum));
                EchartsDataResonse stat2 = new EchartsDataResonse("7-17岁少年", Long.valueOf(sum), String.format("%.2f", (float) (long) age2 / sum));
                EchartsDataResonse stat3 = new EchartsDataResonse("18-40岁青年", Long.valueOf(sum), String.format("%.2f", (float) (long) age3 / sum));
                EchartsDataResonse stat4 = new EchartsDataResonse("41-59岁中年", Long.valueOf(sum), String.format("%.2f", (float) (long) age4 / sum));
                EchartsDataResonse stat5 = new EchartsDataResonse("60岁以上老年", Long.valueOf(sum), String.format("%.2f", (float) (long) age5 / sum));
                statList.add(stat1);
                statList.add(stat2);
                statList.add(stat3);
                statList.add(stat4);
                statList.add(stat5);
                //2.8、保证数据总和一百
                statList = EchartsUtils.sum100(statList);
            }
        }
        return statList;
    }

    /**
     * @param
     * @return
     * @说明: 实验人物性别统计
     * @关联表: experiment_indicator_instance、case_indicator_instance、case_indicator_rule、experiment_person
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/7/13 11:31
     */
    public List<EchartsDataResonse> statGenderRate(ExperimentIndicatorInstanceRequest experimentIndicatorInstanceRequest) {
        List<EchartsDataResonse> statList = new ArrayList<>();
        //1、根据实验实例ID、小组ID以及机构ID获取对应的人物列表
        LambdaQueryWrapper<ExperimentPersonEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExperimentPersonEntity::getExperimentInstanceId, experimentIndicatorInstanceRequest.getExperimentInstanceId())
                .eq(ExperimentPersonEntity::getExperimentGroupId, experimentIndicatorInstanceRequest.getExperimentGroupId())
                .eq(ExperimentPersonEntity::getExperimentOrgId, experimentIndicatorInstanceRequest.getExperimentOrgId())
                .eq(ExperimentPersonEntity::getDeleted, false)
                .orderByDesc(ExperimentPersonEntity::getDt);
        List<ExperimentPersonEntity> personEntities = experimentPersonService.list(queryWrapper);
        //2、查询上述人物在实验人物指标表中的指标信息
        //2.1、先获取上述人物的性别数据总数
        List<String> personIdList = personEntities.stream().map(e -> e.getExperimentPersonId()).collect(Collectors.toList());
        if (personIdList != null && personIdList.size() > 0) {
            List<ExperimentIndicatorInstanceRsEntity> indicatorInstanceRsEntities = experimentIndicatorInstanceRsService.lambdaQuery()
                    .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.SEX.getType())
                    .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, personIdList)
                    .eq(ExperimentIndicatorInstanceRsEntity::getDeleted, false)
                    .list();
            if (indicatorInstanceRsEntities == null || indicatorInstanceRsEntities.size() == 0) {
                //2.2、性别为空赋值0
                List<String> ageList = Arrays.asList(new String[]{"女性", "男性"});
                statList = EchartsUtils.fillEmptydata(statList, ageList);
            } else {
                //2.3、计算性别数据总数
                int sum = indicatorInstanceRsEntities.size();
                //2.4、计算每个阶段人数比例
                Integer gender1 = 0;
                Integer gender2 = 0;
                for (ExperimentIndicatorInstanceRsEntity indicatorInstanceRsEntity : indicatorInstanceRsEntities) {
                    if (indicatorInstanceRsEntity.getDef().equals("女")) {
                        gender1 += 1;
                    }
                    if (indicatorInstanceRsEntity.getDef().equals("男")) {
                        gender2 += 1;
                    }
                }
                EchartsDataResonse stat1 = new EchartsDataResonse("女性", Long.valueOf(sum), String.format("%.2f", (float) (long) gender1 / sum));
                EchartsDataResonse stat2 = new EchartsDataResonse("男性", Long.valueOf(sum), String.format("%.2f", (float) (long) gender2 / sum));
                statList.add(stat1);
                statList.add(stat2);
                //2.5、保证数据总和一百
                statList = EchartsUtils.sum100(statList);
            }
        }
        return statList;
    }

    public String getMoneyDef(String experimentPersonId) {
        ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = experimentIndicatorInstanceRsService.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
                .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.MONEY.getType())
                .one();
        if (Objects.isNull(experimentIndicatorInstanceRsEntity)) {
            return "0";
        } else {
            return experimentIndicatorInstanceRsEntity.getDef();
        }
    }

    public GroupAverageHealthPointResponse groupAverageHealth(String experimentId, String experimentGroupId, Integer periods) {
        if (ConfigExperimentFlow.SWITCH2EvalCache) {
            return queryPersonBiz.groupAverageHealth(experimentId, experimentGroupId, periods);
        }
        Set<String> experimentPersonIdSet = experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentGroupId, experimentGroupId)
                .list()
                .stream()
                .map(ExperimentPersonEntity::getExperimentPersonId)
                .collect(Collectors.toSet());
        if (experimentPersonIdSet.isEmpty()) {
            return GroupAverageHealthPointResponse
                    .builder()
                    .experimentPersonCount(0)
                    .averageHealthPoint("0")
                    .build();
        }

        Set<String> healthPointExperimentIndicatorInstanceIdSet = experimentIndicatorInstanceRsService.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.HEALTH_POINT.getType())
                .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
                .list()
                .stream()
                .map(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId)
                .collect(Collectors.toSet());
        if (healthPointExperimentIndicatorInstanceIdSet.isEmpty()) {
            return GroupAverageHealthPointResponse
                    .builder()
                    .experimentPersonCount(0)
                    .averageHealthPoint("0")
                    .build();
        }
        BigDecimal total = experimentIndicatorValRsService.lambdaQuery()
                .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, healthPointExperimentIndicatorInstanceIdSet)
                .list()
                .stream()
                .map(experimentIndicatorValRsEntity -> {
                    return BigDecimal.valueOf(Double.parseDouble(experimentIndicatorValRsEntity.getCurrentVal()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int experimentPersonCount = healthPointExperimentIndicatorInstanceIdSet.size();
        String averageHealthPoint = total.divide(BigDecimal.valueOf(experimentPersonCount), 2, RoundingMode.HALF_UP).toString();

        ExperimentTimePoint nowPoint = ExperimentSettingCache.Instance().getTimePointByRealTimeSilence(ExperimentCacheKey.create("3", experimentId),
                LocalDateTime.now(), false);
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(nowPoint))
                .throwMessage("未找到实验时间设置");
        //int rank = 0 ;
        AtomicInteger curRank = new AtomicInteger(1);
        /*Map<String, Integer> kExperimentGroupIdVRankMap = new HashMap<>();
        experimentScoringService.lambdaQuery()
                .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentId)
                .eq(ExperimentScoringEntity::getPeriods, nowPoint.getGameState()==EnumExperimentState.FINISH? periods: (periods - 1))
                .orderByDesc(ExperimentScoringEntity::getId,ExperimentScoringEntity::getTotalScore)
                .list()
                .forEach(experimentScoringEntity -> {
                    kExperimentGroupIdVRankMap.put(experimentScoringEntity.getExperimentGroupId(), curRank.getAndIncrement());
                });
        if (Objects.nonNull(kExperimentGroupIdVRankMap.get(experimentGroupId))) {
            rank = kExperimentGroupIdVRankMap.get(experimentGroupId);
        }*/
        experimentScoringService.lambdaQuery()
                .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentId)
                .eq(ExperimentScoringEntity::getPeriods, nowPoint.getGameState() == EnumExperimentState.FINISH ? periods : (periods - 1))
                .orderByDesc(ExperimentScoringEntity::getTotalScore, ExperimentScoringEntity::getId)
                .oneOpt()
                .ifPresent(i -> curRank.set(i.getRankNo()));
        return GroupAverageHealthPointResponse
                .builder()
                .experimentPersonCount(experimentPersonCount)
                .averageHealthPoint(averageHealthPoint)
                .rank(curRank.get())
                .build();
    }

    public Map<String, String> getInitMoneyByPeriods(RsInitMoneyRequest rsInitMoneyRequest) {
        if (ConfigExperimentFlow.SWITCH2EvalCache) {
            return queryPersonBiz.getSysIndicatorVals(rsInitMoneyRequest.getExperimentPersonIdSet(), EnumIndicatorType.MONEY, true);
        }
        Integer periods = rsInitMoneyRequest.getPeriods();
        Set<String> experimentPersonIdSet = rsInitMoneyRequest.getExperimentPersonIdSet();
        if (Objects.isNull(experimentPersonIdSet) || experimentPersonIdSet.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, String> kExperimentPersonIdVInitMoneyMap = new HashMap<>();

        Map<String, String> kExperimentPersonIdVMoneyExperimentIndicatorInstanceIdMap = new HashMap<>();
        experimentIndicatorInstanceRsService.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.MONEY.getType())
                .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
                .list()
                .forEach(experimentIndicatorInstanceRsEntity -> {
                    kExperimentPersonIdVMoneyExperimentIndicatorInstanceIdMap.put(experimentIndicatorInstanceRsEntity.getExperimentPersonId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
                });
        if (kExperimentPersonIdVMoneyExperimentIndicatorInstanceIdMap.isEmpty()) {
            return new HashMap<>();
        }
        Set<String> moneyExperimentIndicatorInstanceIdSet = new HashSet<>(kExperimentPersonIdVMoneyExperimentIndicatorInstanceIdMap.values());

        Map<String, String> kMoneyExperimentIndicatorInstanceIdVInitValMap = experimentIndicatorValRsService.lambdaQuery()
                .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, moneyExperimentIndicatorInstanceIdSet)
                .list()
                .stream()
                .collect(Collectors.toMap(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, ExperimentIndicatorValRsEntity::getInitVal));

        kExperimentPersonIdVMoneyExperimentIndicatorInstanceIdMap.forEach((experimentPersonId, moneyExperimentIndicatorInstanceId) -> {
            String initVal = kMoneyExperimentIndicatorInstanceIdVInitValMap.get(moneyExperimentIndicatorInstanceId);
            if (StringUtils.isNotBlank(initVal)) {
                kExperimentPersonIdVInitMoneyMap.put(experimentPersonId, initVal);
            }
        });
        return kExperimentPersonIdVInitMoneyMap;
    }

    public Map<String, String> getSexByPeriods(RsSexRequest rsSexRequest) {
        if (ConfigExperimentFlow.SWITCH2EvalCache) {
            return queryPersonBiz.getSysIndicatorVals(rsSexRequest.getExperimentPersonIdSet(), EnumIndicatorType.SEX, false);
        }
        /* runsix:result */
        Map<String, String> kExperimentPersonIdVSexMap = new HashMap<>();
        /* runsix:param */
        Integer periods = rsSexRequest.getPeriods();
        Set<String> experimentPersonIdSet = rsSexRequest.getExperimentPersonIdSet();
        if (Objects.isNull(experimentPersonIdSet) || experimentPersonIdSet.isEmpty()) {
            return kExperimentPersonIdVSexMap;
        }

        Map<String, String> kExperimentPersonIdVSexIdMap = new HashMap<>();
        experimentIndicatorInstanceRsService.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.SEX.getType())
                .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
                .list()
                .forEach(experimentIndicatorInstanceRsEntity -> {
                    kExperimentPersonIdVSexIdMap.put(experimentIndicatorInstanceRsEntity.getExperimentPersonId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
                });
        if (kExperimentPersonIdVSexIdMap.isEmpty()) {
            return kExperimentPersonIdVSexIdMap;
        }

        Set<String> sexIdSet = new HashSet<>(kExperimentPersonIdVSexIdMap.values());
        Map<String, String> kSexIdMapVValMap = new HashMap<>();
        experimentIndicatorValRsService.lambdaQuery()
                .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, sexIdSet)
                .list()
                .forEach(experimentIndicatorValRsEntity -> {
                    String indicatorInstanceId = experimentIndicatorValRsEntity.getIndicatorInstanceId();
                    String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
                    kSexIdMapVValMap.put(indicatorInstanceId, currentVal);
                });
        kExperimentPersonIdVSexIdMap.forEach((experimentPersonId, sexId) -> {
            String val = kSexIdMapVValMap.get(sexId);
            if (StringUtils.isNotBlank(val)) {
                kExperimentPersonIdVSexMap.put(experimentPersonId, val);
            }
        });

        return kExperimentPersonIdVSexMap;
    }

    public Map<String, String> getAgeByPeriods(RsAgeRequest rsAgeRequest) {
        if (ConfigExperimentFlow.SWITCH2EvalCache) {
            return queryPersonBiz.getSysIndicatorVals(rsAgeRequest.getExperimentPersonIdSet(), EnumIndicatorType.AGE, false);
        }
        /* runsix:result */
        Map<String, String> kExperimentPersonIdVAgeMap = new HashMap<>();
        /* runsix:param */
        Integer periods = rsAgeRequest.getPeriods();
        Set<String> experimentPersonIdSet = rsAgeRequest.getExperimentPersonIdSet();
        if (Objects.isNull(experimentPersonIdSet) || experimentPersonIdSet.isEmpty()) {
            return kExperimentPersonIdVAgeMap;
        }

        Map<String, String> kExperimentPersonIdVAgeIdMap = new HashMap<>();
        experimentIndicatorInstanceRsService.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.AGE.getType())
                .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
                .list()
                .forEach(experimentIndicatorInstanceRsEntity -> {
                    kExperimentPersonIdVAgeIdMap.put(experimentIndicatorInstanceRsEntity.getExperimentPersonId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
                });
        if (kExperimentPersonIdVAgeIdMap.isEmpty()) {
            return kExperimentPersonIdVAgeIdMap;
        }

        Set<String> ageIdSet = new HashSet<>(kExperimentPersonIdVAgeIdMap.values());
        Map<String, String> kAgeIdMapVValMap = new HashMap<>();
        experimentIndicatorValRsService.lambdaQuery()
                .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, ageIdSet)
                .list()
                .forEach(experimentIndicatorValRsEntity -> {
                    String indicatorInstanceId = experimentIndicatorValRsEntity.getIndicatorInstanceId();
                    String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
                    kAgeIdMapVValMap.put(indicatorInstanceId, currentVal);
                });
        kExperimentPersonIdVAgeIdMap.forEach((experimentPersonId, sexId) -> {
            String val = kAgeIdMapVValMap.get(sexId);
            if (StringUtils.isNotBlank(val)) {
                kExperimentPersonIdVAgeMap.put(experimentPersonId, val);
            }
        });

        return kExperimentPersonIdVAgeMap;
    }

    public Map<String, List<String>> getCoreByPeriodsAndExperimentPersonIdList(Integer periods, List<String> experimentPersonIdList) {
        Map<String, List<String>> resultMap = new HashMap<>();

        /* runsix:校验 */
        if (Objects.isNull(periods) || Objects.isNull(experimentPersonIdList) || experimentPersonIdList.isEmpty()) {
            return resultMap;
        }

        /* runsix:1.获取所有人所有核心指标 */
        Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
        Map<String, List<ExperimentIndicatorInstanceRsEntity>> kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap = new HashMap<>();
        experimentIndicatorInstanceRsService.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getCore, EnumStatus.ENABLE.getCode())
                .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdList)
                .list()
                .forEach(experimentIndicatorInstanceRsEntity -> {
                    String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
                    experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceId);

                    String experimentPersonId = experimentIndicatorInstanceRsEntity.getExperimentPersonId();
                    List<ExperimentIndicatorInstanceRsEntity> experimentIndicatorInstanceRsEntityList = kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap.get(experimentPersonId);
                    if (Objects.isNull(experimentIndicatorInstanceRsEntityList)) {
                        experimentIndicatorInstanceRsEntityList = new ArrayList<>();
                    }
                    experimentIndicatorInstanceRsEntityList.add(experimentIndicatorInstanceRsEntity);
                    kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap.put(experimentPersonId, experimentIndicatorInstanceRsEntityList);
                });

        /* runsix:校验 */
        if (experimentIndicatorInstanceIdSet.isEmpty()) {
            return resultMap;
        }

        /* runsix:2.获取所有核心指标的值 */
        Map<String, String> kExperimentIndicatorInstanceIdVCurrentValMap = new HashMap<>();
        if (!ConfigExperimentFlow.SWITCH2EvalCache) {
            experimentIndicatorValRsService.lambdaQuery()
                    .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                    .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
                    .list()
                    .forEach(experimentIndicatorValRsEntity -> {
                        kExperimentIndicatorInstanceIdVCurrentValMap
                                .put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity.getCurrentVal());
                    });
        }



        /* runsix:3.组合数据 */
        kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap.forEach((experimentPersonId, experimentIndicatorInstanceRsEntityList) -> {
            EvalPersonOnceHolder evalHolder = null;
            if (ConfigExperimentFlow.SWITCH2EvalCache) {
                evalHolder = EvalPersonCache.Instance().getCurHolder(experimentPersonId);
            }
            for (ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity : experimentIndicatorInstanceRsEntityList) {
                List<String> resultList = resultMap.get(experimentPersonId);
                if (Objects.isNull(resultList)) {
                    resultList = new ArrayList<>();
                }

                String indicatorName = experimentIndicatorInstanceRsEntity.getIndicatorName();
                String unit = experimentIndicatorInstanceRsEntity.getUnit();
                String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
                String currentVal;
                if (ConfigExperimentFlow.SWITCH2EvalCache) {
                    currentVal = Optional.ofNullable(evalHolder.getIndicator(experimentIndicatorInstanceId))
                            .map(EvalIndicatorValues::getCurVal)
                            .orElse("");
                } else {
                    currentVal = kExperimentIndicatorInstanceIdVCurrentValMap.get(experimentIndicatorInstanceId);
                }
                /* runsix:理论上每个指标都有默认值，如果存在数据错误，就丢掉 */
                if (StringUtils.isBlank(currentVal)) {
                    return;
                }
                resultList.add(RsUtilBiz.getCoreString(indicatorName, currentVal, unit));

                resultMap.put(experimentPersonId, resultList);
            }

        });

        return resultMap;
    }
}
