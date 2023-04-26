package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.request.QuestionCategoryRequest;
import org.dows.hep.api.base.question.response.QuestionCategoryResponse;
import org.dows.hep.entity.QuestionCategoryEntity;
import org.dows.hep.service.QuestionCategoryService;
import org.dows.sequence.api.IdGenerator;
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

    private final IdGenerator idGenerator;
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
        QuestionCategoryEntity questionCategoryEntity;
        String questionCategId = questionCategory.getQuestionCategId();
        if (StrUtil.isBlank(questionCategId)) {
            questionCategoryEntity = saveCategory(questionCategory);
        } else {
            questionCategoryEntity = updCategory(questionCategory);
        }

        // after handle
        buildCategPath(questionCategoryEntity);
        questionCategoryService.updateById(questionCategoryEntity);
        return questionCategId;
    }

    private void beforeSaveOrUpd(QuestionCategoryRequest questionCategory) {
        String questionCategGroup = questionCategory.getQuestionCategGroup();
        if (StrUtil.isBlank(questionCategGroup)) {
            throw new BizException("问题域类别管理分组不能为空");
        }

        String questionCategPid = questionCategory.getQuestionCategPid();
        if (questionCategPid == null) {
            questionCategory.setQuestionCategPid("0");
        }

        LambdaQueryWrapper<QuestionCategoryEntity> queryWrapper = new LambdaQueryWrapper<QuestionCategoryEntity>()
                .eq(QuestionCategoryEntity::getQuestionCategGroup, questionCategGroup)
                .orderByDesc(QuestionCategoryEntity::getSequence);
        QuestionCategoryEntity lastOne = questionCategoryService.getOne(queryWrapper);
        Integer lastSequence = lastOne.getSequence();
        questionCategory.setSequence(lastSequence);
    }

    private QuestionCategoryEntity saveCategory(QuestionCategoryRequest questionCategory) {
        QuestionCategoryEntity questionCategoryEntity = BeanUtil.copyProperties(questionCategory, QuestionCategoryEntity.class);
        questionCategoryEntity.setQuestionCategId(idGenerator.nextIdStr());
        questionCategoryService.save(questionCategoryEntity);
        return questionCategoryEntity;
    }

    private QuestionCategoryEntity updCategory(QuestionCategoryRequest questionCategory) {
        QuestionCategoryEntity questionCategoryEntity = BeanUtil.copyProperties(questionCategory, QuestionCategoryEntity.class);
        questionCategoryService.updateById(questionCategoryEntity);
        return questionCategoryEntity;
    }

    public List<QuestionCategoryResponse> getChildrenByPid(String pid, String categoryGroup) {
        List<QuestionCategoryEntity> children = questionCategoryService.getChildrenByPid(pid, categoryGroup);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }

        return children.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionCategoryResponse.class))
                .collect(Collectors.toList());
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
    public Boolean delById(String id) {
        if (Objects.isNull(id)) {
            return false;
        }

        // del self
        questionCategoryService.removeById(id);

        // del children
        LambdaQueryWrapper<QuestionCategoryEntity> remWrapper = new LambdaQueryWrapper<>();
        remWrapper.eq(QuestionCategoryEntity::getQuestionCategPid, id);
        questionCategoryService.remove(remWrapper);
        return true;
    }

    private void buildCategPath(QuestionCategoryEntity entity) {
        String categIdPath = "";
        String categNamePath = "";
        String questionCategId = entity.getQuestionCategId();
        String questionCategName = entity.getQuestionCategName();

        List<QuestionCategoryEntity> parents = getParents(questionCategId);
        if (parents.size() > 0) {
            categNamePath = parents.stream()
                    .map(QuestionCategoryEntity::getQuestionCategName)
                    .collect(Collectors.joining(CATEG_PATH_DELIMITER));
            categNamePath += CATEG_PATH_DELIMITER + questionCategName;

            categIdPath = parents.stream()
                    .map(QuestionCategoryEntity::getQuestionCategId)
                    .collect(Collectors.joining(CATEG_PATH_DELIMITER));
            categIdPath += CATEG_PATH_DELIMITER + questionCategId;
        } else {
            categNamePath = questionCategName;
            categIdPath = questionCategId;
        }

        entity.setQuestionCategNamePath(categNamePath);
        entity.setQuestionCategIdPath(categIdPath);
    }

    private List<QuestionCategoryEntity> getParents(String questionCategId) {
        List<QuestionCategoryEntity> result = new ArrayList<>();
        buildParents(questionCategId, result);
        Collections.reverse(result);
        return result;
    }

    private void buildParents(String questionCategId, List<QuestionCategoryEntity> list) {
        QuestionCategoryEntity parent = getParent(questionCategId);
        if (null == parent) {
            return;
        }

        list.add(parent);
        String categId = parent.getQuestionCategId();
        buildParents(categId, list);
    }

    private QuestionCategoryEntity getParent(String questionCategId) {
        QuestionCategoryEntity questionCategoryEntity = questionCategoryService.getById(questionCategId);
        if (BeanUtil.isEmpty(questionCategoryEntity)) {
            return null;
        }

        String questionCategPid = questionCategoryEntity.getQuestionCategPid();
        return questionCategoryService.getById(questionCategPid);
    }
}
