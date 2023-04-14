package org.dows.hep.rest.intervene;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
import org.dows.hep.biz.intervene.SurveyBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:评估问卷
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "评估问卷")
public class SurveyRest {
    private final SurveyBiz surveyBiz;

    /**
    * 获取类别
    * @param
    * @return
    */
    @ApiOperation("获取类别")
    @PostMapping("v1/intervene/survey/listSurveyCateg")
    public List<InterveneCategResponse> listSurveyCateg(@RequestBody @Validated FindInterveneCategRequest findInterveneCateg ) {
        return surveyBiz.listSurveyCateg(findInterveneCateg);
    }

    /**
    * 保存类别
    * @param
    * @return
    */
    @ApiOperation("保存类别")
    @PostMapping("v1/intervene/survey/saveSurveyCateg")
    public Boolean saveSurveyCateg(@RequestBody @Validated SaveInterveneCategRequest saveInterveneCateg ) {
        return surveyBiz.saveSurveyCateg(saveInterveneCateg);
    }

    /**
    * 删除类别
    * @param
    * @return
    */
    @ApiOperation("删除类别")
    @DeleteMapping("v1/intervene/survey/delSurveyCateg")
    public Boolean delSurveyCateg(@Validated DelInterveneCategRequest delInterveneCateg ) {
        return surveyBiz.delSurveyCateg(delInterveneCateg);
    }

    /**
    * 获取评估问卷列表
    * @param
    * @return
    */
    @ApiOperation("获取评估问卷列表")
    @PostMapping("v1/intervene/survey/pageSurvey")
    public SurveyResponse pageSurvey(@RequestBody @Validated FindSurveyRequest findSurvey ) {
        return surveyBiz.pageSurvey(findSurvey);
    }

    /**
    * 获取评估问卷详细信息
    * @param
    * @return
    */
    @ApiOperation("获取评估问卷详细信息")
    @GetMapping("v1/intervene/survey/getSurvey")
    public SurveyInfoResponse getSurvey(@Validated String surveyId, @Validated Integer step) {
        return surveyBiz.getSurvey(surveyId,step);
    }

    /**
    * 删除问卷
    * @param
    * @return
    */
    @ApiOperation("删除问卷")
    @DeleteMapping("v1/intervene/survey/delSurvey")
    public Boolean delSurvey(@Validated DelSurveyRequest delSurvey ) {
        return surveyBiz.delSurvey(delSurvey);
    }

    /**
    * 启用、禁用问卷
    * @param
    * @return
    */
    @ApiOperation("启用、禁用问卷")
    @PostMapping("v1/intervene/survey/setSurveyState")
    public Boolean setSurveyState(@RequestBody @Validated SetSurveyStateRequest setSurveyState ) {
        return surveyBiz.setSurveyState(setSurveyState);
    }

    /**
    * 保存问卷基本信息
    * @param
    * @return
    */
    @ApiOperation("保存问卷基本信息")
    @PostMapping("v1/intervene/survey/saveSurveyBasic")
    public Boolean saveSurveyBasic(@RequestBody @Validated SaveSurveyBasicRequest saveSurveyBasic ) {
        return surveyBiz.saveSurveyBasic(saveSurveyBasic);
    }

    /**
    * 保存问卷题目信息
    * @param
    * @return
    */
    @ApiOperation("保存问卷题目信息")
    @PostMapping("v1/intervene/survey/saveSurveyQuestion")
    public Boolean saveSurveyQuestion(@RequestBody @Validated SaveSurveyQuestionRequest saveSurveyQuestion ) {
        return surveyBiz.saveSurveyQuestion(saveSurveyQuestion);
    }

    /**
    * 保存问卷维度公式
    * @param
    * @return
    */
    @ApiOperation("保存问卷维度公式")
    @PostMapping("v1/intervene/survey/saveSurveyEval")
    public Boolean saveSurveyEval(@RequestBody @Validated SaveSurveyEvalRequest saveSurveyEval ) {
        return surveyBiz.saveSurveyEval(saveSurveyEval);
    }

    /**
    * 保存问卷报告设置
    * @param
    * @return
    */
    @ApiOperation("保存问卷报告设置")
    @PostMapping("v1/intervene/survey/saveSurveyReport")
    public Boolean saveSurveyReport(@RequestBody @Validated SaveSurveyReportRequest saveSurveyReport ) {
        return surveyBiz.saveSurveyReport(saveSurveyReport);
    }


}