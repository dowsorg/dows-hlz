package org.dows.hep.biz.event.sysevent.dealers;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.base.indicator.request.RsCalculatePeriodsRequest;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.biz.base.indicator.RsExperimentCalculateBiz;
import org.dows.hep.biz.eval.EvalPersonBiz;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.BaseEventDealer;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireBiz;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireScoreBiz;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageBody;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : wuzl
 * @date : 2023/8/23 14:28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PeriodEndDealer extends BaseEventDealer {

    private final ExperimentQuestionnaireBiz experimentQuestionnaireBiz;
    private final ExperimentQuestionnaireScoreBiz experimentQuestionnaireScoreBiz;

    private final RsExperimentCalculateBiz rsExperimentCalculateBiz;

    private final EvalPersonBiz evalPersonBiz;
    @Override
    public EnumSysEventPushType getPushType() {
        return EnumSysEventPushType.ALWAYS;
    }

    @Override
    protected boolean coreDeal(EventDealResult rst, SysEventRow row, SysEventRunStat stat) {
        final ExperimentSysEventEntity event = row.getEntity();
        final String appId = event.getAppId();
        final String experimentInstanceId = event.getExperimentInstanceId();
        final Integer period = event.getPeriods();

        final ExperimentCacheKey exptKey=ExperimentCacheKey.create(appId,experimentInstanceId);
        final ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(exptKey, true);
        this.pushTimeState(rst, exptKey, exptColl, EnumWebSocketType.FLOW_PERIOD_ENDING , row);


        // 每期结束后，统一提交知识答题。 （注：需要在算分前执行）
        experimentQuestionnaireBiz.submitQuestionnaireBatch(experimentInstanceId, period);

        // 算分： 知识考点
        experimentQuestionnaireScoreBiz.calculateExptQuestionnaireScore(experimentInstanceId, period);

        // 指标计算
        if(ConfigExperimentFlow.SWITCH2EvalCache){
            evalPersonBiz.evalPeriodEnd(RsCalculatePeriodsRequest
                    .builder()
                    .appId(appId)
                    .experimentId(experimentInstanceId)
                    .periods(period)
                    .build());
        }else{
            try {
                rsExperimentCalculateBiz.experimentReCalculatePeriods(RsCalculatePeriodsRequest
                        .builder()
                        .appId(appId)
                        .experimentId(experimentInstanceId)
                        .periods(period)
                        .build());

            } catch (Exception ex) {
                rst.append("calcError:%s", ex.getMessage());
                return false;
            }
        }

        this.pushTimeState(rst, exptKey, exptColl, EnumWebSocketType.FLOW_PERIOD_ENDED , row);
        //this.oldPush(appId, experimentInstanceId, period);
        return true;
    }
    private void oldPush(String appId, String experimentInstanceId,Integer period){
        MessageBody messageBody = new MessageBody<>();
        messageBody.setExperimentId(experimentInstanceId);
        messageBody.setPeriod(period.toString());
        messageBody.setAppId(appId);
        messageBody.setAction("calc");
        // 计算完成
        messageBody.setData(true);
        // 通知客户端
        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfosByExperimentId(experimentInstanceId);
        Set<Channel> channels = userInfos.keySet();
        for (Channel channel : channels) {
            HepClientManager.sendInfoRetry(channel, MessageCode.MESS_CODE, Response.ok(messageBody), idGenerator.nextIdStr(), null);
        }
    }

    @Override
    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        if(!exptColl.hasSandMode()){
            return null;
        }
        List<ExperimentSysEventEntity> rst=new ArrayList<>();
        for(int i=1;i<=exptColl.getPeriods();i++){
            rst.add(buildEvent(exptColl,i,
                    EnumSysEventDealType.PERIODEnd,
                    EnumSysEventTriggerType.PERIODEnd));
        }
        return rst;
    }

}
