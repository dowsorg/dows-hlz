package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.tenant.casus.CaseCategoryGroupEnum;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.request.CaseCategoryRequest;
import org.dows.hep.api.tenant.casus.response.CaseCategoryResponse;
import org.dows.hep.entity.CaseCategoryEntity;
import org.dows.hep.entity.CaseSchemeEntity;
import org.dows.hep.service.CaseCategoryService;
import org.dows.hep.service.CaseSchemeService;
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
    private final CaseSchemeService caseSchemeService;
    private final TenantCaseBaseBiz baseBiz;

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    @DSTransactional
    public String saveOrUpdateCaseCategory(CaseCategoryRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        CaseCategoryEntity caseCategoryEntity = convertRequest2Entity(request);
        caseCategoryService.saveOrUpdate(caseCategoryEntity);

        return caseCategoryEntity.getCaseCategId();
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 批量新增和更新
     * @date 2023/5/24 11:25
     */
    public boolean batchSaveOrUpd(List<CaseCategoryRequest> list) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return Boolean.FALSE;
        }

        List<CaseCategoryEntity> entityList = convertRequestList2EntityList(list);
        return caseCategoryService.saveOrUpdateBatch(entityList);
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
        return listParents0(id, categoryGroup);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/11 21:22
     */
    public String[] getParentIds(String id, String categoryGroup) {
        List<CaseCategoryResponse> arrayList = listParents0(id, categoryGroup);
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
        if (CollUtil.isEmpty(ids)) {
            return Boolean.FALSE;
        }

        // get referenced id
        Boolean referenced = isReferenced(ids);
        if (referenced) {
            throw new BizException(CaseESCEnum.CANNOT_DEL_REF_DATA);
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

    private CaseCategoryEntity convertRequest2Entity(CaseCategoryRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        CaseCategoryEntity result = CaseCategoryEntity.builder()
                .appId(baseBiz.getAppId())
                .caseCategId(request.getCaseCategId())
                .caseCategName(request.getCaseCategName())
                .caseCategGroup(request.getCaseCategGroup())
                .sequence(request.getSequence())
                .build();

        String caseCategId = result.getCaseCategId();
        if (StrUtil.isBlank(caseCategId)) {
            result.setCaseCategId(baseBiz.getIdStr());
            if (StrUtil.isBlank(result.getCaseCategPid())) {
                result.setCaseCategPid(baseBiz.getCaseCategoryPid());
            }
        } else {
            CaseCategoryEntity oriEntity = getById(caseCategId);
            if (BeanUtil.isEmpty(oriEntity)) {
                throw new BizException(CaseESCEnum.DATA_NULL);
            }
            result.setId(oriEntity.getId());
        }
        return result;
    }

    private List<CaseCategoryEntity> convertRequestList2EntityList(List<CaseCategoryRequest> requestList) {
        if (CollUtil.isEmpty(requestList)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        List<CaseCategoryEntity> entityList = listEntityInGroup(CaseCategoryGroupEnum.CASE_SCHEME.name());
        Map<String, CaseCategoryEntity> idMapEntity = entityList.stream()
                .collect(Collectors.toMap(CaseCategoryEntity::getCaseCategId, item -> item));

        List<CaseCategoryEntity> result = new ArrayList<>();
        requestList.forEach(request -> {
            CaseCategoryEntity resultItem = CaseCategoryEntity.builder()
                    .appId(baseBiz.getAppId())
                    .caseCategId(request.getCaseCategId())
                    .caseCategName(request.getCaseCategName())
                    .caseCategGroup(request.getCaseCategGroup())
                    .sequence(request.getSequence())
                    .build();

            String caseCategId = resultItem.getCaseCategId();
            // 新增 or 更新
            if (StrUtil.isBlank(caseCategId)) {
                resultItem.setCaseCategId(baseBiz.getIdStr());
                if (StrUtil.isBlank(resultItem.getCaseCategPid())) {
                    resultItem.setCaseCategPid(baseBiz.getCaseCategoryPid());
                }
            } else {
                CaseCategoryEntity oriEntity = idMapEntity.get(caseCategId);
                if (BeanUtil.isEmpty(oriEntity)) {
                    throw new BizException(CaseESCEnum.DATA_NULL);
                }
                resultItem.setId(oriEntity.getId());
            }

            result.add(resultItem);
        });

        return result;
    }

    private List<CaseCategoryResponse> listInGroup(String categGroup) {
        List<CaseCategoryEntity> entityList = listEntityInGroup(categGroup);
        return BeanUtil.copyToList(entityList, CaseCategoryResponse.class);
    }

    private List<CaseCategoryEntity> listEntityInGroup(String categGroup) {
        if (StrUtil.isBlank(categGroup)) {
            return new ArrayList<>();
        }

        return caseCategoryService.lambdaQuery()
                .eq(CaseCategoryEntity::getCaseCategGroup, categGroup)
                .orderBy(true, true, CaseCategoryEntity::getSequence)
                .list();
    }

    private List<CaseCategoryResponse> listParents0(String id, String categoryGroup) {
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
        List<CaseCategoryResponse> children = listChildren(sources, item -> node.getCaseCategId().equals(item.getCaseCategPid()));
        if (children.isEmpty()) {
            return;
        }

        // 处理当前节点
        handleCurrentNode(node, sources);

        // 处理子节点
        handleChildrenNode(node, sources);
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
        if (CollUtil.isEmpty(ids)) {
            return Boolean.FALSE;
        }

        List<CaseSchemeEntity> list = caseSchemeService.lambdaQuery()
                .in(CaseSchemeEntity::getCaseCategId, ids)
                .list();
        if (CollUtil.isNotEmpty(list)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }


}
