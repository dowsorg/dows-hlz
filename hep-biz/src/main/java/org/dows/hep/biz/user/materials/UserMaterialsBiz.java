package org.dows.hep.biz.user.materials;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.materials.request.MaterialsPageRequest;
import org.dows.hep.api.base.materials.response.MaterialsPageResponse;
import org.dows.hep.api.base.materials.response.MaterialsResponse;
import org.dows.hep.biz.base.materials.MaterialsManageBiz;
import org.springframework.stereotype.Service;

/**
* @description project descr:资料中心:资料信息
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@Service
public class UserMaterialsBiz {
    private final MaterialsManageBiz materialsManageBiz;
    /**
    * @param
    * @return
    * @说明: 分页
    * @关联表: Materials,MaterialsAttachment
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Page<MaterialsPageResponse> pageMaterials(MaterialsPageRequest materialsPageRequest ) {
        return materialsManageBiz.pageMaterials(materialsPageRequest);
    }
    /**
    * @param
    * @return
    * @说明: 根据ID获取详情
    * @关联表: Materials,MaterialsAttachment
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public MaterialsResponse getMaterials(String materialsId ) {
        return materialsManageBiz.getMaterials(materialsId);
    }
}