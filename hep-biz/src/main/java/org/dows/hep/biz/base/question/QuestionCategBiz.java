package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.request.QuestionCategoryRequest;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.response.QuestionCategoryResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.entity.QuestionCategoryEntity;
import org.dows.hep.service.QuestionCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/4/19 16:19
 */
@Service
@RequiredArgsConstructor
public class QuestionCategBiz {

    private final QuestionDomainBaseBiz baseBiz;
    private final QuestionInstanceBiz questionInstanceBiz;
    private final QuestionCategoryService questionCategoryService;
    public static final String CATEG_PATH_DELIMITER = "|";

    @Transactional
    public String saveOrUpdateQuestionCategory(QuestionCategoryRequest questionCategory) {
        if (BeanUtil.isEmpty(questionCategory)) {
            return "";
        }

        // before handle
        beforeSaveOrUpd(questionCategory);

        // handle
        QuestionCategoryEntity questionCategoryEntity = BeanUtil.copyProperties(questionCategory, QuestionCategoryEntity.class);
        questionCategoryService.saveOrUpdate(questionCategoryEntity);

        // after handle
//        buildCategPath(questionCategoryEntity);
//        questionCategoryService.updateById(questionCategoryEntity);

        return questionCategoryEntity.getQuestionCategId();
    }

    public List<QuestionCategoryEntity> getChildrenByPid(String pid, String categoryGroup) {
//        return questionCategoryService.getChildrenByPid(pid, categoryGroup);
        return Collections.emptyList();
    }

    public List<QuestionCategoryResponse> getAllCategory(String categoryGroup) {
        List<QuestionCategoryEntity> allCategory = questionCategoryService.getAllCategory(categoryGroup);
        if (allCategory == null || allCategory.isEmpty()) {
            return new ArrayList<>();
        }

        return allCategory.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionCategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public Boolean delByIds(List<String> ids) {
        if (Objects.isNull(ids)) {
            return false;
        }

        // get referenced id
        Boolean referenced = isReferenced(ids);
        if (referenced) {
            throw new BizException("被引用类目不可删除");
        }

        // del self
        LambdaQueryWrapper<QuestionCategoryEntity> queryWrapper1 = new LambdaQueryWrapper<QuestionCategoryEntity>()
                .in(QuestionCategoryEntity::getQuestionCategId, ids);
        boolean remRes1 = questionCategoryService.remove(queryWrapper1);

        // del children
        LambdaQueryWrapper<QuestionCategoryEntity> remWrapper = new LambdaQueryWrapper<QuestionCategoryEntity>()
                .in(QuestionCategoryEntity::getQuestionCategPid, ids);
        boolean remRes2 = questionCategoryService.remove(remWrapper);

        return remRes1 && remRes2;
    }

    private void getUnReferencedIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        QuestionSearchRequest questionSearchRequest = QuestionSearchRequest.builder()
                .categIdList(ids)
                .build();
        List<QuestionResponse> questionResponses = questionInstanceBiz.listQuestion(questionSearchRequest);
        if (questionResponses == null || questionResponses.isEmpty()) {
            return;
        }

        List<String> referencedIds = questionResponses.stream()
                .map(QuestionResponse::getQuestionCategId)
                .toList();
        ids.removeAll(referencedIds);
    }

    private Boolean isReferenced(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Boolean.FALSE;
        }

        QuestionSearchRequest questionSearchRequest = QuestionSearchRequest.builder()
                .categIdList(ids)
                .build();
        List<QuestionResponse> questionResponses = questionInstanceBiz.listQuestion(questionSearchRequest);
        if (questionResponses != null && !questionResponses.isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

//    private void buildCategPath(QuestionCategoryEntity entity) {
//        String categIdPath = "";
//        String categNamePath = "";
//        String questionCategId = entity.getQuestionCategId();
//        String questionCategName = entity.getQuestionCategName();
//
//        List<QuestionCategoryEntity> parents = getParents(questionCategId);
//        if (parents.size() > 0) {
//            categNamePath = parents.stream()
//                    .map(QuestionCategoryEntity::getQuestionCategName)
//                    .collect(Collectors.joining(CATEG_PATH_DELIMITER));
//            categNamePath += CATEG_PATH_DELIMITER + questionCategName;
//
//            categIdPath = parents.stream()
//                    .map(QuestionCategoryEntity::getQuestionCategId)
//                    .collect(Collectors.joining(CATEG_PATH_DELIMITER));
//            categIdPath += CATEG_PATH_DELIMITER + questionCategId;
//        } else {
//            categNamePath = questionCategName;
//            categIdPath = questionCategId;
//        }
//
//        entity.setQuestionCategNamePath(categNamePath);
//        entity.setQuestionCategIdPath(categIdPath);
//    }
//
//    private List<QuestionCategoryEntity> getParents(String questionCategId) {
//        List<QuestionCategoryEntity> result = new ArrayList<>();
//        buildParents(questionCategId, result);
//        Collections.reverse(result);
//        return result;
//    }

//    private void buildParents(String questionCategId, List<QuestionCategoryEntity> list) {
//        QuestionCategoryEntity parent = getParent(questionCategId);
//        if (null == parent) {
//            return;
//        }
//
//        list.add(parent);
//        String categId = parent.getQuestionCategId();
//        buildParents(categId, list);
//    }

//    private QuestionCategoryEntity getParent(String questionCategId) {
//        QuestionCategoryEntity questionCategoryEntity = questionCategoryService.getById(questionCategId);
//        if (BeanUtil.isEmpty(questionCategoryEntity)) {
//            return null;
//        }
//
//        String questionCategPid = questionCategoryEntity.getQuestionCategPid();
//        return questionCategoryService.getById(questionCategPid);
//    }

    private void beforeSaveOrUpd(QuestionCategoryRequest questionCategory) {
        String questionCategId = questionCategory.getQuestionCategId();
        if (StrUtil.isBlank(questionCategId)) {
            questionCategory.setQuestionCategId(baseBiz.getIdStr());
        }

        String questionCategPid = questionCategory.getQuestionCategPid();
        if (StrUtil.isBlank(questionCategPid)) {
            questionCategory.setQuestionCategPid(baseBiz.getQuestionInstancePid());
        }
        // todo seq
    }
}
