package org.dows.hep.biz.intervene;

import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.intervene.response.InterveneCategResponse;
import org.dows.hep.api.intervene.request.SaveInterveneCategRequest;
import org.dows.hep.api.intervene.request.DelInterveneCategRequest;
import org.dows.hep.api.intervene.request.FindSurveyRequest;
import org.dows.hep.api.intervene.response.SurveyResponse;
import org.dows.hep.api.intervene.response.SurveyInfoResponse;
import org.dows.hep.api.intervene.request.DelSurveyRequest;
import org.dows.hep.api.intervene.request.SetSurveyStateRequest;
import org.dows.hep.api.intervene.request.SaveSurveyBasicRequest;
import org.dows.hep.api.intervene.request.SaveSurveyQuestionRequest;
import org.dows.hep.api.intervene.request.SaveSurveyEvalRequest;
import org.dows.hep.api.intervene.request.SaveSurveyReportRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:干预:评估问卷
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
public class SurveyBiz{
    /**
    * @param
    * @return
    * @说明: 获取类别
    * @关联表: survey_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public List<InterveneCategResponse> listSurveyCateg(FindInterveneCategRequest findInterveneCateg ) {
        return new ArrayList<InterveneCategResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 保存类别
    * @关联表: survey_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean saveSurveyCateg(SaveInterveneCategRequest saveInterveneCateg ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除类别
    * @关联表: survey_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean delSurveyCateg(DelInterveneCategRequest delInterveneCateg ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取评估问卷列表
    * @关联表: survey
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public SurveyResponse pageSurvey(FindSurveyRequest findSurvey ) {
        return new SurveyResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取评估问卷详细信息
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public SurveyInfoResponse getSurvey(String surveyId, Integer step ) {
        return new SurveyInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 删除问卷
    * @关联表: survey,survey_eval,survey_report
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean delSurvey(DelSurveyRequest delSurvey ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 启用、禁用问卷
    * @关联表: survey
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean setSurveyState(SetSurveyStateRequest setSurveyState ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 保存问卷基本信息
    * @关联表: survey
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean saveSurveyBasic(SaveSurveyBasicRequest saveSurveyBasic ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 保存问卷题目信息
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean saveSurveyQuestion(SaveSurveyQuestionRequest saveSurveyQuestion ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 保存问卷维度公式
    * @关联表: survey_eval
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean saveSurveyEval(SaveSurveyEvalRequest saveSurveyEval ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 保存问卷报告设置
    * @关联表: survey_report
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean saveSurveyReport(SaveSurveyReportRequest saveSurveyReport ) {
        return Boolean.FALSE;
    }
}