package org.dows.hep.biz.experiment.user;

import org.dows.framework.api.Response;
import org.dows.hep.api.experiment.user.request.ScoreRankRequest;
import org.dows.hep.api.experiment.user.response.ScoreRankResponse;
import org.dows.hep.api.experiment.user.request.GroupRankingRequest;
import org.dows.hep.api.experiment.user.response.GroupRankingResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:实验:实验查看
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
public class ExperimentCheckBiz{
    /**
    * @param
    * @return
    * @说明: 查看分数排行
    * @关联表: OperateResult
    * @工时: 5H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
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
    * @创建时间: 2023年4月14日 上午10:19:59
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
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public void checkDigtalArchive() {
        
    }
}