package org.dows.hep.biz.base.materials;

import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.api.base.materials.request.MaterialsSearchRequest;
import org.dows.hep.api.base.materials.request.QuestionSearchRequest;
import org.dows.hep.api.base.materials.response.MaterialsResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:资料中心:资料信息
*
* @author lait.zhang
* @date 2023年4月21日 上午10:26:46
*/
@Service
public class MaterialsBiz{
    /**
    * @param
    * @return
    * @说明: 新增和更新资料信息
    * @关联表: Materials,MaterialsAttachment
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月21日 上午10:26:46
    */
    public String saveOrUpdMaterials(MaterialsRequest materials ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 分页
    * @关联表: Materials,MaterialsAttachment
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月21日 上午10:26:46
    */
    public MaterialsResponse pageMaterials(MaterialsSearchRequest materialsSearch ) {
        return new MaterialsResponse();
    }
    /**
    * @param
    * @return
    * @说明: 条件查询-无分页
    * @关联表: Materials,MaterialsAttachment
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月21日 上午10:26:46
    */
    public MaterialsResponse listMaterials(QuestionSearchRequest questionSearch ) {
        return new MaterialsResponse();
    }
    /**
    * @param
    * @return
    * @说明: 根据ID获取详情
    * @关联表: Materials,MaterialsAttachment
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月21日 上午10:26:46
    */
    public MaterialsResponse getMaterials(String materialsId ) {
        return new MaterialsResponse();
    }
    /**
    * @param
    * @return
    * @说明: 启用
    * @关联表: Materials,MaterialsAttachment
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月21日 上午10:26:46
    */
    public Boolean enabledMaterials(String materialsId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 禁用
    * @关联表: Materials,MaterialsAttachment
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月21日 上午10:26:46
    */
    public Boolean disabledMaterials(String materialsId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除or批量删除
    * @关联表: Materials,MaterialsAttachment
    * @工时: 6H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月21日 上午10:26:46
    */
    public Boolean delMaterials(String materialsId ) {
        return Boolean.FALSE;
    }
}