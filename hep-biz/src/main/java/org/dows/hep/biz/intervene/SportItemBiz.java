package org.dows.hep.biz.intervene;

import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindSportRequest;
import org.dows.hep.api.intervene.response.SportItemResponse;
import org.dows.hep.api.intervene.response.SportItemInfoResponse;
import org.dows.hep.api.intervene.request.SaveSportItemRequest;
import org.dows.hep.api.intervene.request.DelSpotItemRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:干预:运动项目
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
public class SportItemBiz{
    /**
    * @param
    * @return
    * @说明: 获取运动项目列表
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:31:43
    */
    public SportItemResponse pageSportItem(FindSportRequest findSport ) {
        return new SportItemResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取运动项目详细信息
    * @关联表: sport_item,sport_item_indicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:31:43
    */
    public SportItemInfoResponse getSportItem(String sportItemId ) {
        return new SportItemInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 保存运动项目
    * @关联表: sport_item,sport_item_indicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:31:43
    */
    public Boolean saveSportItem(SaveSportItemRequest saveSportItem ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除运动项目
    * @关联表: sport_item,sport_item_indicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:31:43
    */
    public Boolean delSportItem(DelSpotItemRequest delSpotItem ) {
        return Boolean.FALSE;
    }
}