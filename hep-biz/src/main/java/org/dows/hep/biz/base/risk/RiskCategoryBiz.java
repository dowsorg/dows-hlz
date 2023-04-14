package org.dows.hep.biz.base.risk;

import org.dows.framework.api.Response;
import org.dows.hep.api.base.risk.request.CreateRiskCategoryRequest;
import org.dows.hep.api.base.risk.request.UpdateRiskCategoryRequest;
import org.dows.hep.api.base.risk.response.RiskCategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:风险:风险类别
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class RiskCategoryBiz{
    /**
    * @param
    * @return
    * @说明: 创建风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void createRiskCategory(CreateRiskCategoryRequest createRiskCategory ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void deleteRiskCategory(String riskCategoryId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更改风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void updateRiskCategory(UpdateRiskCategoryRequest updateRiskCategory ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查询风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public RiskCategoryResponse getRiskCategory(String riskCategoryId ) {
        return new RiskCategoryResponse();
    }
}