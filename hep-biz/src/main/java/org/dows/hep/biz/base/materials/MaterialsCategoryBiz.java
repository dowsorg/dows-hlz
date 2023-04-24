package org.dows.hep.biz.base.materials;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.materials.request.MaterialsCategoryRequest;
import org.dows.hep.api.enums.EnumMaterials;
import org.dows.hep.api.exception.MaterialException;
import org.dows.hep.entity.MaterialsCategoryEntity;
import org.dows.hep.service.MaterialsCategoryService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/4/24 16:13
 */
@Service
@RequiredArgsConstructor
public class MaterialsCategoryBiz {
    private final MaterialsCategoryService materialsCategoryService;
    private final IdGenerator idGenerator;
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
        //1、判断是否已经存在该类别名称
        materialsCategoryService.lambdaQuery()
                .eq(MaterialsCategoryEntity::getAppId, materials.getAppId())
                .eq(MaterialsCategoryEntity::getCategoryName, materials.getCategoryName())
                .oneOpt()
                .ifPresent((a) -> {
                    throw new MaterialException(EnumMaterials.CATEGORY_NAME_IS_EXIST);
                });
        //2、新增
        MaterialsCategoryEntity model = new MaterialsCategoryEntity();
        BeanUtils.copyProperties(materials,model);
        model.setMaterialsCategoryId(idGenerator.nextIdStr());
        return materialsCategoryService.save(model);
    }
}
