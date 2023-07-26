package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.RsCalculatePeriodsRequest;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.user.experiment.request.ExperimentPersonRequest;
import org.dows.hep.api.user.experiment.response.IntervalResponse;
import org.dows.hep.biz.base.indicator.RsExperimentCalculateBiz;
import org.dows.hep.biz.calc.ExperimentScoreCalculator;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireBiz;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.dows.hep.biz.user.person.PersonStatiscBiz;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private final ExperimentScoreCalculator experimentScoreCalculator;
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
        experimentScoreCalculator.calc(exptScoreCalcRequest);

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
    }
}
