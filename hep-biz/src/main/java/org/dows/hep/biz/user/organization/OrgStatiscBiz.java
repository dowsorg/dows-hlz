package org.dows.hep.biz.user.organization;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.user.organization.request.AgeRatioRequest;
import org.dows.hep.api.user.organization.request.GenderRatioRequest;
import org.dows.hep.api.user.organization.response.CaseOrgResponse;
import org.dows.hep.api.user.organization.response.NormalDataResponse;
import org.dows.hep.api.user.organization.response.NormalDataResponseResponse;
import org.dows.hep.entity.CaseOrgEntity;
import org.dows.hep.entity.ExperimentOrgEntity;
import org.dows.hep.service.CaseOrgService;
import org.dows.hep.service.ExperimentOrgService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @description project descr:机构:机构数据统计
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class OrgStatiscBiz{

    private final CaseOrgService caseOrgService;

    private final ExperimentOrgService experimentOrgService;
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

    /**
     * @param
     * @return
     * @说明: 获取机构操作手册
     * @关联表: case_org
     * @工时: 0.5H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月08日 下午17:19:34
     */
    public CaseOrgResponse getOrgHandbook(String experimentInstanceId) {
        //1、根据实验找到案例机构ID
        List<ExperimentOrgEntity> experimentOrgList = experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentOrgEntity::getDeleted, false)
                .list();
        CaseOrgResponse orgResponse = new CaseOrgResponse();
        //2、获取操作手册
        if (experimentOrgList != null && experimentOrgList.size() > 0) {
            CaseOrgEntity entity = caseOrgService.lambdaQuery()
                    .eq(CaseOrgEntity::getDeleted, false)
                    .eq(CaseOrgEntity::getCaseOrgId, experimentOrgList.get(0).getCaseOrgId())
                    .one();
            if (entity != null && !ReflectUtil.isObjectNull(entity)) {
                BeanUtil.copyProperties(entity, orgResponse);
            }
        }
        return orgResponse;
    }
}