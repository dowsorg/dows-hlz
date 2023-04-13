package org.dows.hep.rest.organization.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.organization.user.response.NormalDataResponseResponse;
import org.dows.hep.api.organization.user.response.NormalDataResponse;
import org.dows.hep.biz.organization.user.OrgStatiscBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:机构:机构数据统计
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "机构数据统计")
public class OrgStatiscRest {
    private final OrgStatiscBiz orgStatiscBiz;

    /**
    * 获取性别分类
    * @param
    * @return
    */
    @ApiOperation("获取性别分类")
    @GetMapping("v1/organizationUser/orgStatisc/listGenderRatio")
    public NormalDataResponseResponse listGenderRatio(@Validated String orgId) {
        return orgStatiscBiz.listGenderRatio(orgId);
    }

    /**
    * 获取年龄分类
    * @param
    * @return
    */
    @ApiOperation("获取年龄分类")
    @GetMapping("v1/organizationUser/orgStatisc/listGenderRatio")
    public NormalDataResponse listGenderRatio(@Validated String orgId) {
        return orgStatiscBiz.listGenderRatio(orgId);
    }

    /**
    * 获取标签分类？？？
    * @param
    * @return
    */
    @ApiOperation("获取标签分类？？？")
    @GetMapping("v1/organizationUser/orgStatisc/listTagRatio")
    public void listTagRatio() {
        orgStatiscBiz.listTagRatio();
    }


}