package org.dows.hep.rest.casus.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.casus.user.request.CaseInfoRequest;
import org.dows.hep.biz.casus.user.CaseInfoBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案列:案例基础信息
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "案例基础信息")
public class CaseInfoRest {
    private final CaseInfoBiz caseInfoBiz;

    /**
    * 获取案例基础信息[背景|帮助中心|评分提示]
    * @param
    * @return
    */
    @ApiOperation("获取案例基础信息[背景|帮助中心|评分提示]")
    @GetMapping("v1/casusUser/caseInfo/getCaseInfo")
    public String getCaseInfo(@Validated CaseInfoRequest caseInfo) {
        return caseInfoBiz.getCaseInfo(caseInfo);
    }


}