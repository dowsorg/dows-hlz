package org.dows.hep.rest.casus.tenant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.casus.tenant.request.CaseSettingRequest;
import org.dows.hep.api.casus.tenant.response.CaseSettingResponse;
import org.dows.hep.biz.casus.tenant.CaseSettingBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案例:案例问卷设置
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "案例问卷设置")
public class CaseSettingRest {
    private final CaseSettingBiz caseSettingBiz;

    /**
    * 新增和更新案例问卷设置
    * @param
    * @return
    */
    @ApiOperation("新增和更新案例问卷设置")
    @PostMapping("v1/casusTenant/caseSetting/saveOrUpdCaseSetting")
    public Boolean saveOrUpdCaseSetting(@RequestBody @Validated CaseSettingRequest caseSetting ) {
        return caseSettingBiz.saveOrUpdCaseSetting(caseSetting);
    }

    /**
    * 获取案例问卷设置
    * @param
    * @return
    */
    @ApiOperation("获取案例问卷设置")
    @GetMapping("v1/casusTenant/caseSetting/getCaseSetting")
    public CaseSettingResponse getCaseSetting(@Validated String caseInstanceId) {
        return caseSettingBiz.getCaseSetting(caseInstanceId);
    }

    /**
    * 删除案例问卷设置
    * @param
    * @return
    */
    @ApiOperation("删除案例问卷设置")
    @DeleteMapping("v1/casusTenant/caseSetting/delCaseSetting")
    public Boolean delCaseSetting(@Validated String caseInstanceId ) {
        return caseSettingBiz.delCaseSetting(caseInstanceId);
    }


}