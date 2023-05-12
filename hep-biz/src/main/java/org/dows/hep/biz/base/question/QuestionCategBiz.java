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
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

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

    /**
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     * @param
     * @return 
     */
    @Transactional
    public String saveOrUpdateQuestionCategory(QuestionCategoryRequest questionCategory) {
        if (BeanUtil.isEmpty(questionCategory)) {
            return "";
        }

        // check
        checkBeforeSaveOrUpd(questionCategory);

        // handle
        QuestionCategoryEntity questionCategoryEntity = BeanUtil.copyProperties(questionCategory, QuestionCategoryEntity.class);
        questionCategoryService.saveOrUpdate(questionCategoryEntity);

        return questionCategoryEntity.getQuestionCategId();
    }

    /**
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     * @param 
     * @return 
     */
    public QuestionCategoryEntity getQuestionCategory(String questionCategId) {
        LambdaQueryWrapper<QuestionCategoryEntity> queryWrapper = new LambdaQueryWrapper<QuestionCategoryEntity>()
                .eq(QuestionCategoryEntity::getQuestionCategId, questionCategId);
        return questionCategoryService.getOne(queryWrapper);
    }

    /**
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     * @param 
     * @return 
     */
    public List<QuestionCategoryResponse> getChildrenByPid(String pid, String categoryGroup) {
        List<QuestionCategoryResponse> result = new ArrayList<>();
        List<QuestionCategoryResponse> listInGroup = listInGroup(categoryGroup);
        convertList2TreeList(listInGroup, pid, result);
        return result;
    }

    /**
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     * @param 
     * @return 
     */
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

    private List<QuestionCategoryResponse> listInGroup(String questionCategGroup) {
        if (StrUtil.isBlank(questionCategGroup)) {
            return new ArrayList<>();
        }

        return questionCategoryService.lambdaQuery()
                .eq(QuestionCategoryEntity::getQuestionCategGroup, questionCategGroup)
                .list()
                .stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionCategoryResponse.class))
                .toList();
    }

    private void convertList2TreeList(List<QuestionCategoryResponse> sources, String pid, List<QuestionCategoryResponse> target) {
        // list children
        List<QuestionCategoryResponse> children = listChildren(sources, item -> pid.equals(item.getQuestionCategPid()));

        // add target
        target.addAll(children);

        // handle children
        target.forEach(item -> traverse(item, sources));
    }

    private void traverse(QuestionCategoryResponse node, List<QuestionCategoryResponse> sources) {
        // 判空
        boolean isBack = checkNull(node, sources);
        if (isBack) {
            return;
        }

        // 处理当前节点
        handleCurrentNode(node, sources);

        // 处理子节点
        handleChildrenNode(node, sources);
    }

    private boolean checkNull(QuestionCategoryResponse currentNode, List<QuestionCategoryResponse> sources) {
        List<QuestionCategoryResponse> children = listChildren(sources, item -> currentNode.getQuestionCategId().equals(item.getQuestionCategPid()));
        if (children.isEmpty()) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private void handleCurrentNode(QuestionCategoryResponse currentNode, List<QuestionCategoryResponse> sources) {
        List<QuestionCategoryResponse> children = listChildren(sources, item -> currentNode.getQuestionCategId().equals(item.getQuestionCategPid()));
        currentNode.setChildren(children);
    }

    private static List<QuestionCategoryResponse> listChildren(List<QuestionCategoryResponse> sources, Predicate<QuestionCategoryResponse> predicate) {
        return sources.stream()
                .filter(predicate)
                .toList();
    }

    private void handleChildrenNode(QuestionCategoryResponse currentNode, List<QuestionCategoryResponse> sources) {
        List<QuestionCategoryResponse> children = currentNode.getChildren();
        for (QuestionCategoryResponse cNode : children) {
            traverse(cNode, sources);
        }
    }

    private void checkBeforeSaveOrUpd(QuestionCategoryRequest questionCategory) {
        String questionCategId = questionCategory.getQuestionCategId();
        if (StrUtil.isBlank(questionCategId)) {
            questionCategory.setQuestionCategId(baseBiz.getIdStr());
            if (StrUtil.isBlank(questionCategory.getQuestionCategPid())) {
                questionCategory.setQuestionCategPid(baseBiz.getQuestionInstancePid());
            }
        } else {
            QuestionCategoryEntity questionCategoryEntity = getQuestionCategory(questionCategId);
            if (BeanUtil.isEmpty(questionCategoryEntity)) {
                throw new BizException("数据不存在");
            }
            questionCategory.setId(questionCategoryEntity.getId());
        }
    }
}
