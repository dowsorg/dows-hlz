package org.dows.hep.event.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentMonitorFollowupCheckRequestRs;
import org.dows.hep.api.enums.EnumNoticeType;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.notify.NoticeContent;
import org.dows.hep.api.notify.message.ExperimentFollowupMessage;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.biz.noticer.FollowupNoticer;
import org.dows.hep.biz.task.ExperimentNoticeTask;
import org.dows.hep.biz.user.experiment.ExperimentOrgBiz;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentIndicatorViewMonitorFollowupPlanRsService;
import org.dows.hep.service.ExperimentSettingService;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 实验检测对方事件处理
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentFollowupHandler extends AbstractEventHandler implements EventHandler<ExperimentMonitorFollowupCheckRequestRs> {

    private final ExperimentIndicatorViewMonitorFollowupPlanRsService experimentIndicatorViewMonitorFollowupPlanRsService;

    private final FollowupNoticer followupNoticer;

    private final ExperimentSettingService experimentSettingService;

    private final ExperimentOrgBiz experimentOrgBiz;


    @Override
    public void exec(ExperimentMonitorFollowupCheckRequestRs experimentMonitorFollowupCheckRequestRs) throws ExecutionException, InterruptedException {

        long ct = System.currentTimeMillis();


        List<ExperimentSettingEntity> experimentSettings = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentMonitorFollowupCheckRequestRs.getExperimentId())
                .list();
        ExperimentSetting.SandSetting sandSetting = null;
        for (ExperimentSettingEntity expSetting : experimentSettings) {
            String configKey = expSetting.getConfigKey();
            if (configKey.equals(ExperimentSetting.SandSetting.class.getName())) {
                sandSetting = JSONUtil.toBean(expSetting.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
                break;
            }
        }

        // 实验中的随访时间间隔比例
        Integer intervalDay = experimentMonitorFollowupCheckRequestRs.getIntervalDay();
        Collection<ExperimentTimerEntity> values = experimentTimerBiz
                .getExperimentPeriodsStartAnsEndTime(experimentMonitorFollowupCheckRequestRs.getExperimentId()).values();

        // 总天数
        int sum = sandSetting.getPeriodMap().values().stream().mapToInt(a -> a.intValue()).sum();
        if (intervalDay > sum) {
            throw new ExperimentException("超出实验时长");
        }

        // 间隔毫秒数
        long intervalMs = intervalDay * 24 * 60 * 60 * 1000;

        // 在当前期数内剩余时间戳
        AtomicLong prts = new AtomicLong(0L);

        // 记录cron表达式
        //StringBuilder cronSb = new StringBuilder();
        List<Date> dates = new ArrayList<>();

        for (ExperimentTimerEntity v : values) {
            // 当前期对应的天数（天）
            Integer pday = sandSetting.getPeriodMap().get(v.getPeriod().toString());
            // 当前期对应的时长(分钟)
            Integer ptime = sandSetting.getDurationMap().get(v.getPeriod().toString());
            // 统一单位，转为毫秒
            BigDecimal pdayms = NumberUtil.mul(pday, 24, 60, 60, 1000);
            BigDecimal ptimems = NumberUtil.mul(ptime, 60, 1000);
            // 得到比例(一秒模拟多少天)
            BigDecimal curRatio = NumberUtil.div(pdayms, ptimems);
            // 得到间隔天数在当前期对应多少秒
            BigDecimal realityMs = NumberUtil.div(intervalMs, curRatio);

            // 找出当前所在期
            if (v.getStartTime().getTime() >= ct && ct <= v.getEndTime().getTime()) {
                // 当前期剩余时间
                long residueTime = v.getEndTime().getTime() - ct;
                extracted(ct, intervalMs, prts, dates, v, realityMs, residueTime);
                continue;
            }
//            if (prts.get() != 0) {
//                BigDecimal mul = NumberUtil.mul(prts.get(), curRatio);
//                long pct = v.getRestartTime().getTime() + mul.longValue();
//                dates.add(new Date(pct));
//                long residueTime = v.getEndTime().getTime() - pct;
//                prts.set(0L);
//                extracted(pct, intervalMs, prts, dates, v, realityMs, residueTime);
//            }
        }

        ExperimentFollowupMessage build = ExperimentFollowupMessage.builder().flag(true).build();
        BeanUtil.copyProperties(experimentMonitorFollowupCheckRequestRs, build);
        NoticeContent noticeContent = NoticeContent.builder()
                .type(EnumNoticeType.PushCurrentAccountNotice)
                .messageCode(MessageCode.MESS_CODE)
                .accountId(Arrays.asList(experimentMonitorFollowupCheckRequestRs.getOperatorId()))
                .payload(build)
                .build();

        ExperimentNoticeTask experimentNoticeTask = new ExperimentNoticeTask(
                experimentMonitorFollowupCheckRequestRs.getExperimentId(),
                experimentMonitorFollowupCheckRequestRs.getExperimentGroupId(),
                experimentMonitorFollowupCheckRequestRs.getPeriods(),
                followupNoticer, noticeContent, experimentTaskScheduleService);
        // 调度
        for (Date date : dates) {
            taskScheduler.schedule(experimentNoticeTask, date);
        }

    }

    private static void extracted(long ct, long intervalMs, AtomicLong prts, List<Date> dates,
                                  ExperimentTimerEntity v, BigDecimal realityMs, long residueTime) {
        if (residueTime > realityMs.longValue()) {
            /**
             * 本期调度开始时间
             */
            long loopCount = residueTime / intervalMs;
            long delayTime = 0L;
            if (loopCount >= 1) {
                for (long l = 0; l < loopCount; l++) {
                    delayTime += ct + realityMs.longValue() * l;
                    dates.add(new Date(delayTime));
                }
            }
//            else {
//                // 剩余时间延到下一期（期数间隔时间不包含在内）,下一期开始的时候需要加上该值
//                prts.set(realityMs.longValue() - (residueTime - delayTime));
//            }
        }
//        else {
//            /**
//             *  下期开始时间 = 随访间隔天数-本期剩余时间+间隔时间，下一期开始的时候需要加上该时长值
//             */
//            prts.set(realityMs.longValue() - residueTime);
//        }
    }
}
