package org.dows.hep.biz.user.materials;

import org.dows.hep.api.user.materials.request.MaterialsSearchRequest;
import org.dows.hep.api.user.materials.request.QuestionSearchRequest;
import org.dows.hep.api.user.materials.response.MaterialsResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:资料中心:资料信息
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class MaterialsBiz{
    /**
    * @param
    * @return
    * @说明: 分页
    * @关联表: Materials,MaterialsAttachment
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public MaterialsResponse getMaterials(String materialsId ) {
        return new MaterialsResponse();
    }
}