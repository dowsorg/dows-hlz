package org.dows.hep.biz.base.question;

import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:问题:问题
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class QuestionInstanceBiz{
    /**
    * @param
    * @return
    * @说明: 新增和更新
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String saveOrUpdQuestion(QuestionRequest question ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 分页
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public QuestionResponse pageQuestion(QuestionSearchRequest questionSearch ) {
        return new QuestionResponse();
    }
    /**
    * @param
    * @return
    * @说明: 条件查询-无分页
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<QuestionResponse> listQuestion(QuestionSearchRequest questionSearch ) {
        return new ArrayList<QuestionResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 根据ID获取详情
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public QuestionResponse getQuestion(String questionInstanceId ) {
        return new QuestionResponse();
    }
    /**
    * @param
    * @return
    * @说明: 启用
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean enabledQuestion(String questionInstanceId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 禁用
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean disabledQuestion(String questionInstanceId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 排序
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean sortQuestion(String string, Integer sequence ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 交换
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean transposeQuestion(String leftQuestionInstanceId, String rightQuestionInstanceId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除or批量删除
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 6H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean delQuestion(String questionInstanceIds ) {
        return Boolean.FALSE;
    }
}