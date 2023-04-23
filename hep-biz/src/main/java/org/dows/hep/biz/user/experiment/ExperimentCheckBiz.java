package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.user.experiment.request.GroupRankingRequest;
import org.dows.hep.api.user.experiment.request.ScoreRankRequest;
import org.dows.hep.api.user.experiment.response.GroupRankingResponse;
import org.dows.hep.api.user.experiment.response.ScoreRankResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:实验:实验查看
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class ExperimentCheckBiz{
    /**
    * @param
    * @return
    * @说明: 查看分数排行
    * @关联表: OperateResult
    * @工时: 5H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<ScoreRankResponse> checkScore(ScoreRankRequest scoreRank ) {
        return new ArrayList<ScoreRankResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 查看排行榜
    * @关联表: OperateResult
    * @工时: 5H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<GroupRankingResponse> checkRanking(GroupRankingRequest groupRanking ) {
        return new ArrayList<GroupRankingResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 查看人物数字档案
    * @关联表: 
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void checkDigtalArchive() {
        
    }
}