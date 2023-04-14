package org.dows.hep.biz.intervene;

import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.intervene.response.InterveneCategResponse;
import org.dows.hep.api.intervene.request.SaveInterveneCategRequest;
import org.dows.hep.api.intervene.request.DelInterveneCategRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:干预:干预类别管理
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
public class InterveneCategBiz{
    /**
    * @param
    * @return
    * @说明: 获取类别
    * @关联表: intervene_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public List<InterveneCategResponse> listInterveneCateg(FindInterveneCategRequest findInterveneCateg ) {
        return new ArrayList<InterveneCategResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 保存类别
    * @关联表: intervene_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public Boolean saveInterveneCateg(SaveInterveneCategRequest saveInterveneCateg ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除类别
    * @关联表: intervene_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public Boolean delInterveneCateg(DelInterveneCategRequest delInterveneCateg ) {
        return Boolean.FALSE;
    }
}