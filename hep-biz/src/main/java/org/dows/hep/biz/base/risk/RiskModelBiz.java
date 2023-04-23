package org.dows.hep.biz.base.risk;

import org.dows.hep.api.base.risk.request.CreateRiskModelRequest;
import org.dows.hep.api.base.risk.request.UpdateRiskModelRequest;
import org.dows.hep.api.base.risk.request.UpdateStatusRiskModelRequest;
import org.dows.hep.api.base.risk.response.RiskModelResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:风险:风险模型
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class RiskModelBiz{
    /**
    * @param
    * @return
    * @说明: 创建风险模型
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createRiskModel(CreateRiskModelRequest createRiskModel ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除风险模型
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteRiskModel(String riskModelId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更改风险模型
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateRiskModel(UpdateRiskModelRequest updateRiskModel ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更改启用状态
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateStatusRiskModel(UpdateStatusRiskModelRequest updateStatusRiskModel ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取风险模型
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public RiskModelResponse getRiskModel(String riskModelId ) {
        return new RiskModelResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选风险模型
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<RiskModelResponse> listRiskModel(String appId, String riskModelId, String modelName, Integer status ) {
        return new ArrayList<RiskModelResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选风险模型
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageRiskModel(Integer pageNo, Integer pageSize, String appId, String riskModelId, String modelName, Integer status ) {
        return new String();
    }
}