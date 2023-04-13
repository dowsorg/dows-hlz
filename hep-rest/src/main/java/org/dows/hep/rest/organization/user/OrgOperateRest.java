package org.dows.hep.rest.organization.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.organization.user.request.PersonQueryRequest;
import org.dows.hep.api.organization.user.response.PersonInstanceResponse;
import org.dows.hep.api.organization.user.response.OrganizationFunsResponse;
import org.dows.hep.api.organization.user.request.CaseOrgFeeRequest;
import org.dows.hep.api.organization.user.request.TransferPersonelRequest;
import org.dows.hep.biz.organization.user.OrgOperateBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:机构:机构操作
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "机构操作")
public class OrgOperateRest {
    private final OrgOperateBiz orgOperateBiz;

    /**
    * 获取机构组员列表[人物档案]
    * @param
    * @return
    */
    @ApiOperation("获取机构组员列表[人物档案]")
    @GetMapping("v1/organizationUser/orgOperate/listPerson")
    public PersonInstanceResponse listPerson(@Validated PersonQueryRequest personQuery) {
        return orgOperateBiz.listPerson(personQuery);
    }

    /**
    * 列出机构功能
    * @param
    * @return
    */
    @ApiOperation("列出机构功能")
    @GetMapping("v1/organizationUser/orgOperate/listOrgFunc")
    public List<OrganizationFunsResponse> listOrgFunc(@Validated String orgId) {
        return orgOperateBiz.listOrgFunc(orgId);
    }

    /**
    * 列出机构费用
    * @param
    * @return
    */
    @ApiOperation("列出机构费用")
    @GetMapping("v1/organizationUser/orgOperate/listOrgFee")
    public Boolean listOrgFee(@Validated CaseOrgFeeRequest caseOrgFee) {
        return orgOperateBiz.listOrgFee(caseOrgFee);
    }

    /**
    * 转移人员
    * @param
    * @return
    */
    @ApiOperation("转移人员")
    @PutMapping("v1/organizationUser/orgOperate/transferPerson")
    public Boolean transferPerson(@Validated TransferPersonelRequest transferPersonel ) {
        return orgOperateBiz.transferPerson(transferPersonel);
    }


}