package org.dows.hep.biz.base.extuim;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.account.entity.AccountInstance;
import org.dows.account.service.AccountInstanceService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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

}
