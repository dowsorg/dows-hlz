package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:实验:机构操作-操作指标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class ExperimentOrgInterveneBiz{
    /**
    * @param
    * @return
    * @说明: 心理干预+治疗方案：获取分类+项目
    * @关联表: intervene_category,sport_item，treat_item
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<OrgInterveneCategResponse> listOrgInterveneCategs(FindOrgInterveneCategsRequest findOrgInterveneCategs ) {
        return new ArrayList<OrgInterveneCategResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 饮食干预：保存食谱
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SaveOrgInterveneFoodResponse saveOrgInterveneFood(SaveOrgInterveneFoodRequest saveOrgInterveneFood ) {
        return new SaveOrgInterveneFoodResponse();
    }
    /**
    * @param
    * @return
    * @说明: 饮食干预：获取最新食谱
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgInterveneFoodResponse getOrgInterveneFood(FindOrgInterveneFoodRequest findOrgInterveneFood ) {
        return new OrgInterveneFoodResponse();
    }
    /**
    * @param
    * @return
    * @说明: 运动干预：保存运动方案
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SaveOrgInterveneSportResponse saveOrgInterveneSport(SaveOrgInterveneSportRequest saveOrgInterveneSport ) {
        return new SaveOrgInterveneSportResponse();
    }
    /**
    * @param
    * @return
    * @说明: 运动干预：获取最新运动方案
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgInterveneSportResponse getOrgInterveneSport(FindOrgInterveneSportRequest findOrgInterveneSport ) {
        return new OrgInterveneSportResponse();
    }
    /**
    * @param
    * @return
    * @说明: 心理干预+治疗方案：保存，生成诊疗报告
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SaveOrgInterveneTreatResponse saveOrgInterveneTreat(SaveOrgInterveneTreatRequest saveOrgInterveneTreat ) {
        return new SaveOrgInterveneTreatResponse();
    }
    /**
    * @param
    * @return
    * @说明: 心理干预+治疗方案：获取最新保存列表
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgInterveneTreatResponse getOrgInterveneTreat(FindOrgInterveneTreatRequest findOrgInterveneTreat ) {
        return new OrgInterveneTreatResponse();
    }
}