package org.dows.hep.biz.user.experiment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @description project descr:实验:实验分数计算
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@Slf4j
@Service
public class ExperimentComputeBiz{

    private final ExperimentScoringBiz experimentScoringBiz;



    /**
    * @param
    * @return
    * @说明: 计算排名
    * @关联表: 
    * @工时: 0H
    * @开发者: 
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void computeRanking() {
//        experimentScoringBiz.
    }




    /**
    * @param
    * @return
    * @说明: 获取排名
    * @关联表: 
    * @工时: 0H
    * @开发者: 
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void getRanks() {
        
    }
}