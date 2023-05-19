package org.dows.hep.biz.base.materials;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.materials.MaterialsEnabledEnum;
import org.dows.hep.api.base.materials.request.MaterialsAttachmentRequest;
import org.dows.hep.api.base.materials.request.MaterialsPageRequest;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.api.base.materials.request.MaterialsSearchRequest;
import org.dows.hep.api.base.materials.response.MaterialsAttachmentResponse;
import org.dows.hep.api.base.materials.response.MaterialsPageResponse;
import org.dows.hep.api.base.materials.response.MaterialsResponse;
import org.dows.hep.api.enums.EnumStatus;
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
    private final MaterialsBaseBiz baseBiz;
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
    public String saveOrUpdMaterials(MaterialsRequest materialsRequest) {
        if (BeanUtil.isEmpty(materialsRequest)) {
            return "";
        }

        // check and saveOrUpd
        checkBeforeSaveOrUpd(materialsRequest);
        MaterialsEntity materialsEntity = BeanUtil.copyProperties(materialsRequest, MaterialsEntity.class);
        materialsService.saveOrUpdate(materialsEntity);

        // handle materials' attachments
        handleAttachment(materialsRequest);
        return materialsEntity.getMaterialsId();
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
    public IPage<MaterialsPageResponse> pageMaterials(MaterialsPageRequest request) {
        Page<MaterialsPageResponse> result = new Page<>();
        if (BeanUtil.isEmpty(request)) {
            return result;
        }

        Page<MaterialsEntity> pageRequest = new Page<>(request.getPageNo(), request.getPageSize());
        Page<MaterialsEntity> pageResult = materialsService.lambdaQuery()
                .eq(MaterialsEntity::getAppId, request.getAppId())
                .eq(MaterialsEntity::getBizCode, request.getBizCode())
                .like(BeanUtil.isNotEmpty(request) && StrUtil.isNotBlank(request.getKeyword()), MaterialsEntity::getTitle, request.getKeyword())
                .page(pageRequest);
        return baseBiz.convertPage(pageResult, MaterialsPageResponse.class);
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
    public List<MaterialsResponse> listMaterials(MaterialsSearchRequest request) {
        List<MaterialsResponse> result = new ArrayList<>();

        // list materials
        List<MaterialsEntity> materialsEntityList = materialsService.lambdaQuery()
                .eq(MaterialsEntity::getAppId, request.getAppId())
                .eq(MaterialsEntity::getBizCode, request.getBizCode())
                .like(BeanUtil.isNotEmpty(request) && StrUtil.isNotBlank(request.getKeyword()), MaterialsEntity::getTitle, request.getKeyword())
                .list();
        if (materialsEntityList == null || materialsEntityList.isEmpty()) {
            return result;
        }

        // list attachmentResponse
        List<String> materialsIds = materialsEntityList.stream()
                .map(MaterialsEntity::getMaterialsId)
                .collect(Collectors.toList());
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
        if (StrUtil.isBlank(materialsId)) {
            return new MaterialsResponse();
        }

        // get materials
        MaterialsEntity materialsEntity = getById(materialsId);
        if (BeanUtil.isEmpty(materialsEntity)) {
            return new MaterialsResponse();
        }
        MaterialsResponse result = BeanUtil.copyProperties(materialsEntity, MaterialsResponse.class);

        // list attachment of materials
        List<MaterialsAttachmentEntity> attachmentEntityList = materialsAttachmentService.lambdaQuery()
                .eq(MaterialsAttachmentEntity::getMaterialsId, materialsId)
                .list();
        if (attachmentEntityList == null || attachmentEntityList.isEmpty()) {
            return result;
        }
        List<MaterialsAttachmentResponse> attachmentResponseList = attachmentEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, MaterialsAttachmentResponse.class))
                .collect(Collectors.toList());
        result.setMaterialsAttachment(attachmentResponseList);
        return result;
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
        if (StrUtil.isBlank(materialsId)) {
            return Boolean.FALSE;
        }

        LambdaUpdateWrapper<MaterialsEntity> updateWrapper = new LambdaUpdateWrapper<MaterialsEntity>()
                .eq(MaterialsEntity::getMaterialsId, materialsId)
                .set(MaterialsEntity::getEnabled, EnumStatus.ENABLE.getCode());
        return materialsService.update(updateWrapper);
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
        if (StrUtil.isBlank(materialsId)) {
            return Boolean.FALSE;
        }

        LambdaUpdateWrapper<MaterialsEntity> updateWrapper = new LambdaUpdateWrapper<MaterialsEntity>()
                .eq(MaterialsEntity::getMaterialsId, materialsId)
                .set(MaterialsEntity::getEnabled, EnumStatus.DISABLE.getCode());
        return materialsService.update(updateWrapper);
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
    public Boolean delMaterials(List<String> materialsIds) {
        if (materialsIds == null || materialsIds.isEmpty()) {
            return Boolean.FALSE;
        }

        // remove materials
        LambdaQueryWrapper<MaterialsEntity> queryWrapper1 = new LambdaQueryWrapper<MaterialsEntity>()
                .in(MaterialsEntity::getMaterialsId, materialsIds);
        boolean remRes1 = materialsService.remove(queryWrapper1);

        // remove attachment
        LambdaQueryWrapper<MaterialsAttachmentEntity> queryWrapper2 = new LambdaQueryWrapper<MaterialsAttachmentEntity>()
                .in(MaterialsAttachmentEntity::getMaterialsId, materialsIds);
        boolean remRes2 = materialsAttachmentService.remove(queryWrapper2);

        return remRes1 && remRes2;
    }

    private void checkBeforeSaveOrUpd(MaterialsRequest request) {
        String uniqueId = request.getMaterialsId();
        if (StrUtil.isBlank(uniqueId)) {
            request.setAppId(baseBiz.getAppId());
            request.setMaterialsId(baseBiz.getIdStr());
            request.setEnabled(request.getEnabled() == null ? MaterialsEnabledEnum.ENABLED.getCode() : request.getEnabled());
        } else {
            MaterialsEntity entity = getById(uniqueId);
            if (BeanUtil.isEmpty(entity)) {
                throw new BizException("数据不存在");
            }
            request.setId(entity.getId());
        }
    }

    private MaterialsEntity getById(String materialsId) {
        LambdaQueryWrapper<MaterialsEntity> queryWrapper = new LambdaQueryWrapper<MaterialsEntity>()
                .eq(MaterialsEntity::getMaterialsId, materialsId);
        return materialsService.getOne(queryWrapper);
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
        List<MaterialsAttachmentEntity> attachmentEntities = materialsAttachments.stream()
                .map(item -> {
                    MaterialsAttachmentEntity materialsAttachmentEntity = BeanUtil.copyProperties(item, MaterialsAttachmentEntity.class);
                    materialsAttachmentEntity.setMaterialsAttachmentId(baseBiz.getIdStr());
                    materialsAttachmentEntity.setMaterialsId(materialsId);
                    materialsAttachmentEntity.setAppId(appId);
                    return materialsAttachmentEntity;
                })
                .collect(Collectors.toList());
        materialsAttachmentService.saveOrUpdateBatch(attachmentEntities);
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