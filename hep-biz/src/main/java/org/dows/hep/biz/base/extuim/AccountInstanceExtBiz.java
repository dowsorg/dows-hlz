package org.dows.hep.biz.base.extuim;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dows.account.entity.AccountInstance;
import org.dows.account.service.AccountInstanceService;
import org.springframework.stereotype.Service;

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
     * 分页获取,人物管理列表
     * source 为 人物管理
     * accountIds
     */
    public IPage<AccountInstance> getAccountInstancePages(String source, long pageNo, long pageSize) {
        if (StringUtils.isBlank(source)) {
            source = "人物管理";
        }
        LambdaQueryWrapper<AccountInstance> accountWrapper = new LambdaQueryWrapper<>();
        accountWrapper
                .eq(AccountInstance::getSource, source)
                .orderByDesc(AccountInstance::getDt);
        Page<AccountInstance> page = new Page<>(pageNo, pageSize);
        IPage<AccountInstance> resultPage = accountInstanceService.page(page, accountWrapper);
        return resultPage;
    }

}
