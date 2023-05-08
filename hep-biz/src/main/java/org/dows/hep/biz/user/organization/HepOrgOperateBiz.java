package org.dows.hep.biz.user.organization;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.organization.request.CaseOrgFeeRequest;
import org.dows.hep.api.user.organization.request.OrgPositionRequest;
import org.dows.hep.api.user.organization.request.PersonQueryRequest;
import org.dows.hep.api.user.organization.request.TransferPersonelRequest;
import org.dows.hep.api.user.organization.response.AccountOrgGeoResponse;
import org.dows.hep.api.user.organization.response.OrganizationFunsResponse;
import org.dows.hep.api.user.organization.response.PersonInstanceResponse;
import org.dows.hep.service.ExperimentPersonService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:机构:机构操作
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class HepOrgOperateBiz{

    private final ExperimentPersonService experimentPersonService;
    /**
    * @param
    * @return
    * @说明: 获取机构组员列表[人物档案]
    * @关联表: AccountGroup、AccountUser、UserInstance、IndicatorInstance、IndicatorPrincipalRef、ExperimentPerson，标签表？？
    * @工时: 3H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public PersonInstanceResponse listPerson(PersonQueryRequest personQuery ) {
        return new PersonInstanceResponse();
    }
    /**
    * @param
    * @return
    * @说明: 列出机构功能
    * @关联表: CaseOrgFunction
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<OrganizationFunsResponse> listOrgFunc(String orgId ) {
        return new ArrayList<OrganizationFunsResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 列出机构费用
    * @关联表: caseOrgFee
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean listOrgFee(CaseOrgFeeRequest caseOrgFee ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 转移人员
    * @关联表: OperateTransfers,AccountGroup
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    @DSTransactional
    public Boolean transferPerson(TransferPersonelRequest request) {
      //1、根据案例机构ID和账户ID查找用户
      return false;
    }
    /**
    * @param
    * @return
    * @说明: 列出机构位置
    * @关联表: AccountOrgGeo
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public AccountOrgGeoResponse listOrgPosition(OrgPositionRequest orgPosition ) {
        return new AccountOrgGeoResponse();
    }
}