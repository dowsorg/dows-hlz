package org.dows.hep.biz.extend.uim;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.RequiredArgsConstructor;
import org.dows.account.entity.AccountInstance;
import org.dows.account.service.AccountInstanceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/10 17:44
 */

@Service
@DS("uim")
@RequiredArgsConstructor
public class XAccountInstanceApi {

    private final AccountInstanceService accountInstanceService;

    public List<AccountInstance> getAccountInstancesBySource(String source, SFunction<AccountInstance,?>...cols) {

        return accountInstanceService.lambdaQuery()
                .eq(AccountInstance::getSource, source)
                .orderByAsc(AccountInstance::getId)
                .select(cols)
                .list();

    }

}
