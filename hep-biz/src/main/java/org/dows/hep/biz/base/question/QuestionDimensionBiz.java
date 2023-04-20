package org.dows.hep.biz.base.question;

import org.dows.hep.api.base.question.request.QuestionDimensionRequest;
import org.dows.hep.api.base.question.response.QuestionDimensionResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:问题:问题-维度
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class QuestionDimensionBiz{
    /**
    * @param
    * @return
    * @说明: 关联问题维度
    * @关联表: QuestionDimension,QuestionInstance,QuestionSectionDimension
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean relateQuestionDimension(QuestionDimensionRequest questionDimension ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取问题下所有维度
    * @关联表: QuestionDimension,QuestionInstance,QuestionSectionDimension
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<QuestionDimensionResponse> listQuestionDimension(String questionInstanceId ) {
        return new ArrayList<QuestionDimensionResponse>();
    }
}