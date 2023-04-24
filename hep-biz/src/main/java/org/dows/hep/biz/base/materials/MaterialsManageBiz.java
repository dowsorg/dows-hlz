package org.dows.hep.biz.base.materials;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.materials.request.MaterialsAttachmentRequest;
import org.dows.hep.api.user.materials.request.MaterialsRequest;
import org.dows.hep.api.user.materials.request.MaterialsSearchRequest;
import org.dows.hep.api.user.materials.response.MaterialsAttachmentResponse;
import org.dows.hep.api.user.materials.response.MaterialsResponse;
import org.dows.hep.entity.MaterialsAttachmentEntity;
import org.dows.hep.entity.MaterialsEntity;
import org.dows.hep.service.MaterialsAttachmentService;
import org.dows.hep.service.MaterialsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:资料中心:资料信息管理
 * @date 2023年4月18日 上午10:45:07
 */
@RequiredArgsConstructor
@Service
public class MaterialsManageBiz {
    private final MaterialsService materialsService;
    private final MaterialsAttachmentService materialsAttachmentService;

    /**
     * @param
     * @return
     * @说明: 新增和更新
     * @关联表: Materials, MaterialsAttachment
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @Transactional
    public String saveOrUpdMaterials(MaterialsRequest materials) {
        MaterialsEntity materialsEntity = BeanUtil.copyProperties(materials, MaterialsEntity.class);
        String id = fillDefault(materialsEntity);
//        saveOrUpd(materialsEntity);
        materialsService.saveOrUpdate(materialsEntity);

        handleAttachment(materials);
        return id;
    }

    /**
     * @param
     * @return
     * @说明: 分页
     * @关联表: Materials, MaterialsAttachment
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public IPage<MaterialsEntity> pageMaterials(MaterialsSearchRequest materialsSearch) {
        // TODO
//        IPage<MaterialsEntity> pageEntity = getPage(materialsSearch);

//        IPage<MaterialsEntity> pageResult = materialsService.lambdaQuery()
//                .like(BeanUtil.isNotEmpty(materialsSearch) && StrUtil.isNotBlank(materialsSearch.getKeyword()), MaterialsEntity::getTitle, materialsSearch.getKeyword())
//                .like(BeanUtil.isNotEmpty(materialsSearch) && StrUtil.isNotBlank(materialsSearch.getKeyword()), MaterialsEntity::getAccountName, materialsSearch.getKeyword())
//                .like(BeanUtil.isNotEmpty(materialsSearch) && StrUtil.isNotBlank(materialsSearch.getKeyword()), MaterialsEntity::getAccountId, materialsSearch.getKeyword())
//                .page(pageEntity);
//        List<MaterialsEntity> records = pageResult.getRecords();
//        if (records != null && records.size() > 0) {
//            List<MaterialsResponse> collect = records.stream()
//                    .map(item -> BeanUtil.copyProperties(item, MaterialsResponse.class))
//                    .collect(Collectors.toList());
//        }
        return null;
    }

    /**
     * @param
     * @return
     * @说明: 条件查询-无分页
     * @关联表: Materials, MaterialsAttachment
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public List<MaterialsResponse> listMaterials(MaterialsSearchRequest materialsSearch) {
        List<MaterialsResponse> result = new ArrayList<>();

        // list materials
        List<MaterialsEntity> materialsEntityList = materialsService.lambdaQuery()
                .like(BeanUtil.isNotEmpty(materialsSearch) && StrUtil.isNotBlank(materialsSearch.getKeyword()), MaterialsEntity::getTitle, materialsSearch.getKeyword())
                .like(BeanUtil.isNotEmpty(materialsSearch) && StrUtil.isNotBlank(materialsSearch.getKeyword()), MaterialsEntity::getAccountName, materialsSearch.getKeyword())
                .like(BeanUtil.isNotEmpty(materialsSearch) && StrUtil.isNotBlank(materialsSearch.getKeyword()), MaterialsEntity::getAccountId, materialsSearch.getKeyword())
                .list();
        if (materialsEntityList == null || materialsEntityList.isEmpty()) {
            return result;
        }

        // list attachmentResponse
        List<String> materialsIds = materialsEntityList.stream().map(MaterialsEntity::getMaterialsId).collect(Collectors.toList());
        List<MaterialsAttachmentResponse> attachmentResponseList = listMaterialsAttachmentResponses(materialsIds);

        // build
        Map<String, List<MaterialsAttachmentResponse>> attachmentMap = attachmentResponseList.stream()
                .collect(Collectors.groupingBy(MaterialsAttachmentResponse::getMaterialsId));
        return materialsEntityList.stream()
                .map(materialsEntity -> {
                    MaterialsResponse materialsResponse = BeanUtil.copyProperties(materialsEntity, MaterialsResponse.class);
                    String materialsId = materialsResponse.getMaterialsId();
                    List<MaterialsAttachmentResponse> att = attachmentMap.get(materialsId);
                    materialsResponse.setMaterialsAttachment(att);

                    return materialsResponse;
                })
                .collect(Collectors.toList());
    }

    /**
     * @param
     * @return
     * @说明: 根据ID获取详情
     * @关联表: Materials, MaterialsAttachment
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public MaterialsResponse getMaterials(String materialsId) {
        MaterialsResponse result = new MaterialsResponse();
        if (StrUtil.isBlank(materialsId)) {
            return result;
        }

        return new MaterialsResponse();
    }

    /**
     * @param
     * @return
     * @说明: 启用
     * @关联表: Materials, MaterialsAttachment
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean enabledMaterials(String materialsId) {
        return Boolean.FALSE;
    }

    /**
     * @param
     * @return
     * @说明: 禁用
     * @关联表: Materials, MaterialsAttachment
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean disabledMaterials(String materialsId) {
        return Boolean.FALSE;
    }

    /**
     * @param
     * @return
     * @说明: 删除or批量删除
     * @关联表: Materials, MaterialsAttachment
     * @工时: 6H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean delMaterials(String materialsId) {
        return Boolean.FALSE;
    }

    private void handleAttachment(MaterialsRequest materials) {
        if (BeanUtil.isEmpty(materials)) {
            return;
        }

        List<MaterialsAttachmentRequest> materialsAttachments = materials.getMaterialsAttachments();
        if (materialsAttachments == null || materialsAttachments.isEmpty()) {
            return;
        }

        String materialsId = materials.getMaterialsId();
        String appId = materials.getAppId();
        int sequence = 1;
        List<MaterialsAttachmentEntity> attachmentEntities = materialsAttachments.stream()
                .map(item -> {
                    // TODO generate id
                    String id = "";
                    MaterialsAttachmentEntity materialsAttachmentEntity = BeanUtil.copyProperties(item, MaterialsAttachmentEntity.class);
                    materialsAttachmentEntity.setMaterialsAttachmentId(id);
                    materialsAttachmentEntity.setMaterialsId(materialsId);
                    materialsAttachmentEntity.setAppId(appId);
                    materialsAttachmentEntity.setSequence(sequence + 1);
                    return materialsAttachmentEntity;
                })
                .collect(Collectors.toList());
        materialsAttachmentService.saveOrUpdateBatch(attachmentEntities);
    }

    private String fillDefault(MaterialsEntity materialsEntity) {
        String id = "";
        if (BeanUtil.isEmpty(materialsEntity)) {
            return id;
        }

        // todo generate id
        String materialsId = materialsEntity.getMaterialsId();
        if (materialsId == null) {
//            id = IdGenerator;
            materialsEntity.setMaterialsId(id);
        }

        Boolean enabled = materialsEntity.getEnabled();
        if (enabled == null) {
            materialsEntity.setEnabled(true);
        }

        Integer sequence = materialsEntity.getSequence();
        if (null == sequence) {
            materialsEntity.setSequence(0);
        }

        return id;
    }

    private List<MaterialsAttachmentResponse> listMaterialsAttachmentResponses(List<String> materialsIds) {
        List<MaterialsAttachmentResponse> result = new ArrayList<>();
        if (materialsIds == null || materialsIds.isEmpty()) {
            return result;
        }


        List<MaterialsAttachmentEntity> attachmentEntityList = materialsAttachmentService.lambdaQuery()
                .in(MaterialsAttachmentEntity::getMaterialsId, materialsIds)
                .list();
        if (attachmentEntityList == null || attachmentEntityList.isEmpty()) {
            return result;
        }

        return attachmentEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, MaterialsAttachmentResponse.class))
                .collect(Collectors.toList());
    }
}