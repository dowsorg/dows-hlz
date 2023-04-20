package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.GroupRankingRequest;
import org.dows.hep.api.user.experiment.request.ScoreRankRequest;
import org.dows.hep.api.user.experiment.response.GroupRankingResponse;
import org.dows.hep.api.user.experiment.response.ScoreRankResponse;
import org.dows.hep.biz.user.experiment.ExperimentCheckBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @description project descr:实验:实验查看
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验查看", description = "实验查看")
public class ExperimentCheckRest {
    private final ExperimentCheckBiz experimentCheckBiz;

    /**
    * 查看分数排行
    * @param
    * @return
    */
    @Operation(summary = "查看分数排行")
    @GetMapping("v1/userExperiment/experimentCheck/checkScore")
    public List<ScoreRankResponse> checkScore(@Validated ScoreRankRequest scoreRank) {
        return experimentCheckBiz.checkScore(scoreRank);
    }

    /**
    * 查看排行榜
    * @param
    * @return
    */
    @Operation(summary = "查看排行榜")
    @GetMapping("v1/userExperiment/experimentCheck/checkRanking")
    public List<GroupRankingResponse> checkRanking(@Validated GroupRankingRequest groupRanking) {
        return experimentCheckBiz.checkRanking(groupRanking);
    }

    /**
    * 查看人物数字档案
    * @param
    * @return
    */
    @Operation(summary = "查看人物数字档案")
    @GetMapping("v1/userExperiment/experimentCheck/checkDigtalArchive")
    public void checkDigtalArchive() {
        experimentCheckBiz.checkDigtalArchive();
    }


}