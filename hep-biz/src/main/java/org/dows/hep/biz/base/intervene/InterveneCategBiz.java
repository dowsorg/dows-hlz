package org.dows.hep.biz.base.intervene;

import org.dows.hep.api.base.intervene.request.DelInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.SaveInterveneCategRequest;
import org.dows.hep.api.base.intervene.response.InterveneCategResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:干预:干预类别管理
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class InterveneCategBiz{
    /**
    * @param
    * @return
    * @说明: 获取类别
    * @关联表: intervene_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveInterveneCateg(SaveInterveneCategRequest saveInterveneCateg ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 批量保存类别
    * @关联表: intervene_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveInterveneCategs(List<SaveInterveneCategRequest> saveInterveneCateg ) {
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
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delInterveneCateg(DelInterveneCategRequest delInterveneCateg ) {
        return Boolean.FALSE;
    }
}