package org.dows.hep.biz.base.materials;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.materials.request.MaterialsCategoryRequest;
import org.dows.hep.api.enums.EnumMaterials;
import org.dows.hep.api.exception.MaterialException;
import org.dows.hep.entity.MaterialsCategoryEntity;
import org.dows.hep.entity.MaterialsEntity;
import org.dows.hep.service.MaterialsCategoryService;
import org.dows.hep.service.MaterialsService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jx
 * @date 2023/4/24 16:13
 */
@Service
@RequiredArgsConstructor
public class MaterialsCategoryBiz {
    private final MaterialsCategoryService materialsCategoryService;
    private final IdGenerator idGenerator;
    private final MaterialsService materialsService;

    /**
     * @param
     * @return
     * @说明: 新增资料类别
     * @关联表: MaterialsCategory
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月24日 下午16:15:46
     */
    @DSTransactional
    public Boolean saveMaterialsCategory(MaterialsCategoryRequest materials) {
        //1、添加父节点
        if(StringUtils.isNotEmpty(materials.getBizCode())) {
            materials.setMaterialsCategIdPath(materials.getBizCode() + "/");
            materials.setMaterialsCategNamePath(materials.getBizCode() + "/");
        }
        //2、判断是否已经存在该类别名称
        materialsCategoryService.lambdaQuery()
                .eq(MaterialsCategoryEntity::getAppId, materials.getAppId())
                .eq(MaterialsCategoryEntity::getCategoryName, materials.getCategoryName())
                .oneOpt()
                .ifPresent((a) -> {
                    throw new MaterialException(EnumMaterials.CATEGORY_NAME_IS_EXIST);
                });
        //3、新增
        MaterialsCategoryEntity model = new MaterialsCategoryEntity();
        BeanUtils.copyProperties(materials, model);
        model.setMaterialsCategoryId(idGenerator.nextIdStr());
        model.setBizCode(materials.getBizCode());
        return materialsCategoryService.save(model);
    }

    /**
     * @param
     * @return
     * @说明: 批量新增或修改资料类别信息
     * @关联表: MaterialsCategory
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月06日 下午14:23:46
     */
    @DSTransactional
    public Integer batchSaveOrUpdateMaterialsCategory(List<MaterialsCategoryRequest> materialsList, String accountId, String accountName) {
        Integer count = 0;
        for(MaterialsCategoryRequest materials : materialsList) {
            //1、添加父节点
            if (StringUtils.isNotEmpty(materials.getBizCode())) {
                materials.setMaterialsCategIdPath(materials.getBizCode() + "/");
                materials.setMaterialsCategNamePath(materials.getBizCode() + "/");
            }
            //2、新增判断是否已经存在该类别名称
            if (StringUtils.isEmpty(materials.getMaterialsCategoryId())) {
                materialsCategoryService.lambdaQuery()
                        .eq(MaterialsCategoryEntity::getAppId, materials.getAppId())
                        .eq(MaterialsCategoryEntity::getCategoryName, materials.getCategoryName())
                        .oneOpt()
                        .ifPresent((a) -> {
                            throw new MaterialException(EnumMaterials.CATEGORY_NAME_IS_EXIST);
                        });
            }
            //3、判断有没有materialsCategoryId，有的话就更新，没有则新增
            if (StringUtils.isNotEmpty(materials.getMaterialsCategoryId())) {
                MaterialsCategoryEntity entity = materialsCategoryService.lambdaQuery()
                        .eq(MaterialsCategoryEntity::getMaterialsCategoryId, materials.getMaterialsCategoryId())
                        .eq(MaterialsCategoryEntity::getDeleted, false)
                        .one();
                BeanUtils.copyProperties(materials, entity);
                entity.setAccountId(accountId);
                entity.setAccountName(accountName);
                materialsCategoryService.updateById(entity);
            } else {
                MaterialsCategoryEntity model = new MaterialsCategoryEntity();
                BeanUtils.copyProperties(materials, model);
                model.setMaterialsCategoryId(idGenerator.nextIdStr());
                model.setBizCode(materials.getBizCode());
                model.setAccountId(accountId);
                model.setAccountName(accountName);
                materialsCategoryService.save(model);
            }
            count++;
        }
        return count;
    }

