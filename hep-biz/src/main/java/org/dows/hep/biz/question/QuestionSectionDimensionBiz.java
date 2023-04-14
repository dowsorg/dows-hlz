package org.dows.hep.biz.question;

import org.dows.framework.api.Response;
import org.dows.hep.api.question.request.QuestionSectionDimensionRequest;
import org.dows.hep.api.question.response.QuestionSectionDimensionResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:问题:问题集-维度
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
public class QuestionSectionDimensionBiz{
    /**
    * @param
    * @return
    * @说明: 新增和更新问题集维度
    * @关联表: QuestionSection,QuestionSectionDimension
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public Boolean saveOrUpdQuestionSectionDimension(QuestionSectionDimensionRequest questionSectionDimension ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取问题集所有维度
    * @关联表: QuestionSection,QuestionSectionDimension
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public List<QuestionSectionDimensionResponse> listQuestionSectionDimension(String questionSectionId ) {
        return new ArrayList<QuestionSectionDimensionResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 删除问题集维度
    * @关联表: QuestionSection,QuestionSectionDimension
    * @工时: 6H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public Boolean delQuestionSectionDimension(String questionSectionDimensionIds ) {
        return Boolean.FALSE;
    }
}