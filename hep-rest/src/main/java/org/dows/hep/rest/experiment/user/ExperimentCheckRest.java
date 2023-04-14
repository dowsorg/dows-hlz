package org.dows.hep.rest.experiment.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.user.request.ScoreRankRequest;
import org.dows.hep.api.experiment.user.response.ScoreRankResponse;
import org.dows.hep.api.experiment.user.request.GroupRankingRequest;
import org.dows.hep.api.experiment.user.response.GroupRankingResponse;
import org.dows.hep.biz.experiment.user.ExperimentCheckBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:实验:实验查看
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "实验查看")
public class ExperimentCheckRest {
    private final ExperimentCheckBiz experimentCheckBiz;

    /**
    * 查看分数排行
    * @param
    * @return
    */
    @ApiOperation("查看分数排行")
    @GetMapping("v1/experimentUser/experimentCheck/checkScore")
    public List<ScoreRankResponse> checkScore(@Validated ScoreRankRequest scoreRank) {
        return experimentCheckBiz.checkScore(scoreRank);
    }

    /**
    * 查看排行榜
    * @param
    * @return
    */
    @ApiOperation("查看排行榜")
    @GetMapping("v1/experimentUser/experimentCheck/checkRanking")
    public List<GroupRankingResponse> checkRanking(@Validated GroupRankingRequest groupRanking) {
        return experimentCheckBiz.checkRanking(groupRanking);
    }

    /**
    * 查看人物数字档案
    * @param
    * @return
    */
    @ApiOperation("查看人物数字档案")
    @GetMapping("v1/experimentUser/experimentCheck/checkDigtalArchive")
    public void checkDigtalArchive() {
        experimentCheckBiz.checkDigtalArchive();
    }


}