    /**
     * @param
     * @return
     * @说明: 获取 资料类别下的子类
     * @关联表: MaterialsCategory
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月25日 下午14:29:46
     */
    public List<MaterialsCategoryEntity> listChildMaterialsCategory(MaterialsCategoryRequest materials) {
        List<MaterialsCategoryEntity> categoryEntities = materialsCategoryService.lambdaQuery()
                .eq(MaterialsCategoryEntity::getMaterialsCategNamePath, materials.getCategoryName() + "/")
                .eq(MaterialsCategoryEntity::getAppId, materials.getAppId())
                .list();
        categoryEntities = categoryEntities.stream().sorted(Comparator.comparing(MaterialsCategoryEntity::getSequence)).collect(Collectors.toList());
        return categoryEntities;
    }

    /**
     * @param
     * @return
     * @说明: 删除 资料类别信息
     * @关联表: MaterialsCategory、Materials
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月25日 下午14:43:46
     */
    @DSTransactional
    public Integer deleteCaterialsCategorys(Set<String> materialsCategoryIds, String appId) {
        Integer count = 0;
        for (String materialsCategoryId : materialsCategoryIds) {
            //1、获取该类别
            MaterialsCategoryEntity materialsCategory = materialsCategoryService.lambdaQuery()
                    .eq(MaterialsCategoryEntity::getAppId, appId)
                    .eq(MaterialsCategoryEntity::getMaterialsCategoryId, materialsCategoryId)
                    .oneOpt()
                    .orElseThrow(() -> new MaterialException(EnumMaterials.CATEGORY_IS_NOT_FIND));
            //2、判断该类别是否存在数据，存在则不能删除
            List<MaterialsEntity> materialsList = materialsService.lambdaQuery()
                    .eq(MaterialsEntity::getAppId, appId)
                    .eq(MaterialsEntity::getCategoryId, materialsCategory.getMaterialsCategoryId())
                    .list();
            if (materialsList != null && materialsList.size() > 0) {
                throw new MaterialException(EnumMaterials.CATEGORY_HAVE_FILE);
            }
            //3、否则删除
            LambdaUpdateWrapper<MaterialsCategoryEntity> categoryEntityWrapper = Wrappers.lambdaUpdate(MaterialsCategoryEntity.class);
            categoryEntityWrapper.set(MaterialsCategoryEntity::getDeleted, true)
                    .eq(MaterialsCategoryEntity::getMaterialsCategoryId, materialsCategoryId)
                    .eq(MaterialsCategoryEntity::getAppId, appId);
            materialsCategoryService.update(categoryEntityWrapper);
            count++;
        }
        return count;
    }

    /**
     * @param
     * @return
     * @说明: 排序 资料类别信息列表
     * @关联表: MaterialsCategory
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月25日 下午15:18:46
     */
    @DSTransactional
    public Boolean sortCaterialsCategoryList(List<MaterialsCategoryRequest> list) {
        List<MaterialsCategoryEntity> entities = new ArrayList<>();
        list.forEach(materialsCategoryRequest -> {
            MaterialsCategoryEntity entity = new MaterialsCategoryEntity();
            BeanUtils.copyProperties(materialsCategoryRequest,entity);
            //1、根据分布式键找到主键
            MaterialsCategoryEntity materialsCategoryEntity = materialsCategoryService.lambdaQuery()
                    .eq(MaterialsCategoryEntity::getAppId, materialsCategoryRequest.getAppId())
                    .eq(MaterialsCategoryEntity::getMaterialsCategoryId, materialsCategoryRequest.getMaterialsCategoryId())
                    .oneOpt()
                    .orElseThrow(() -> new MaterialException(EnumMaterials.CATEGORY_IS_NOT_FIND));
            entity.setId(materialsCategoryEntity.getId());
            entities.add(entity);
        });
        //2、批量更新
        return materialsCategoryService.updateBatchById(entities);
    }

}
