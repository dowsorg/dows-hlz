package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:实验:机构操作-判断指标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class ExperimentOrgJudgeBiz{
    /**
    * @param
    * @return
    * @说明: 健康问题+健康指导：获取问题列表（含分类）
    * @关联表: indicator_judge_health_problem,indicator_judge_health_guidance,OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<OrgJudgeItemsResponse> listOrgJudgeItems(FindOrgJudgeItemsRequest findOrgJudgeItems ) {
        return new ArrayList<OrgJudgeItemsResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 疾病问题：获取检查类别+项目
    * @关联表: indicator_judge_disease_problem，OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<OrgJudgeCategResponse> listOrgJudgeCategs(FindOrgJudgeCategsRequest findOrgJudgeCategs ) {
        return new ArrayList<OrgJudgeCategResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 健康问题+健康指导+疾病问题：获取最新保存列表
    * @关联表: indicator_judge_health_problem,indicator_judge_health_guidance,indicator_judge_disease_problem，OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<OrgJudgedItemsResponse> listOrgJudgedItems(FindOrgJudgedItemsRequest findOrgJudgedItems ) {
        return new ArrayList<OrgJudgedItemsResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 健康问题+健康指导+疾病问题：保存
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SaveOrgJudgeResponse saveOrgJudge(SaveOrgJudgeRequest saveOrgJudge ) {
        return new SaveOrgJudgeResponse();
    }
    /**
    * @param
    * @return
    * @说明: 健管目标：获取健管目标列表
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgJudgeGoalsResponse listOrgJudgeGoals(FindOrgJudgeGoalsRequest findOrgJudgeGoals ) {
        return new OrgJudgeGoalsResponse();
    }
    /**
    * @param
    * @return
    * @说明: 健管目标：保存，包含是否购买保险
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SaveOrgJudgeGoalsResponse saveOrgJudgeGoals(SaveOrgJudgeGoalsRequest saveOrgJudgeGoals ) {
        return new SaveOrgJudgeGoalsResponse();
    }
}