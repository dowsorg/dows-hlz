package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.enums.QuestionESCEnum;
import org.dows.hep.api.base.question.request.QuestionCategoryRequest;
import org.dows.hep.api.base.question.response.QuestionCategoryResponse;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.entity.QuestionCategoryEntity;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.service.QuestionCategoryService;
import org.dows.hep.service.QuestionInstanceService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
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
    private final QuestionInstanceService questionInstanceService;
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

        QuestionCategoryEntity questionCategoryEntity = convertRequest2Entity(questionCategory);
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
    public QuestionCategoryEntity getById(String questionCategId) {
        LambdaQueryWrapper<QuestionCategoryEntity> queryWrapper = new LambdaQueryWrapper<QuestionCategoryEntity>()
                .eq(QuestionCategoryEntity::getQuestionCategId, questionCategId);
        return questionCategoryService.getOne(queryWrapper);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    public List<QuestionCategoryResponse> getChildrenByPid(String pid, String categoryGroup) {
        List<QuestionCategoryResponse> result = new ArrayList<>();
        List<QuestionCategoryResponse> listInGroup = listInGroup(categoryGroup);
        convertList2TreeList(listInGroup, pid, result);
        return result;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 递归获取该类目的全路径 
     * @date 2023/5/11 21:22
     */
    public List<QuestionCategoryResponse> getFullPath(String id, String categoryGroup) {
        return getParents0(id, categoryGroup);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 获取该类目的全路径 id 数组
     * @date 2023/5/11 21:22
     */
    public String[] getFullPathIds(String id, String categoryGroup) {
        List<QuestionCategoryResponse> arrayList = getParents0(id, categoryGroup);
        if (arrayList.isEmpty()) {
            return new String[0];
        }

        return arrayList.stream()
                .map(QuestionCategoryResponse::getQuestionCategId)
                .toArray(String[]::new);
    }

    /**
     * @author fhb
     * @description 根据Ids 获取父类的 QuestionCategoryResponse
     * @date 2023/5/26 16:48
     * @param
     * @return
     */
    public List<QuestionCategoryResponse> listParents(List<String> categIds) {
        List<QuestionCategoryResponse> curs = listQuestionCategory(categIds);
        if (Objects.isNull(curs) || curs.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> pIds = curs.stream().map(QuestionCategoryResponse::getQuestionCategPid).toList();
        return listQuestionCategory(pIds);
    }

    /**
     * @author fhb
     * @description 根据ids 获取对应的 QuestionCategoryResponse
     * @date 2023/5/26 16:48
     * @param
     * @return
     */
    public List<QuestionCategoryResponse> listQuestionCategory(List<String> categIds) {
        if (categIds == null || categIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<QuestionCategoryEntity> list = questionCategoryService.lambdaQuery()
                .in(QuestionCategoryEntity::getQuestionCategId, categIds)
                .list();
        return BeanUtil.copyToList(list, QuestionCategoryResponse.class);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    @Transactional
    public Boolean delByIds(List<String> ids) {
        if (Objects.isNull(ids)) {
            return false;
        }

        // get referenced id
        Boolean referenced = isReferenced(ids);
        if (referenced) {
            throw new BizException(QuestionESCEnum.CANNOT_DEL_FER_DATA);
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

    private Boolean isReferenced(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Boolean.FALSE;
        }

        List<QuestionInstanceEntity> list = questionInstanceService.lambdaQuery()
                .in(QuestionInstanceEntity::getQuestionCategId, ids)
                .list();
        if (list != null && !list.isEmpty()) {
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

    private List<QuestionCategoryResponse> listChildren(List<QuestionCategoryResponse> sources, Predicate<QuestionCategoryResponse> predicate) {
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

    private QuestionCategoryEntity convertRequest2Entity(QuestionCategoryRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        QuestionCategoryEntity result = QuestionCategoryEntity.builder()
                .appId(baseBiz.getAppId())
                .questionCategPid(request.getQuestionCategPid())
                .questionCategId(request.getQuestionCategId())
                .questionCategName(request.getQuestionCategName())
                .questionCategGroup(request.getQuestionCategGroup())
                .sequence(request.getSequence())
                .build();

        String questionCategId = result.getQuestionCategId();
        if (StrUtil.isBlank(questionCategId)) {
            result.setQuestionCategId(baseBiz.getIdStr());
            if (StrUtil.isBlank(result.getQuestionCategPid())) {
                result.setQuestionCategPid(baseBiz.getQuestionInstancePid());
            }
        } else {
            QuestionCategoryEntity oriEntity = getById(questionCategId);
            if (BeanUtil.isEmpty(oriEntity)) {
                throw new BizException(CaseESCEnum.DATA_NULL);
            }
            result.setId(oriEntity.getId());
        }
        return result;
    }

    private void getQcrpList(String id, Map<String, QuestionCategoryResponse> idCollect, ArrayList<QuestionCategoryResponse> result) {
        QuestionCategoryResponse questionCategoryResponse = idCollect.get(id);
        if (questionCategoryResponse == null) {
            return;
        }

        // 处理当前节点
        result.add(questionCategoryResponse);

        // 判空
        String questionCategPid = questionCategoryResponse.getQuestionCategPid();
        if (baseBiz.getQuestionInstancePid().equals(questionCategPid)) {
            return;
        }

        // 处理父节点
        getQcrpList(questionCategPid, idCollect, result);
    }

    @NotNull
    private ArrayList<QuestionCategoryResponse> getParents0(String id, String categoryGroup) {
        if (StrUtil.isBlank(id) || StrUtil.isBlank(categoryGroup)) {
            return new ArrayList<>();
        }

        // list all in group
        List<QuestionCategoryResponse> listInGroup = listInGroup(categoryGroup);
        if (listInGroup == null || listInGroup.isEmpty()) {
            return new ArrayList<>();
        }

        // convert list 2 collect
        Map<String, QuestionCategoryResponse> idCollect = listInGroup.stream()
                .collect(Collectors.toMap(QuestionCategoryResponse::getQuestionCategId, v -> v, (v1, v2) -> v1));

        // get parents
        ArrayList<QuestionCategoryResponse> result = new ArrayList<>();
        getQcrpList(id, idCollect, result);
        Collections.reverse(result);
        return result;
    }
}
