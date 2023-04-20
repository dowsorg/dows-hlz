package org.dows.hep.biz.user.organization;

import org.dows.hep.api.user.organization.request.CaseOrgFeeRequest;
import org.dows.hep.api.user.organization.request.OrgPositionRequest;
import org.dows.hep.api.user.organization.request.PersonQueryRequest;
import org.dows.hep.api.user.organization.request.TransferPersonelRequest;
import org.dows.hep.api.user.organization.response.AccountOrgGeoResponse;
import org.dows.hep.api.user.organization.response.OrganizationFunsResponse;
import org.dows.hep.api.user.organization.response.PersonInstanceResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:机构:机构操作
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class HepOrgOperateBiz{
    /**
    * @param
    * @return
    * @说明: 获取机构组员列表[人物档案]
    * @关联表: AccountGroup、AccountUser、UserInstance、
    * @工时: 3H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean transferPerson(TransferPersonelRequest transferPersonel ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 列出机构位置
    * @关联表: AccountOrgGeo
    * @工时: 4H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public AccountOrgGeoResponse listOrgPosition(OrgPositionRequest orgPosition ) {
        return new AccountOrgGeoResponse();
    }
}