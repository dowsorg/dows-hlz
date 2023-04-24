package org.dows.hep.biz.base.picture;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.api.user.materials.request.MaterialsAttachmentRequest;
import org.dows.hep.biz.enums.EnumMaterials;
import org.dows.hep.biz.exception.MaterialException;
import org.dows.hep.entity.MaterialsAttachmentEntity;
import org.dows.hep.entity.MaterialsCategoryEntity;
import org.dows.hep.entity.MaterialsEntity;
import org.dows.hep.service.MaterialsAttachmentService;
import org.dows.hep.service.MaterialsCategoryService;
import org.dows.hep.service.MaterialsService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
     * @关联表: materials、materials_attachment
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/24 15:49
     */
    @DSTransactional
    public Boolean savePicture(MaterialsRequest materials) {
        Boolean flag = null;
        //1、根据分类名称找到资料ID
        MaterialsCategoryEntity entity = materialsCategoryService.lambdaQuery()
                .eq(MaterialsCategoryEntity::getAppId, materials.getAppId())
                .eq(MaterialsCategoryEntity::getCategoryName, materials.getCategoryName())
                .oneOpt()
                .orElseThrow(() -> new MaterialException(EnumMaterials.CATEGORY_IS_NOT_FIND));
        //2、获取图片附件
        List<MaterialsAttachmentRequest> attachmentList = JSONArray.parseArray(materials.getMaterialsAttachment(), MaterialsAttachmentRequest.class);
        //3、保存图片主体
        MaterialsEntity material = MaterialsEntity.builder()
                .materialsId(idGenerator.nextIdStr())
                .appId(materials.getAppId())
                .categoryId(entity.getMaterialsCategoryId())
                .categoryName(materials.getCategoryName())
                .type(materials.getType())
                .enabled(materials.getEnabled())
                .build();
        materialsService.save(material);
        //3、保存图片附件
        List<MaterialsAttachmentEntity> entities = new ArrayList<>();
        if(attachmentList != null && attachmentList.size() > 0){
            attachmentList.forEach(attachment->{
                MaterialsAttachmentEntity materialsAttachment = MaterialsAttachmentEntity.builder()
                        .materialsAttachmentId(idGenerator.nextIdStr())
                        .materialsId(material.getMaterialsId())
                        .fileName(attachment.getFileName())
                        .fileUri(attachment.getFileUri())
                        .appId(materials.getAppId())
                        .fileType(attachment.getFileType())
                        .build();
                entities.add(materialsAttachment);
            });
            flag = materialsAttachmentService.saveBatch(entities);
        }
         return flag;
    }
}
