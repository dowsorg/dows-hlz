package org.dows.hep.biz.base.question;

import org.dows.hep.api.base.question.request.*;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:问题:
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class QuestionSectionBiz{
    /**
    * @param
    * @return
    * @说明: 新增和更新问题集[问卷]
    * @关联表: QuestionSection,QuestionSectionItem,QuestionSectionDimension
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String saveOrUpdQuestionSection(QuestionSectionRequest questionSection ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 分页问题集[问卷]
    * @关联表: QuestionSection,QuestionSectionItem,QuestionSectionDimension
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public QuestionSectionResponse pageQuestionSection(QuestionSectionSearchRequest questionSectionSearch ) {
        return new QuestionSectionResponse();
    }
    /**
    * @param
    * @return
    * @说明: 列出问题集[问卷]-无分页
    * @关联表: 
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<QuestionSectionResponse> listQuestionSection(QuestionSectionSearchRequest questionSectionSearch ) {
        return new ArrayList<QuestionSectionResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 根据ID获取详情
    * @关联表: QuestionSection,QuestionSectionItem,QuestionSectionDimension
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public QuestionSectionResponse getQuestionSection(String questionSectionId ) {
        return new QuestionSectionResponse();
    }
    /**
    * @param
    * @return
    * @说明: 启用问题集[问卷]
    * @关联表: QuestionSection
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean enabledQuestionSection(String questionSectionId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 禁用问题集[问卷]
    * @关联表: QuestionSection
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean disabledQuestionSection(String questionSectionId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 排序问题集[问卷]
    * @关联表: 
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean sortQuestionSection(String questionSectionId, Integer sequence ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 交换问题集[问卷]
    * @关联表: QuestionSection
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean transposeQuestionSection(String leftSectionId, String rightSectionId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除or批量删除问题集[问卷]
    * @关联表: QuestionSection
    * @工时: 6H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean delQuestionSection(String questionSectionIds ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 复制问题集[问卷]
    * @关联表: caseInstance
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String copyQuestionSection(String oriQuestionSectionId ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 自动生成问题集[问卷]
    * @关联表: QuestionSection,QuestionSectionItem,QuestionInstance
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String generateQuestionSectionAutomatic(QuestionnaireGenerateElementsRequest questionnaireGenerateElements ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 合并问题集[问卷]
    * @关联表: QuestionSection,QuestionSectionItem,QuestionInstance
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String mergeQuestionSection(QuestionnaireMergeElementsRequest questionnaireMergeElements ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 查询
    * @关联表: QuestionSection,QuestionSectionItem
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public QuestionSectionResponse listSectionQuestion(QuestionsInSectionRequest questionsInSection ) {
        return new QuestionSectionResponse();
    }
    /**
    * @param
    * @return
    * @说明: 排序问题集-题目
    * @关联表: QuestionSection,QuestionSectionItem
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean sortSectionQuestion(String questionSectionId, String questionSectionItemId, Integer sequence ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 交换问题集-题目顺序
    * @关联表: QuestionSection,QuestionSectionItem
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean transposeSectionQuestion(String questionSectionId, String leftQuestionSectionItemId, String rightQuestionSectionItemId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 启用问题集-题目
    * @关联表: QuestionSection,QuestionSectionItem
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean enabledSectionQuestion(String questionSectionId, String questionSectionItemId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 禁用问题集-题目
    * @关联表: QuestionSection,QuestionSectionItem
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean disabledSectionQuestion(String questionSectionId, String questionSectionItemId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除or批量删除问题集-题目
    * @关联表: QuestionSection,QuestionSectionItem
    * @工时: 6H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean delSectionQuestion(String questionSectionId, String questionSectionItemIds ) {
        return Boolean.FALSE;
    }
}