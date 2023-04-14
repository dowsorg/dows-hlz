package org.dows.hep.biz.question;

import org.dows.framework.api.Response;
import org.dows.hep.api.question.request.QuestionSectionResultRequest;
import org.dows.hep.api.question.request.QuestionSectionResultSearchRequest;
import org.dows.hep.api.question.response.QuestionSectionResultResponse;
import org.dows.hep.api.question.response.QuestionSectionResultResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:问题:问题集-答题记录
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
public class QuestionSectionResultBiz{
    /**
    * @param
    * @return
    * @说明: 新增和更新问题集-答题
    * @关联表: QuestionSection,QuestionSectionResult,QuestionSectionResultItem
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public Boolean saveOrUpdQuestionSectionResult(QuestionSectionResultRequest questionSectionResult ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 分页查询问题集-答题记录
    * @关联表: QuestionSection,QuestionSectionResult,QuestionSectionResultItem
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public QuestionSectionResultResponse pageQuestionSectionResult(QuestionSectionResultSearchRequest questionSectionResultSearch ) {
        return new QuestionSectionResultResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取问题集-答题记录
    * @关联表: QuestionSection,QuestionSectionResult,QuestionSectionResultItem
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public QuestionSectionResultResponse getQuestionSectionResult(String questionSectionResultId ) {
        return new QuestionSectionResultResponse();
    }
}