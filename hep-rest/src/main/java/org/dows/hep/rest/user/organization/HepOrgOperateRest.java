package org.dows.hep.rest.user.organization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.user.organization.request.PersonQueryRequest;
import org.dows.hep.api.user.organization.response.PersonInstanceResponse;
import org.dows.hep.api.user.organization.response.OrganizationFunsResponse;
import org.dows.hep.api.user.organization.request.CaseOrgFeeRequest;
import org.dows.hep.api.user.organization.request.TransferPersonelRequest;
import org.dows.hep.biz.user.organization.HepOrgOperateBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:机构:机构操作
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "机构操作", description = "机构操作")
public class HepOrgOperateRest {
    private final HepOrgOperateBiz hepOrgOperateBiz;

    /**
    * 获取机构组员列表[人物档案]
    * @param
    * @return
    */
    @Operation(summary = "获取机构组员列表[人物档案]")
    @GetMapping("v1/userOrganization/hepOrgOperate/listPerson")
    public PersonInstanceResponse listPerson(@Validated PersonQueryRequest personQuery) {
        return hepOrgOperateBiz.listPerson(personQuery);
    }

    /**
    * 列出机构功能
    * @param
    * @return
    */
    @Operation(summary = "列出机构功能")
    @GetMapping("v1/userOrganization/hepOrgOperate/listOrgFunc")
    public List<OrganizationFunsResponse> listOrgFunc(@Validated String orgId) {
        return hepOrgOperateBiz.listOrgFunc(orgId);
    }

    /**
    * 列出机构费用
    * @param
    * @return
    */
    @Operation(summary = "列出机构费用")
    @GetMapping("v1/userOrganization/hepOrgOperate/listOrgFee")
    public Boolean listOrgFee(@Validated CaseOrgFeeRequest caseOrgFee) {
        return hepOrgOperateBiz.listOrgFee(caseOrgFee);
    }

    /**
    * 转移人员
    * @param
    * @return
    */
    @Operation(summary = "转移人员")
    @PutMapping("v1/userOrganization/hepOrgOperate/transferPerson")
    public Boolean transferPerson(@Validated TransferPersonelRequest transferPersonel ) {
        return hepOrgOperateBiz.transferPerson(transferPersonel);
    }


}