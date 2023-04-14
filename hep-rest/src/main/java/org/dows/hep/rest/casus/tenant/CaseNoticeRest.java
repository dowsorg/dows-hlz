package org.dows.hep.rest.casus.tenant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.casus.tenant.request.CreateCaseNoticeRequest;
import org.dows.hep.api.casus.tenant.response.CaseNoticeResponse;
import org.dows.hep.biz.casus.tenant.CaseNoticeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案例:案例公告
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "案例公告")
public class CaseNoticeRest {
    private final CaseNoticeBiz caseNoticeBiz;

    /**
    * 新增和更新案例公告
    * @param
    * @return
    */
    @ApiOperation("新增和更新案例公告")
    @PostMapping("v1/casusTenant/caseNotice/saveOrUpdCaseNotice")
    public Boolean saveOrUpdCaseNotice(@RequestBody @Validated CreateCaseNoticeRequest createCaseNotice ) {
        return caseNoticeBiz.saveOrUpdCaseNotice(createCaseNotice);
    }

    /**
    * 列出案例公告
    * @param
    * @return
    */
    @ApiOperation("列出案例公告")
    @GetMapping("v1/casusTenant/caseNotice/listCaseNotice")
    public List<CaseNoticeResponse> listCaseNotice(@Validated String caseInstanceId) {
        return caseNoticeBiz.listCaseNotice(caseInstanceId);
    }

    /**
    * 删除案例公告
    * @param
    * @return
    */
    @ApiOperation("删除案例公告")
    @DeleteMapping("v1/casusTenant/caseNotice/delCaseNotice")
    public Boolean delCaseNotice(@Validated String caseNoticeId ) {
        return caseNoticeBiz.delCaseNotice(caseNoticeId);
    }


}