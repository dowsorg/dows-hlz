package org.dows.hep.rest.user.organization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.organization.request.AgeRatioRequest;
import org.dows.hep.api.user.organization.request.GenderRatioRequest;
import org.dows.hep.api.user.organization.response.NormalDataResponse;
import org.dows.hep.api.user.organization.response.NormalDataResponseResponse;
import org.dows.hep.biz.user.organization.OrgStatiscBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:机构:机构数据统计
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "机构数据统计", description = "机构数据统计")
public class OrgStatiscRest {
    private final OrgStatiscBiz orgStatiscBiz;

    /**
    * 获取性别分类
    * @param
    * @return
    */
    @Operation(summary = "获取性别分类")
    @GetMapping("v1/userOrganization/orgStatisc/listGenderRatio")
    public NormalDataResponseResponse listGenderRatio(@Validated GenderRatioRequest genderRatio) {
        return orgStatiscBiz.listGenderRatio(genderRatio);
    }

    /**
    * 获取年龄分类
    * @param
    * @return
    */
    @Operation(summary = "获取年龄分类")
    @GetMapping("v1/userOrganization/orgStatisc/listAgeRatio")
    public NormalDataResponse listAgeRatio(@Validated AgeRatioRequest ageRatio) {
        return orgStatiscBiz.listAgeRatio(ageRatio);
    }

    /**
    * 获取标签分类？？？
    * @param
    * @return
    */
    @Operation(summary = "获取标签分类？？？")
    @GetMapping("v1/userOrganization/orgStatisc/listTagRatio")
    public void listTagRatio() {
        orgStatiscBiz.listTagRatio();
    }


}