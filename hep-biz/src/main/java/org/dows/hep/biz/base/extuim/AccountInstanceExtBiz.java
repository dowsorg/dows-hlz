package org.dows.hep.biz.base.extuim;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dows.account.entity.AccountInstance;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.service.AccountInstanceService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * uim拓展服务
 *
 * @description: lifel 2023/10/8
 */
@Service
@RequiredArgsConstructor
public class AccountInstanceExtBiz {

    private final AccountInstanceService accountInstanceService;

    /**
     * 分页获取,不重复的 accountIds
     */
    public Set<String> getAccountInstanceList(long pageNo, long pageSize) {
        Set<String> accountIds = new HashSet<>();
        LambdaQueryWrapper<AccountInstance> accountWrapper = new LambdaQueryWrapper<>();
        accountWrapper.orderByDesc(AccountInstance::getDt);
        Page<AccountInstance> page = new Page<>(pageNo, pageSize);
        IPage<AccountInstance> resultPage = accountInstanceService.page(page, accountWrapper);
        resultPage.getRecords().forEach(accountInstance -> {
            accountIds.add(accountInstance.getAccountId());
        });
        return accountIds;
    }

    public List<AccountInstanceResponse> getAccountInstanceList1(AccountInstanceRequest request) {
        List<AccountInstance> voList =
                this.accountInstanceService.lambdaQuery()
                        .like(StringUtils.isNotEmpty(request.getAccountId()), AccountInstance::getAccountId, request.getAccountId())
                        .like(StringUtils.isNotEmpty(request.getAccountName()), AccountInstance::getAccountName, request.getAccountName())
                        .eq(StringUtils.isNotEmpty(request.getSource()), AccountInstance::getSource, request.getSource())
                        .like(StringUtils.isNotEmpty(request.getPhone()), AccountInstance::getPhone, request.getPhone())
                        .eq(StringUtils.isNotEmpty(request.getAppId()), AccountInstance::getAppId, request.getAppId())
                        .eq(request.getStatus() != null, AccountInstance::getStatus, request.getStatus())
                        .eq(request.getDt() != null, AccountInstance::getDt, request.getDt())
                        .gt(request.getStartTime() != null, AccountInstance::getDt, request.getStartTime())
                        .lt(request.getEndTime() != null, AccountInstance::getDt, request.getEndTime())
                        .orderByDesc(AccountInstance::getDt)
                        .list();
        List<AccountInstanceResponse> list = new ArrayList<>();
        voList.forEach((vo) -> {
            AccountInstanceResponse model = new AccountInstanceResponse();
            BeanUtils.copyProperties(vo, model);
            model.setId(vo.getId().toString());
            list.add(model);
        });

        return list;
    }
}
