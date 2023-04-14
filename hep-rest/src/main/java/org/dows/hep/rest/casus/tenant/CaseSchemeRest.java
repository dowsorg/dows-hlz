package org.dows.hep.rest.casus.tenant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.casus.tenant.request.CaseSchemeRequest;
import org.dows.hep.api.casus.tenant.request.CaseSchemePageRequest;
import org.dows.hep.api.casus.tenant.response.CaseSchemeResponse;
import org.dows.hep.api.casus.tenant.response.CaseSchemeResponse;
import org.dows.hep.api.casus.tenant.request.CaseSchemeSearchRequest;
import org.dows.hep.api.casus.tenant.response.CaseSchemeResponse;
import org.dows.hep.api.casus.tenant.response.CaseSchemeResponse;
import org.dows.hep.biz.casus.tenant.CaseSchemeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案例:案例方案设计
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "案例方案设计")
public class CaseSchemeRest {
    private final CaseSchemeBiz caseSchemeBiz;

    /**
    * 新增和更新方案设计
    * @param
    * @return
    */
    @ApiOperation("新增和更新方案设计")
    @PostMapping("v1/casusTenant/caseScheme/saveOrUpdCaseScheme")
    public Boolean saveOrUpdCaseScheme(@RequestBody @Validated CaseSchemeRequest caseScheme ) {
        return caseSchemeBiz.saveOrUpdCaseScheme(caseScheme);
    }

    /**
    * 分页案例方案
    * @param
    * @return
    */
    @ApiOperation("分页案例方案")
    @PostMapping("v1/casusTenant/caseScheme/pageCaseScheme")
    public List<CaseSchemeResponse> pageCaseScheme(@RequestBody @Validated CaseSchemePageRequest caseSchemePage ) {
        return caseSchemeBiz.pageCaseScheme(caseSchemePage);
    }

    /**
    * 获取案例方案
    * @param
    * @return
    */
    @ApiOperation("获取案例方案")
    @GetMapping("v1/casusTenant/caseScheme/getCaseScheme")
    public CaseSchemeResponse getCaseScheme(@Validated String caseSchemeId) {
        return caseSchemeBiz.getCaseScheme(caseSchemeId);
    }

    /**
    * 启用案例方案
    * @param
    * @return
    */
    @ApiOperation("启用案例方案")
    @GetMapping("v1/casusTenant/caseScheme/enabledCaseScheme")
    public Boolean enabledCaseScheme(@Validated String caseSchemeId) {
        return caseSchemeBiz.enabledCaseScheme(caseSchemeId);
    }

    /**
    * 禁用案例方案
    * @param
    * @return
    */
    @ApiOperation("禁用案例方案")
    @GetMapping("v1/casusTenant/caseScheme/disabledCaseScheme")
    public Boolean disabledCaseScheme(@Validated String caseSchemeId) {
        return caseSchemeBiz.disabledCaseScheme(caseSchemeId);
    }

    /**
    * 删除or批量删除案例方案
    * @param
    * @return
    */
    @ApiOperation("删除or批量删除案例方案")
    @DeleteMapping("v1/casusTenant/caseScheme/delCaseScheme")
    public Boolean delCaseScheme(@Validated String caseSchemeIds ) {
        return caseSchemeBiz.delCaseScheme(caseSchemeIds);
    }

    /**
    * 
    * @param
    * @return
    */
    @ApiOperation("")
    @PostMapping("v1/casusTenant/caseScheme/listC")
    public List<CaseSchemeResponse> listC(@RequestBody @Validated CaseSchemeSearchRequest caseSchemeSearch ) {
        return caseSchemeBiz.listC(caseSchemeSearch);
    }

    /**
    * 
    * @param
    * @return
    */
    @ApiOperation("")
    @GetMapping("v1/casusTenant/caseScheme/getC")
    public CaseSchemeResponse getC(@Validated String caseInstanceId) {
        return caseSchemeBiz.getC(caseInstanceId);
    }

    /**
    * 
    * @param
    * @return
    */
    @ApiOperation("")
    @DeleteMapping("v1/casusTenant/caseScheme/delC")
    public Boolean delC(@Validated String caseInstanceId ) {
        return caseSchemeBiz.delC(caseInstanceId);
    }


}