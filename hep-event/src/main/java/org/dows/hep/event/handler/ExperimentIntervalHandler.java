package org.dows.hep.event.handler;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.WsMessageResponse;
import org.dows.hep.api.base.indicator.request.RsCalculatePeriodsRequest;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.api.user.experiment.response.IntervalResponse;
import org.dows.hep.biz.base.indicator.RsExperimentCalculateBiz;
import org.dows.hep.biz.calc.CalculatorDispatcher;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireBiz;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.dows.hep.biz.user.person.PersonStatiscBiz;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageBody;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * 实验间隔处理器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentIntervalHandler extends AbstractEventHandler implements EventHandler<IntervalResponse> {
    private final PersonStatiscBiz personStatiscBiz;
    private final ExperimentQuestionnaireBiz experimentQuestionnaireBiz;
    private final CalculatorDispatcher calculatorDispatcher;
    private final ExperimentScoringBiz experimentScoringBiz;
    private final RsExperimentCalculateBiz rsExperimentCalculateBiz;

    @Override
    public void exec(IntervalResponse intervalResponse) throws ExecutionException, InterruptedException {
        String appId = intervalResponse.getAppId();
        String experimentInstanceId = intervalResponse.getExperimentInstanceId();
        Integer period = intervalResponse.getPeriod();
        String experimentGroupId = intervalResponse.getExperimentGroupId();

        // 每期结束后，统一提交知识答题。 （注：需要在算分前执行）
        experimentQuestionnaireBiz.submitQuestionnaireBatch(experimentInstanceId, period);

        // 算分： 健康指数 && 知识考点 && 医疗占比 && 操作（不要了） && 总分计算
        ExperimentScoreCalcRequest exptScoreCalcRequest = ExperimentScoreCalcRequest.builder()
                .experimentInstanceId(experimentInstanceId)
                .experimentGroupId(experimentGroupId)
                .period(period)
                .enumCalcCodes(List.of(
                        EnumCalcCode.hepHealthIndexCalculator,
                        EnumCalcCode.hepKnowledgeCalculator,
                        EnumCalcCode.hepTreatmentPercentCalculator,
                        EnumCalcCode.hepTotalScoreCalculator))
                .build();
        calculatorDispatcher.calc(exptScoreCalcRequest);

        // 落库： 所有计算出得分落在 experiment_scoring 表中
        try {
            /* runsix:期数翻转，指标相关计算 */
            rsExperimentCalculateBiz.experimentReCalculatePeriods(RsCalculatePeriodsRequest
                .builder()
                .appId(appId)
                .experimentId(experimentInstanceId)
                .periods(period)
                .build());
            /* runsix:期数翻转，指标相关计算 */
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

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
}
