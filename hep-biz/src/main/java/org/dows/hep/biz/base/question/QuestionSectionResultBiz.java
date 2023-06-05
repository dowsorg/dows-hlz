package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.dto.QuestionResultRecordDTO;
import org.dows.hep.api.base.question.enums.QuestionESCEnum;
import org.dows.hep.api.base.question.request.QuestionSectionResultItemRequest;
import org.dows.hep.api.base.question.request.QuestionSectionResultRequest;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.entity.QuestionSectionEntity;
import org.dows.hep.entity.QuestionSectionResultEntity;
import org.dows.hep.service.QuestionSectionResultService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author lait.zhang
 * @description project descr:问题:问题集-答题记录
 * @date 2023年4月23日 上午9:44:34
 */
@Service
@RequiredArgsConstructor
public class QuestionSectionResultBiz {
    private final QuestionDomainBaseBiz baseBiz;
    private final QuestionSectionResultItemBiz questionSectionResultItemBiz;
    private final QuestionSectionResultService questionSectionResultService;
    private final QuestionSectionBiz questionSectionBiz;

    /**
     * @param
     * @return
     * @说明: 新增和更新问题集-答题
     * @关联表: QuestionSection, QuestionSectionResult, QuestionSectionResultItem
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    @DSTransactional
    public String saveOrUpdQuestionSectionResult(QuestionSectionResultRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        // check and saveOrUpd base-info
        QuestionSectionResultEntity entity = convertRequest2Entity(request);
        questionSectionResultService.saveOrUpdate(entity);

        // save or upd item
        List<QuestionSectionResultItemRequest> itemRequests = request.getQuestionSectionResultItem();
        questionSectionResultItemBiz.saveOrUpdBatch(itemRequests, entity.getQuestionSectionResultId());

        return entity.getQuestionSectionResultId();
    }

    /**
     * @param
     * @return
     * @说明: 获取问题集和答题记录
     * @关联表: QuestionSection, QuestionSectionResult, QuestionSectionResultItem
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public QuestionSectionResponse getQuestionSectionResult(String questionSectionResultId) {
        if (StrUtil.isBlank(questionSectionResultId)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        QuestionSectionResultEntity entity = getById(questionSectionResultId);
        String questionSectionId = Optional.ofNullable(entity)
                .map(QuestionSectionResultEntity::getQuestionSectionId)
                .orElse("");
        QuestionResultRecordDTO questionResultRecordDTO = questionSectionResultItemBiz.listQuestionResult(questionSectionResultId);

        return questionSectionBiz.getQuestionSection(questionSectionId, questionResultRecordDTO);
    }

    private QuestionSectionResultEntity convertRequest2Entity(QuestionSectionResultRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        String questionSectionId = request.getQuestionSectionId();
        if (StrUtil.isBlank(questionSectionId)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        QuestionSectionEntity questionSection = questionSectionBiz.getById(questionSectionId);
        String questionSectionName = Optional.ofNullable(questionSection)
                .map(QuestionSectionEntity::getName)
                .orElse("");
        String questionSectionStructure = Optional.ofNullable(questionSection)
                .map(QuestionSectionEntity::getQuestionSectionStructure)
                .orElse("");
        Integer questionCount = Optional.ofNullable(questionSection)
                .map(QuestionSectionEntity::getQuestionCount)
                .orElse(0);

        QuestionSectionResultEntity result = QuestionSectionResultEntity.builder()
                .appId(baseBiz.getAppId())
                .questionSectionResultId(request.getQuestionSectionResultId())
                .questionSectionId(request.getQuestionSectionId())
                .questionSectionName(questionSectionName)
                .questionCount(questionCount)
                .questionSectionStructure(questionSectionStructure)
                .rightCount(0)
                .scoreStructure("")
                .score(0.0f)
                .accountId(request.getAccountId())
                .accountName(request.getAccountName())
                .build();

        String uniqueId = result.getQuestionSectionResultId();
        if (StrUtil.isBlank(uniqueId)) {
            result.setQuestionSectionResultId(baseBiz.getIdStr());
        } else {
            QuestionSectionResultEntity oriEntity = getById(uniqueId);
            if (BeanUtil.isEmpty(oriEntity)) {
                throw new BizException(QuestionESCEnum.DATA_NULL);
            }
            result.setId(oriEntity.getId());
        }

        return result;
    }

    private QuestionSectionResultEntity getById(String uniqueId) {
        return questionSectionResultService.lambdaQuery()
                .eq(QuestionSectionResultEntity::getQuestionSectionResultId, uniqueId)
                .one();
    }
}