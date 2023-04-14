package org.dows.hep.rest.casus.tenant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.casus.tenant.request.CaseInstanceRequest;
import org.dows.hep.api.casus.tenant.request.CaseInstancePageRequest;
import org.dows.hep.api.casus.tenant.response.CaseInstanceResponse;
import org.dows.hep.api.casus.tenant.response.CaseInstanceResponse;
import org.dows.hep.biz.casus.tenant.CaseManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案例:案例管理
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "案例管理")
public class CaseManageRest {
    private final CaseManageBiz caseManageBiz;

    /**
    * 创建和更新案例
    * @param
    * @return
    */
    @ApiOperation("创建和更新案例")
    @PostMapping("v1/casusTenant/caseManage/saveOrUpdCaseInstance")
    public String saveOrUpdCaseInstance(@RequestBody @Validated CaseInstanceRequest caseInstance ) {
        return caseManageBiz.saveOrUpdCaseInstance(caseInstance);
    }

    /**
    * 复制
    * @param
    * @return
    */
    @ApiOperation("复制")
    @PostMapping("v1/casusTenant/caseManage/copyCaseInstance")
    public String copyCaseInstance(@RequestBody @Validated String oriCaseInstanceId ) {
        return caseManageBiz.copyCaseInstance(oriCaseInstanceId);
    }

    /**
    * 列表
    * @param
    * @return
    */
    @ApiOperation("列表")
    @PostMapping("v1/casusTenant/caseManage/pageCaseInstance")
    public CaseInstanceResponse pageCaseInstance(@RequestBody @Validated CaseInstancePageRequest caseInstancePage ) {
        return caseManageBiz.pageCaseInstance(caseInstancePage);
    }

    /**
    * 获取案例详情
    * @param
    * @return
    */
    @ApiOperation("获取案例详情")
    @GetMapping("v1/casusTenant/caseManage/getCaseInstance")
    public CaseInstanceResponse getCaseInstance(@Validated String caseInstanceId) {
        return caseManageBiz.getCaseInstance(caseInstanceId);
    }

    /**
    * 删除
    * @param
    * @return
    */
    @ApiOperation("删除")
    @DeleteMapping("v1/casusTenant/caseManage/delCaseInstance")
    public Boolean delCaseInstance(@Validated String caseInstanceId ) {
        return caseManageBiz.delCaseInstance(caseInstanceId);
    }


}