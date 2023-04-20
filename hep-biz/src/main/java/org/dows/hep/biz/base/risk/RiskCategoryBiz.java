package org.dows.hep.biz.base.risk;

import org.dows.hep.api.base.risk.request.CreateRiskCategoryRequest;
import org.dows.hep.api.base.risk.request.UpdateRiskCategoryRequest;
import org.dows.hep.api.base.risk.response.RiskCategoryResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:风险:风险类别
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public void updateRiskCategory(UpdateRiskCategoryRequest updateRiskCategory ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public RiskCategoryResponse getRiskCategory(String riskCategoryId ) {
        return new RiskCategoryResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<RiskCategoryResponse> listRiskCategory(String appId, String riskCategoryName ) {
        return new ArrayList<RiskCategoryResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String pageRiskCategory(Integer pageNo, Integer pageSize, String appId, String riskCategoryName ) {
        return new String();
    }
}