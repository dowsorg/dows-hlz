package org.dows.hep.biz.base.person;

import lombok.RequiredArgsConstructor;
import org.dows.account.api.AccountInstanceApi;
import org.dows.account.request.AccountInstanceRequest;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * @author jx
 * @date 2023/4/20 13:18
 */
@Service
@RequiredArgsConstructor
public class PersonBiz {
    private final AccountInstanceApi accountInstanceApi;
    /**
     * @param
     * @return
     * @说明: 登录
     * @关联表: account_instance、account_group、account_org、account_role
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/20 13:20
     */
    public Map<String, Object> login(AccountInstanceRequest request) {
        return accountInstanceApi.login(request);
    }
}
