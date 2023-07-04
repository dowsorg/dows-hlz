package org.dows.hep.biz.score;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 排行榜计算
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScoreCalc {

    private final ExperimentScoringBiz experimentScoringBiz;


    public void calc(String expeirmentInstanceId, Integer peroid, List<String> calcCodes) {
        //experimentScoringBiz.
    }
}
