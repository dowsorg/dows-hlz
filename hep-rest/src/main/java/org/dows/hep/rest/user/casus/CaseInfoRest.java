package org.dows.hep.rest.user.casus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.user.casus.request.CaseInfoRequest;
import org.dows.hep.biz.user.casus.CaseInfoBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案列:案例基础信息
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例基础信息", description = "案例基础信息")
public class CaseInfoRest {
    private final CaseInfoBiz caseInfoBiz;

    /**
    * 获取案例基础信息[背景|帮助中心|评分提示]
    * @param
    * @return
    */
    @Operation(summary = "获取案例基础信息[背景|帮助中心|评分提示]")
    @GetMapping("v1/userCasus/caseInfo/getCaseInfo")
    public String getCaseInfo(@Validated CaseInfoRequest caseInfo) {
        return caseInfoBiz.getCaseInfo(caseInfo);
    }


}