package org.dows.hep.rest.base.org;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.request.AccountOrgRequest;
import org.dows.hep.biz.base.org.OrgBiz;
import org.dows.hep.biz.base.person.PersonBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author jx
 * @date 2023/4/21 17:09
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "机构", description = "机构")
public class OrgRest {
    private final OrgBiz orgBiz;
    /**
     * 创建班级
     * @param
     * @return
     */
    @Operation(summary = "创建班级")
    @PostMapping("v1/baseOrg/org/createClass")
    public String createClass(@RequestBody AccountOrgRequest request, @RequestParam String accountId) {
        return orgBiz.createClass(request,accountId);
    }
}
