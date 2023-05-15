package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.tenant.casus.request.CaseCategoryRequest;
import org.dows.hep.api.tenant.casus.response.CaseCategoryResponse;
import org.dows.hep.entity.CaseCategoryEntity;
import org.dows.hep.service.CaseCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/5/15 16:53
 */
@Service
@RequiredArgsConstructor
public class TenantCaseCategoryBiz {
    private final CaseCategoryService caseCategoryService;
    private final TenantCaseBaseBiz baseBiz;

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    @Transactional
    public String saveOrUpdateCaseCategory(CaseCategoryRequest request) {
        if (BeanUtil.isEmpty(request)) {
            return "";
        }

        // check
        checkBeforeSaveOrUpd(request);

        // handle
        CaseCategoryEntity caseCategoryEntity = BeanUtil.copyProperties(request, CaseCategoryEntity.class);
        caseCategoryService.saveOrUpdate(caseCategoryEntity);

        return caseCategoryEntity.getCaseCategId();
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    public List<CaseCategoryResponse> listCaseCategory(List<String> categIds) {
        if (categIds == null || categIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<CaseCategoryEntity> list = caseCategoryService.lambdaQuery()
                .in(CaseCategoryEntity::getCaseCategId, categIds)
                .list();
        return BeanUtil.copyToList(list, CaseCategoryResponse.class);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    public CaseCategoryEntity getById(String categId) {
        LambdaQueryWrapper<CaseCategoryEntity> queryWrapper = new LambdaQueryWrapper<CaseCategoryEntity>()
                .eq(CaseCategoryEntity::getCaseCategId, categId);
        return caseCategoryService.getOne(queryWrapper);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    public List<CaseCategoryResponse> getChildrenByPid(String pid, String categoryGroup) {
        List<CaseCategoryResponse> result = new ArrayList<>();
        List<CaseCategoryResponse> listInGroup = listInGroup(categoryGroup);
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
    public List<CaseCategoryResponse> getParents(String id, String categoryGroup) {
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
        List<CaseCategoryResponse> arrayList = getParents0(id, categoryGroup);
        if (arrayList.isEmpty()) {
            return new String[0];
        }

        return arrayList.stream()
                .map(CaseCategoryResponse::getCaseCategId)
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
        LambdaQueryWrapper<CaseCategoryEntity> queryWrapper1 = new LambdaQueryWrapper<CaseCategoryEntity>()
                .in(CaseCategoryEntity::getCaseCategId, ids);
        boolean remRes1 = caseCategoryService.remove(queryWrapper1);

        // del children
        LambdaQueryWrapper<CaseCategoryEntity> remWrapper = new LambdaQueryWrapper<CaseCategoryEntity>()
                .in(CaseCategoryEntity::getCaseCategId, ids);
        boolean remRes2 = caseCategoryService.remove(remWrapper);

        return remRes1 && remRes2;
    }

    private void checkBeforeSaveOrUpd(CaseCategoryRequest request) {
        String uniqueId = request.getCaseCategId();
        if (StrUtil.isBlank(uniqueId)) {
            request.setCaseCategId(baseBiz.getIdStr());
            if (StrUtil.isBlank(request.getCaseCategPid())) {
                request.setCaseCategPid(baseBiz.getCaseCategoryPid());
            }
        } else {
            CaseCategoryEntity entity = getById(uniqueId);
            if (BeanUtil.isEmpty(entity)) {
                throw new BizException("数据不存在");
            }
            request.setId(entity.getId());
        }
    }

    private List<CaseCategoryResponse> listInGroup(String categGroup) {
        if (StrUtil.isBlank(categGroup)) {
            return new ArrayList<>();
        }

        return caseCategoryService.lambdaQuery()
                .eq(CaseCategoryEntity::getCaseCategGroup, categGroup)
                .list()
                .stream()
                .map(item -> BeanUtil.copyProperties(item, CaseCategoryResponse.class))
                .toList();
    }

    private void convertList2TreeList(List<CaseCategoryResponse> sources, String pid, List<CaseCategoryResponse> target) {
        // list children
        List<CaseCategoryResponse> children = listChildren(sources, item -> pid.equals(item.getCaseCategPid()));

        // add target
        target.addAll(children);

        // handle children
        target.forEach(item -> traverse(item, sources));
    }

    private void traverse(CaseCategoryResponse node, List<CaseCategoryResponse> sources) {
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

    private boolean checkNull(CaseCategoryResponse currentNode, List<CaseCategoryResponse> sources) {
        List<CaseCategoryResponse> children = listChildren(sources, item -> currentNode.getCaseCategId().equals(item.getCaseCategPid()));
        if (children.isEmpty()) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private static List<CaseCategoryResponse> listChildren(List<CaseCategoryResponse> sources, Predicate<CaseCategoryResponse> predicate) {
        return sources.stream()
                .filter(predicate)
                .toList();
    }

    private void handleCurrentNode(CaseCategoryResponse currentNode, List<CaseCategoryResponse> sources) {
        List<CaseCategoryResponse> children = listChildren(sources, item -> currentNode.getCaseCategId().equals(item.getCaseCategPid()));
        currentNode.setChildren(children);
    }


    private void handleChildrenNode(CaseCategoryResponse currentNode, List<CaseCategoryResponse> sources) {
        List<CaseCategoryResponse> children = currentNode.getChildren();
        for (CaseCategoryResponse cNode : children) {
            traverse(cNode, sources);
        }
    }

    private ArrayList<CaseCategoryResponse> getParents0(String id, String categoryGroup) {
        if (StrUtil.isBlank(id) || StrUtil.isBlank(categoryGroup)) {
            return new ArrayList<>();
        }

        // list all in group
        List<CaseCategoryResponse> listInGroup = listInGroup(categoryGroup);
        if (listInGroup == null || listInGroup.isEmpty()) {
            return new ArrayList<>();
        }

        // convert list 2 collect
        Map<String, CaseCategoryResponse> idCollect = listInGroup.stream()
                .collect(Collectors.toMap(CaseCategoryResponse::getCaseCategId, v -> v, (v1, v2) -> v1));

        // get parents
        ArrayList<CaseCategoryResponse> result = new ArrayList<>();
        getCcrList(id, idCollect, result);
        Collections.reverse(result);
        return result;
    }

    private void getCcrList(String id, Map<String, CaseCategoryResponse> idCollect, ArrayList<CaseCategoryResponse> result) {
        CaseCategoryResponse caseCategoryResponse = idCollect.get(id);
        if (caseCategoryResponse == null) {
            return;
        }

        // 处理当前节点
        result.add(caseCategoryResponse);

        // 判空
        String caseCategPid = caseCategoryResponse.getCaseCategPid();
        if (baseBiz.getCaseCategoryPid().equals(caseCategPid)) {
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
