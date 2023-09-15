package org.dows.hep.biz.eval;

import org.dows.hep.api.base.indicator.response.ExperimentGraphRankResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : wuzl
 * @date : 2023/9/13 19:20
 */
@Component
public class EvalScoreRankBiz {

    //TODO 替换原写入排行榜方法 ExperimentScoringBiz.saveOrUpd
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpd(String experimentInstanceId, Integer periods){

    }

    //TODO 替换原读取排行榜方法 ExperimentScoringBiz.getGraphRank,直接读取Rank排名字段输出
    public ExperimentGraphRankResponse getGraphRank(String appId, String experimentId, Integer period){

        return null;
    }
}
