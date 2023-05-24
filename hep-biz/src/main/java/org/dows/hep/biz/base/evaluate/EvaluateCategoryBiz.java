package org.dows.hep.biz.base.evaluate;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.evaluate.request.EvaluateCategoryRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateCategoryResponse;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.entity.EvaluateCategoryEntity;
import org.dows.hep.service.EvaluateCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/5/24 11:18
 */
@Service
@AllArgsConstructor
public class EvaluateCategoryBiz {
    private final EvaluateCategoryService evaluateCategoryService;
    private final EvaluateBaseBiz baseBiz;

    /**
     * @author fhb
     * @description
     * @date 2023/5/24 11:19
     * @param
     * @return
     */
    @DSTransactional
    public String saveOrUpdateEvaluateCategory(EvaluateCategoryRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        EvaluateCategoryEntity evaluateCategoryEntity = checkBeforeSaveOrUpd(request);
        evaluateCategoryService.saveOrUpdate(evaluateCategoryEntity);

        return evaluateCategoryEntity.getEvaluateCategId();
    }

    /**
     * @author fhb
     * @description dude, go optimize yourself
     * @date 2023/5/24 11:19
     * @param
     * @return
     */
    public boolean batchSaveOrUpd(List<EvaluateCategoryRequest> list) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return Boolean.FALSE;
        }

        list.forEach(this::saveOrUpdateEvaluateCategory);
        return Boolean.TRUE;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    public List<EvaluateCategoryResponse> listCaseCategory(List<String> categIds) {
        if (categIds == null || categIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<EvaluateCategoryEntity> list = evaluateCategoryService.lambdaQuery()
                .in(EvaluateCategoryEntity::getEvaluateCategId, categIds)
                .list();
        return BeanUtil.copyToList(list, EvaluateCategoryResponse.class);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    public EvaluateCategoryEntity getById(String categId) {
        LambdaQueryWrapper<EvaluateCategoryEntity> queryWrapper = new LambdaQueryWrapper<EvaluateCategoryEntity>()
                .eq(EvaluateCategoryEntity::getEvaluateCategId, categId);
        return evaluateCategoryService.getOne(queryWrapper);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    public List<EvaluateCategoryResponse> getChildrenByPid(String pid, String categoryGroup) {
        List<EvaluateCategoryResponse> result = new ArrayList<>();
        List<EvaluateCategoryResponse> listInGroup = listInGroup(categoryGroup);
        convertList2TreeList(listInGroup, pid, result);
        return result;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    public List<EvaluateCategoryResponse> getParents(String id, String categoryGroup) {
        return getParents0(id, categoryGroup);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    public String[] getParentIds(String id, String categoryGroup) {
        List<EvaluateCategoryResponse> arrayList = getParents0(id, categoryGroup);
        if (arrayList.isEmpty()) {
            return new String[0];
        }

        return arrayList.stream()
                .map(EvaluateCategoryResponse::getEvaluateCategId)
                .toArray(String[]::new);
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
            throw new BizException("被引用类目不可删除");
        }

        // del self
        LambdaQueryWrapper<EvaluateCategoryEntity> queryWrapper1 = new LambdaQueryWrapper<EvaluateCategoryEntity>()
                .in(EvaluateCategoryEntity::getEvaluateCategId, ids);
        boolean remRes1 = evaluateCategoryService.remove(queryWrapper1);

        // del children
        LambdaQueryWrapper<EvaluateCategoryEntity> remWrapper = new LambdaQueryWrapper<EvaluateCategoryEntity>()
                .in(EvaluateCategoryEntity::getEvaluateCategId, ids);
        boolean remRes2 = evaluateCategoryService.remove(remWrapper);

        return remRes1 && remRes2;
    }

    private EvaluateCategoryEntity checkBeforeSaveOrUpd(EvaluateCategoryRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        EvaluateCategoryEntity result = EvaluateCategoryEntity.builder()
                .appId(baseBiz.getAppId())
                .evaluateCategId(request.getEvaluateCategId())
                .evaluateCategName(request.getEvaluateCategName())
                .evaluateCategGroup(request.getEvaluateCategGroup())
                .sequence(request.getSequence())
                .build();

        String uniqueId = result.getEvaluateCategId();
        if (StrUtil.isBlank(uniqueId)) {
            result.setEvaluateCategId(baseBiz.getIdStr());
            if (StrUtil.isBlank(result.getEvaluateCategPid())) {
                result.setEvaluateCategPid(baseBiz.getCategPid());
            }
        } else {
            EvaluateCategoryEntity entity = getById(uniqueId);
            if (BeanUtil.isEmpty(entity)) {
                throw new BizException(CaseESCEnum.DATA_NULL);
            }
            result.setId(entity.getId());
        }
        return result;
    }

    private List<EvaluateCategoryResponse> listInGroup(String categGroup) {
        if (StrUtil.isBlank(categGroup)) {
            return new ArrayList<>();
        }

        return evaluateCategoryService.lambdaQuery()
                .eq(EvaluateCategoryEntity::getEvaluateCategGroup, categGroup)
                .list()
                .stream()
                .map(item -> BeanUtil.copyProperties(item, EvaluateCategoryResponse.class))
                .toList();
    }

    private void convertList2TreeList(List<EvaluateCategoryResponse> sources, String pid, List<EvaluateCategoryResponse> target) {
        // list children
        List<EvaluateCategoryResponse> children = listChildren(sources, item -> pid.equals(item.getEvaluateCategPid()));

        // add target
        target.addAll(children);

        // handle children
        target.forEach(item -> traverse(item, sources));
    }

    private void traverse(EvaluateCategoryResponse node, List<EvaluateCategoryResponse> sources) {
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

    private boolean checkNull(EvaluateCategoryResponse currentNode, List<EvaluateCategoryResponse> sources) {
        List<EvaluateCategoryResponse> children = listChildren(sources, item -> currentNode.getEvaluateCategId().equals(item.getEvaluateCategPid()));
        if (children.isEmpty()) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private static List<EvaluateCategoryResponse> listChildren(List<EvaluateCategoryResponse> sources, Predicate<EvaluateCategoryResponse> predicate) {
        return sources.stream()
                .filter(predicate)
                .toList();
    }

    private void handleCurrentNode(EvaluateCategoryResponse currentNode, List<EvaluateCategoryResponse> sources) {
        List<EvaluateCategoryResponse> children = listChildren(sources, item -> currentNode.getEvaluateCategId().equals(item.getEvaluateCategPid()));
        currentNode.setChildren(children);
    }


    private void handleChildrenNode(EvaluateCategoryResponse currentNode, List<EvaluateCategoryResponse> sources) {
        List<EvaluateCategoryResponse> children = currentNode.getChildren();
        for (EvaluateCategoryResponse cNode : children) {
            traverse(cNode, sources);
        }
    }

    private ArrayList<EvaluateCategoryResponse> getParents0(String id, String categoryGroup) {
        if (StrUtil.isBlank(id) || StrUtil.isBlank(categoryGroup)) {
            return new ArrayList<>();
        }

        // list all in group
        List<EvaluateCategoryResponse> listInGroup = listInGroup(categoryGroup);
        if (listInGroup == null || listInGroup.isEmpty()) {
            return new ArrayList<>();
        }

        // convert list 2 collect
        Map<String, EvaluateCategoryResponse> idCollect = listInGroup.stream()
                .collect(Collectors.toMap(EvaluateCategoryResponse::getEvaluateCategId, v -> v, (v1, v2) -> v1));

        // get parents
        ArrayList<EvaluateCategoryResponse> result = new ArrayList<>();
        getCcrList(id, idCollect, result);
        Collections.reverse(result);
        return result;
    }

    private void getCcrList(String id, Map<String, EvaluateCategoryResponse> idCollect, ArrayList<EvaluateCategoryResponse> result) {
        EvaluateCategoryResponse EvaluateCategoryResponse = idCollect.get(id);
        if (EvaluateCategoryResponse == null) {
            return;
        }

        // 处理当前节点
        result.add(EvaluateCategoryResponse);

        // 判空
        String caseCategPid = EvaluateCategoryResponse.getEvaluateCategPid();
        if (baseBiz.getCategPid().equals(caseCategPid)) {
            return;
        }

        // 处理父节点
        getCcrList(caseCategPid, idCollect, result);
    }

    private Boolean isReferenced(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Boolean.FALSE;
        }


        return Boolean.FALSE;
    }
}
