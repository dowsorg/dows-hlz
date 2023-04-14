package org.dows.hep.biz.organization.user;

import org.dows.framework.api.Response;
import org.dows.hep.api.organization.user.response.NormalDataResponseResponse;
import org.dows.hep.api.organization.user.response.NormalDataResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:机构:机构数据统计
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
public class OrgStatiscBiz{
    /**
    * @param
    * @return
    * @说明: 获取性别分类
    * @关联表: AccountInstance、IndicatorInstance、IndicatorPrincipalRef
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public NormalDataResponseResponse listGenderRatio(String orgId ) {
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
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public NormalDataResponse listAgeRatio(String orgId ) {
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
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public void listTagRatio() {
        
    }
}