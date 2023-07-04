package org.dows.hep.biz.user.experiment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 实验计分BIZ
 * todo
 * post/healthIndexScoring/健康指数分数计算
 * post/knowledgeScoring/知识考点分数得分
 * post/treatmentPercentScoring/医疗占比得分
 * post/operateRightScoring/操作准确度得分
 * post//竞争性得分
 * post/totalScoring/总分
 * 此处先计算分数，独立的功能点，供其他bean调用，并将生成的数据保存，用于排行和出报告
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentScoringBiz {


    private final ExperimentSettingBiz experimentSettingBiz;


    /**
     * 健康指数分数计算
     */
/*    public void hepHealthIndexScoring(String experimentInstanceId){
        experimentSettingBiz.getCalcRule(experimentInstanceId);
        //

expeirimentScoreBiz.save();
    }

    *//**
     * 知识考点分数得分
     *//*

    public void hepKnowledgeScoring(){
        experimentSettingBiz.getCalcRule(experimentInstanceId);
        //todo logic clac

    }

    *//**
     * 医疗占比得分
     *//*
    public void hepTreatmentPercentScoring(){
        experimentSettingBiz.getCalcRule(experimentInstanceId);
    }

    *//**
     * 操作准确度得分
     *//*
    public void hepOperateRightScoring(){
        experimentSettingBiz.getCalcRule(experimentInstanceId);

    }*/

    /**
     * 总分
     */
    public void hepTotalScoring(){

    }


}
