package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CasePersonEntity;
import org.dows.hep.service.CasePersonService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/6 13:53
 */
@Component
public class CasePersonDao extends BaseDao<CasePersonService, CasePersonEntity> {

    public CasePersonDao() {
        super("案例人物不存在");
    }


    @Override
    protected SFunction<CasePersonEntity, String> getColId() {
        return CasePersonEntity::getCasePersonId;
    }

    @Override
    protected SFunction<String, ?> setColId(CasePersonEntity item) {
        return item::setCasePersonId;
    }

    @Override
    protected SFunction<CasePersonEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(CasePersonEntity item) {
        return null;
    }

    public List<CasePersonEntity> getByAccountIds(Collection<String> accountIds,SFunction<CasePersonEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(accountIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=accountIds.size()==1;
        return service.lambdaQuery()
                .eq(oneFlag, CasePersonEntity::getAccountId,accountIds.iterator().next())
                .in(!oneFlag, CasePersonEntity::getAccountId,accountIds)
                .select(cols)
                .list();
    }
    public List<CasePersonEntity> getByPersonIds(Collection<String> personIds, SFunction<CasePersonEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(personIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=personIds.size()==1;
        return service.lambdaQuery()
                .eq(oneFlag, CasePersonEntity::getCasePersonId,personIds.iterator().next())
                .in(!oneFlag, CasePersonEntity::getCasePersonId,personIds)
                .select(cols)
                .list();
    }

}
