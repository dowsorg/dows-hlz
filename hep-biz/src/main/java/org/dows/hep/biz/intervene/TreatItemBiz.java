package org.dows.hep.biz.intervene;

import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindTreatRequest;
import org.dows.hep.api.intervene.response.TreatItemResponse;
import org.dows.hep.api.intervene.response.TreatItemInfoResponse;
import org.dows.hep.api.intervene.request.SaveTreatItmeRequest;
import org.dows.hep.api.intervene.request.DelTreatItemRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:干预:治疗项目
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
public class TreatItemBiz{
    /**
    * @param
    * @return
    * @说明: 获取治疗项目列表
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
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
    * @创建时间: 2023年4月14日 上午10:19:59
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
    * @创建时间: 2023年4月14日 上午10:19:59
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
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public Boolean delTreatItem(DelTreatItemRequest delTreatItem ) {
        return Boolean.FALSE;
    }
}