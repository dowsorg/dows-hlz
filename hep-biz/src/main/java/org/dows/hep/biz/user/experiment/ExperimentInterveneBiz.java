package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.InterveneInfoResponse;
import org.dows.hep.api.user.experiment.response.InterveneResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:实验:健康干预
*
* @author lait.zhang
* @date 2023年4月18日 下午1:41:27
*/
@Service
public class ExperimentInterveneBiz{
    /**
    * @param
    * @return
    * @说明: 保存饮食干预记录
    * @关联表: OperateIntervene,OperateIndicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 下午1:41:27
    */
    public Boolean saveInterveneFood(SaveInterveneFoodRequest saveInterveneFood ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 保存运动干预记录
    * @关联表: OperateIntervene,OperateIndicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 下午1:41:27
    */
    public Boolean saveInterveneSport(SaveInterveneSportRequest saveInterveneSport ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 保存治疗干预记录
    * @关联表: OperateIntervene,OperateIndicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 下午1:41:27
    */
    public Boolean saveInterveneTreat(SaveInterveneTreatRequest saveInterveneTreat ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取干预记录列表
    * @关联表: OperateIntervene
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 下午1:41:27
    */
    public List<InterveneResponse> listIntervene(ListInterveneRequest listIntervene ) {
        return new ArrayList<InterveneResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 获取干预记录信息
    * @关联表: OperateIntervene
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 下午1:41:27
    */
    public InterveneInfoResponse getIntervene(GetInterveneRequest getIntervene ) {
        return new InterveneInfoResponse();
    }
}