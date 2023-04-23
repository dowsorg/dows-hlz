package org.dows.hep.biz.base.org;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.account.api.AccountGroupApi;
import org.dows.account.api.AccountGroupInfoApi;
import org.dows.account.api.AccountOrgApi;
import org.dows.account.request.AccountGroupInfoRequest;
import org.dows.account.request.AccountGroupRequest;
import org.dows.account.request.AccountOrgRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

/**
 * @author jx
 * @date 2023/4/21 17:12
 */
@Service
@RequiredArgsConstructor
public class OrgBiz {
    private final AccountOrgApi accountOrgApi;
    private final AccountGroupInfoApi accountGroupInfoApi;
    private final AccountGroupApi accountGroupApi;

    @DSTransactional
    public String createClass(AccountOrgRequest request,String accountId) {
        //1、生成随机code
        String orgCode = createCode(5);
        request.setOrgCode(orgCode);
        //2、创建机构
        String orgId = accountOrgApi.createAccountOrg(request);
        //3、创建团队组员
        request.setOrgId(orgId);
        String groupId = accountGroupApi.insertAccountGroup(AccountGroupRequest.builder()
                .accountId(accountId)
                .orgId(orgId)
                .appId(request.getAppId())
                .build());
        //4、创建团队负责人
        String groupInfoId = accountGroupInfoApi.insertAccountGroupInfo(AccountGroupInfoRequest.builder().
                accountId(accountId).
                orgId(orgId).
                appId(request.getAppId())
                .build());
        return orgId;
}

    public static String createCode(int n) {
        Random r = new Random();
        String code = "";
        for (int i = 0; i < n; i++) {
            int type = r.nextInt(3);  // 0  1  2
            switch (type) {
                case 0:
                    // 大写英文 A 65  Z 90
                    char ch = (char) (r.nextInt(25) + 65);
                    code += ch;
                    break;
                case 1:
                    // 小写英文 A 97 Z 122
                    char ch1 = (char) (r.nextInt(25) + 97);
                    code += ch1;
                    break;
                case 2:
                    // 数字
                    int ch2 = r.nextInt(10);
                    code += ch2;
                    break;
            }
        }
        return code;
    }
}
