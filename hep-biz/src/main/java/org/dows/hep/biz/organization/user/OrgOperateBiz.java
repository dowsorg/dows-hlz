package org.dows.hep.biz.organization.user;

import org.dows.framework.api.Response;
import org.dows.hep.api.organization.user.request.PersonQueryRequest;
import org.dows.hep.api.organization.user.response.PersonInstanceResponse;
import org.dows.hep.api.organization.user.response.OrganizationFunsResponse;
import org.dows.hep.api.organization.user.request.CaseOrgFeeRequest;
import org.dows.hep.api.organization.user.request.TransferPersonelRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:机构:机构操作
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
public class OrgOperateBiz{
    /**
    * @param
    * @return
    * @说明: 获取机构组员列表[人物档案]
    * @关联表: AccountGroup、AccountUser、UserInstance、
    * @工时: 3H
    * @开发者: jx
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean transferPerson(TransferPersonelRequest transferPersonel ) {
        return Boolean.FALSE;
    }
}