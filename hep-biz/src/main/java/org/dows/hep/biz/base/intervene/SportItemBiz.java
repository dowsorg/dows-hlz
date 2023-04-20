package org.dows.hep.biz.base.intervene;

import org.dows.hep.api.base.intervene.request.DelSpotItemRequest;
import org.dows.hep.api.base.intervene.request.FindSportRequest;
import org.dows.hep.api.base.intervene.request.SaveSportItemRequest;
import org.dows.hep.api.base.intervene.response.SportItemInfoResponse;
import org.dows.hep.api.base.intervene.response.SportItemResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:干预:运动项目
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class SportItemBiz{
    /**
    * @param
    * @return
    * @说明: 获取运动项目列表
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean delSportItem(DelSpotItemRequest delSpotItem ) {
        return Boolean.FALSE;
    }
}