package org.dows.hep.biz.event.sysevent.dealers;

import cn.hutool.core.date.DateUtil;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.api.user.experiment.response.StartCutdownResponse;
import org.dows.hep.biz.dao.ExperimentParticipatorDao;
import org.dows.hep.biz.dao.ExperimentTimerDao;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.BaseEventDealer;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : wuzl
 * @date : 2023/8/26 8:04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentReadyDealer extends BaseEventDealer {



    private final ExperimentTimerBiz experimentTimerBiz;

    private final ExperimentTimerDao experimentTimerDao;

    private final ExperimentParticipatorDao experimentParticipatorDao;


    @Override
    protected boolean coreDeal(EventDealResult rst, SysEventRow row, SysEventRunStat stat) {
        final String appId=row.getEntity().getAppId();
        final String experimentInstanceId=row.getEntity().getExperimentInstanceId();

        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(ExperimentCacheKey.create(appId,experimentInstanceId),true);
        if(ShareUtil.XObject.anyEmpty(exptColl,()->exptColl.getMode())){
            rst.append("missSetting[%s]",experimentInstanceId);
            return false;
        }

        List<ExperimentTimerEntity> rowsTimer=null;
        if(exptColl.hasSandMode()) {
            Map<Integer, ExperimentTimerEntity> mapTimers = experimentTimerDao.getMapByExperimentId(appId, experimentInstanceId, null,
                    ExperimentTimerEntity::getId,
                    ExperimentTimerEntity::getExperimentTimerId,
                    ExperimentTimerEntity::getStartTime,
                    ExperimentTimerEntity::getEndTime,
                    ExperimentTimerEntity::getPauseTime,
                    ExperimentTimerEntity::getRestartTime,
                    ExperimentTimerEntity::getPauseDuration,
                    ExperimentTimerEntity::getPeriodTimer,
                    ExperimentTimerEntity::getPeriodInterval,
                    ExperimentTimerEntity::getState);
            final Date now = new Date();
            mapTimers.values().forEach(item -> {
                // 暂停开始时间
                long pst = item.getPauseTime().getTime();
                // 暂停时长 = 暂停结束时间 - 暂停开始时间
                long duration = now.getTime() - pst;
                // 重新开始时间[暂停推迟后的开始时间]
                long rs = now.getTime() + item.getPeriodInterval();

                item.setStartTime(DateUtil.date(item.getStartTime().getTime() + duration))
                        .setEndTime(DateUtil.date(item.getEndTime().getTime() + duration))
                        .setPaused(false)
                        .setPauseDuration(duration)
                        .setPeriodTimer(0L)
                        .setRestartTime(DateUtil.date(rs))
                        .setState(EnumExperimentState.ONGOING.getState());
            });
            rowsTimer=mapTimers.values().stream().toList();
            mapTimers.clear();
        }

        // 保存或更新实验计时器
        if(!experimentTimerBiz.saveOrUpdateExperimentTimeExperimentState(experimentInstanceId,rowsTimer, EnumExperimentState.ONGOING)){
            rst.append("failUpdateExptState[%s]",experimentInstanceId);
            return false;
        }
        pushMsg(experimentInstanceId);
        return true;
    }

    private void pushMsg(String experimentInstanceId) {
        // 查询到对应的accountId
        Set<String> accountIds = ShareUtil.XCollection.toSet(experimentParticipatorDao.getByExperimentId(experimentInstanceId,
                ExperimentParticipatorEntity::getAccountId),ExperimentParticipatorEntity::getAccountId);
                new HashSet<>();


        // 通知实验所有小组
        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfosByExperimentId(experimentInstanceId);

        // 过滤数据，只给学生发websocket
        Set<Channel> channels = userInfos.keySet();
        for (Channel channel : channels) {
            if (accountIds.contains(userInfos.get(channel).getAccountName())) {
                StartCutdownResponse startCutdownResponse = new StartCutdownResponse();
                startCutdownResponse.setType(EnumWebSocketType.START_EXPERIMENT_COUNTDOWN);
                //startCutdownResponse.setModelDescr(periods1.getModelDescr());
                startCutdownResponse.setExperimentInstanceId(experimentInstanceId);
                //startCutdownResponse.setPeriodInterval(periods1.getPeriodInterval());
                Response<StartCutdownResponse> ok = Response.ok(startCutdownResponse);
                HepClientManager.sendInfoRetry(channel, MessageCode.MESS_CODE, ok,idGenerator.nextIdStr(),null);
            }
        }
    }

    @Override
    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        if(!exptColl.hasSandMode()){
            return null;
        }
        return List.of(buildEvent(exptColl,0,
                EnumSysEventDealType.EXPERIMENTReady.getCode(),
                EnumSysEventTriggerType.EXPERIMENTReady.getCode()));
    }


}
