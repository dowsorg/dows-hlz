package org.dows.hep.biz.user.organization;

import org.dows.hep.api.user.organization.request.AgeRatioRequest;
import org.dows.hep.api.user.organization.request.GenderRatioRequest;
import org.dows.hep.api.user.organization.response.NormalDataResponse;
import org.dows.hep.api.user.organization.response.NormalDataResponseResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:机构:机构数据统计
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class OrgStatiscBiz{
    /**
    * @param
    * @return
    * @说明: 获取性别分类
    * @关联表: AccountInstance、IndicatorInstance、IndicatorPrincipalRef
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public NormalDataResponseResponse listGenderRatio(GenderRatioRequest genderRatio ) {
        return new NormalDataResponseResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取年龄分类
    * @关联表: AccountInstance、IndicatorInstance、IndicatorPrincipalRef
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public NormalDataResponse listAgeRatio(AgeRatioRequest ageRatio ) {
        return new NormalDataResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取标签分类？？？
    * @关联表: 
    * @工时: 0H
    * @开发者: 
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void listTagRatio() {
        
    }
}