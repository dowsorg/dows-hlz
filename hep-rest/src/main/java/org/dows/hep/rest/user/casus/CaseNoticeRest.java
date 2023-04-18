package org.dows.hep.rest.user.casus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.casus.request.CaseNoticeSearchRequest;
import org.dows.hep.api.user.casus.response.CaseNoticeResponse;
import org.dows.hep.biz.user.casus.CaseNoticeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:案列:案例公告
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例公告", description = "案例公告")
public class CaseNoticeRest {
    private final CaseNoticeBiz caseNoticeBiz;

    /**
    * 获取案例公告
    * @param
    * @return
    */
    @Operation(summary = "获取案例公告")
    @PostMapping("v1/userCasus/caseNotice/getCaseNotice")
    public CaseNoticeResponse getCaseNotice(@RequestBody @Validated CaseNoticeSearchRequest caseNoticeSearch ) {
        return caseNoticeBiz.getCaseNotice(caseNoticeSearch);
    }


}