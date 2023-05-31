package org.dows.hep.biz.base.picture;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.base.materials.request.MaterialsAttachmentRequest;
import org.dows.hep.api.base.picture.request.PictureRequest;
import org.dows.hep.api.base.picture.response.PictureResponse;
import org.dows.hep.api.enums.EnumMaterials;
import org.dows.hep.api.exception.MaterialException;
import org.dows.hep.entity.MaterialsAttachmentEntity;
import org.dows.hep.entity.MaterialsCategoryEntity;
import org.dows.hep.entity.MaterialsEntity;
import org.dows.hep.service.MaterialsAttachmentService;
import org.dows.hep.service.MaterialsCategoryService;
import org.dows.hep.service.MaterialsService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jx
 * @date 2023/4/24 15:42
 */
@Service
@RequiredArgsConstructor
public class PictureManageBiz {
    private final MaterialsService materialsService;
    private final MaterialsAttachmentService materialsAttachmentService;
    private final MaterialsCategoryService materialsCategoryService;
    private final IdGenerator idGenerator;

    /**
     * @param
     * @return
     * @说明: 新增 图示
     * @关联表: materials、materials_attachment、materials_category
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/24 15:49
     */
    @DSTransactional
    public Boolean savePicture(PictureRequest picture) {
        Boolean flag = null;
        //1、根据分类名称找到资料ID
        MaterialsCategoryEntity entity = materialsCategoryService.lambdaQuery()
                .eq(MaterialsCategoryEntity::getAppId, picture.getAppId())
                .eq(MaterialsCategoryEntity::getCategoryName, picture.getCategoryName())
                .oneOpt()
                .orElseThrow(() -> new MaterialException(EnumMaterials.CATEGORY_IS_NOT_FIND));
        //2、获取图片附件
        List<MaterialsAttachmentRequest> attachmentList = JSONUtil.toList(picture.getMaterialsAttachment(), MaterialsAttachmentRequest.class);
        //3、保存图片主体
        MaterialsEntity material = MaterialsEntity.builder()
                .materialsId(idGenerator.nextIdStr())
                .bizCode(picture.getBizCode())
                .appId(picture.getAppId())
                .categoryId(entity.getMaterialsCategoryId())
                .categoryName(picture.getCategoryName())
                .enabled(picture.getEnabled() == Boolean.TRUE ? 1 : 0)
                .build();
        materialsService.save(material);
        //3、保存图片附件
        List<MaterialsAttachmentEntity> entities = new ArrayList<>();
        if (attachmentList != null && attachmentList.size() > 0) {
            attachmentList.forEach(attachment -> {
                MaterialsAttachmentEntity materialsAttachment = MaterialsAttachmentEntity.builder()
                        .materialsAttachmentId(idGenerator.nextIdStr())
                        .materialsId(material.getMaterialsId())
                        .fileName(attachment.getFileName())
                        .fileUri(attachment.getFileUri())
                        .appId(picture.getAppId())
                        .fileType(attachment.getFileType())
                        .build();
                entities.add(materialsAttachment);
            });
            flag = materialsAttachmentService.saveBatch(entities);
        }
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 删除 图示
     * @关联表: materials、materials_attachment
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/24 19:49
     */
    @DSTransactional
    public Integer deletePersonPictures(Set<String> ids, String appId) {
        Integer count = 0;
        for (String id : ids) {
            //1、删除附件
            List<MaterialsAttachmentEntity> entityList = materialsAttachmentService.lambdaQuery()
                    .eq(MaterialsAttachmentEntity::getAppId, appId)
                    .eq(MaterialsAttachmentEntity::getMaterialsId, id)
                    .list();
            //1.1、如果非空，删除
            if (entityList != null && entityList.size() > 0) {
                LambdaUpdateWrapper<MaterialsAttachmentEntity> attachmentEntityWrapper = Wrappers.lambdaUpdate(MaterialsAttachmentEntity.class);
                attachmentEntityWrapper.set(MaterialsAttachmentEntity::getDeleted, true)
                        .eq(MaterialsAttachmentEntity::getMaterialsId, id)
                        .eq(MaterialsAttachmentEntity::getAppId, appId);
                //1.2、删除附件
                materialsAttachmentService.update(attachmentEntityWrapper);
            }
            //2、删除资料
            MaterialsEntity materials = materialsService.lambdaQuery()
                    .eq(MaterialsEntity::getAppId, appId)
                    .eq(MaterialsEntity::getMaterialsId, id)
                    .one();
            if (materials != null && !ReflectUtil.isObjectNull(materials)) {
                LambdaUpdateWrapper<MaterialsEntity> materialsWrapper = Wrappers.lambdaUpdate(MaterialsEntity.class);
                materialsWrapper.set(MaterialsEntity::getDeleted, true)
                        .eq(MaterialsEntity::getMaterialsId, id)
                        .eq(MaterialsEntity::getAppId, appId);
                materialsService.update(materialsWrapper);
            }
            count++;
        }
        return count;
    }

    /**
     * @param
     * @return
     * @说明: 图示 列表
     * @关联表: materials、materials_attachment、materials_category
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/25 09:00
     */
    public IPage<PictureResponse> listPersonPictures(PictureRequest request) {
        IPage<PictureResponse> voPage = new Page<>();
        //1、根据分类名称找到资料ID
        MaterialsCategoryEntity materialsCategory = materialsCategoryService.lambdaQuery()
                .eq(MaterialsCategoryEntity::getAppId, request.getAppId())
                .eq(MaterialsCategoryEntity::getCategoryName, request.getCategoryName())
                .oneOpt()
                .orElseThrow(() -> new MaterialException(EnumMaterials.CATEGORY_IS_NOT_FIND));
        //2、根据资料类别ID找到资料
        LambdaQueryWrapper<MaterialsEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaterialsEntity::getAppId, request.getAppId())
                .eq(MaterialsEntity::getCategoryId, materialsCategory.getMaterialsCategoryId());
        Page<MaterialsEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        IPage<MaterialsEntity> materialPage = materialsService.page(page, queryWrapper);
        List<PictureResponse> voList = new ArrayList<>();
        //3、根据资料ID找到资料相关的附件
        if (materialPage.getRecords() != null && materialPage.getRecords().size() > 0) {
            materialPage.getRecords().forEach(materialsEntity -> {
                List<MaterialsAttachmentEntity> attachmentEntities = materialsAttachmentService.lambdaQuery()
                        .eq(MaterialsAttachmentEntity::getMaterialsId, materialsEntity.getMaterialsId())
                        .eq(MaterialsAttachmentEntity::getAppId, request.getAppId())
                        .list();
                //3.1、分组去重，人物有两个图片，只取一个
                Map<String, MaterialsAttachmentEntity> map = new HashMap<>();
                if (request.getCategoryName().equals("人物图示")) {
                    map = attachmentEntities.stream().collect(Collectors.groupingBy(MaterialsAttachmentEntity::getMaterialsId,
                            Collectors.collectingAndThen(Collectors.toList(), value -> value.get(1))));
                } else {
                    map = attachmentEntities.stream().collect(Collectors.groupingBy(MaterialsAttachmentEntity::getMaterialsId,
                            Collectors.collectingAndThen(Collectors.toList(), value -> value.get(0))));
                }
                //3.2、赋值
                PictureResponse pictureResponse = new PictureResponse();
                BeanUtil.copyProperties(materialsEntity, pictureResponse);
                MaterialsAttachmentEntity materialsAttachmentEntity = map.get(materialsEntity.getMaterialsId());
                pictureResponse.setMaterialsAttachment(materialsAttachmentEntity.getFileUri());
                voList.add(pictureResponse);
            });
        }
        BeanUtils.copyProperties(materialPage, voPage, new String[]{"records"});
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * @param
     * @return
     * @说明: 人物头像列表
     * @关联表: materials、materials_attachment、materials_category
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/5/31 14:23
     */
    public IPage<PictureResponse> listPersonAvatar(PictureRequest request) {
        IPage<PictureResponse> voPage = new Page<>();
        //1、根据分类名称找到资料ID
        MaterialsCategoryEntity materialsCategory = materialsCategoryService.lambdaQuery()
                .eq(MaterialsCategoryEntity::getAppId, request.getAppId())
                .eq(MaterialsCategoryEntity::getCategoryName, request.getCategoryName())
                .oneOpt()
                .orElseThrow(() -> new MaterialException(EnumMaterials.CATEGORY_IS_NOT_FIND));
        //2、根据资料类别ID找到资料
        LambdaQueryWrapper<MaterialsEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaterialsEntity::getAppId, request.getAppId())
                .eq(MaterialsEntity::getCategoryId, materialsCategory.getMaterialsCategoryId());
        Page<MaterialsEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        IPage<MaterialsEntity> materialPage = materialsService.page(page, queryWrapper);
        List<PictureResponse> voList = new ArrayList<>();
        //3、根据资料ID找到资料相关的附件
        if (materialPage.getRecords() != null && materialPage.getRecords().size() > 0) {
            materialPage.getRecords().forEach(materialsEntity -> {
                List<MaterialsAttachmentEntity> attachmentEntities = materialsAttachmentService.lambdaQuery()
                        .eq(MaterialsAttachmentEntity::getMaterialsId, materialsEntity.getMaterialsId())
                        .eq(MaterialsAttachmentEntity::getAppId, request.getAppId())
                        .list();
                //3.1、分组去重，人物取头像
                Map<String, MaterialsAttachmentEntity> map = attachmentEntities.stream().collect(Collectors.groupingBy(MaterialsAttachmentEntity::getMaterialsId,
                        Collectors.collectingAndThen(Collectors.toList(), value -> value.get(0))));
                //3.2、赋值
                PictureResponse pictureResponse = new PictureResponse();
                BeanUtil.copyProperties(materialsEntity, pictureResponse);
                MaterialsAttachmentEntity materialsAttachmentEntity = map.get(materialsEntity.getMaterialsId());
                pictureResponse.setMaterialsAttachment(materialsAttachmentEntity.getFileUri());
                voList.add(pictureResponse);
            });
        }
        BeanUtils.copyProperties(materialPage, voPage, new String[]{"records"});
        voPage.setRecords(voList);
        return voPage;
    }
}
