package org.dows.hep.biz.base.intervene;

import org.dows.hep.api.base.intervene.request.DelTreatItemRequest;
import org.dows.hep.api.base.intervene.request.FindTreatRequest;
import org.dows.hep.api.base.intervene.request.SaveTreatItmeRequest;
import org.dows.hep.api.base.intervene.response.TreatItemInfoResponse;
import org.dows.hep.api.base.intervene.response.TreatItemResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:干预:治疗项目
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class TreatItemBiz{
    /**
    * @param
    * @return
    * @说明: 获取治疗项目列表
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public TreatItemResponse pageTreatItem(FindTreatRequest findTreat ) {
        return new TreatItemResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取治疗项目信息
    * @关联表: treat_item,treat_item_indicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public TreatItemInfoResponse infoTreatItem(String treatItemId ) {
        return new TreatItemInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 保存治疗项目
    * @关联表: treat_item,treat_item_indicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveTreatItem(SaveTreatItmeRequest saveTreatItme ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除治疗项目
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delTreatItem(DelTreatItemRequest delTreatItem ) {
        return Boolean.FALSE;
    }
}