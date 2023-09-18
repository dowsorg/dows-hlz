package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CaseIndicatorInstanceEntity;
import org.dows.hep.service.CaseIndicatorInstanceService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/6/15 9:59
 */
@Component
public class CaseIndicatorInstanceDao extends BaseDao<CaseIndicatorInstanceService, CaseIndicatorInstanceEntity>  {
    public CaseIndicatorInstanceDao(){
        super("人物指标不存在");
    }

    @Override
    protected SFunction<CaseIndicatorInstanceEntity, String> getColId() {
        return CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId;
    }

    @Override
    protected SFunction<String, ?> setColId(CaseIndicatorInstanceEntity item) {
        return item::setCaseIndicatorInstanceId;
    }

    @Override
    protected SFunction<CaseIndicatorInstanceEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(CaseIndicatorInstanceEntity item) {
        return null;
    }

    /**
     * 获取人物指标列表
     * @param appId
     * @param personId
     * @param cols
     * @return
     */
    public List<CaseIndicatorInstanceEntity> getByPersonId(String appId,String personId,SFunction<CaseIndicatorInstanceEntity,?>...cols){
        if(ShareUtil.XObject.isEmpty(personId)){
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(CaseIndicatorInstanceEntity::getAppId,appId)
                .eq(CaseIndicatorInstanceEntity::getPrincipalId,personId)
                .select(cols)
                .list();
    }

    public List<CaseIndicatorInstanceEntity> getByAccountIds(Collection<String> accountIds, SFunction<CaseIndicatorInstanceEntity,?>...cols){
        if(ShareUtil.XObject.isEmpty(accountIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=accountIds.size()==1;
        return service.lambdaQuery()
                .eq(oneFlag, CaseIndicatorInstanceEntity::getPrincipalId,accountIds.iterator().next())
                .in(!oneFlag, CaseIndicatorInstanceEntity::getPrincipalId,accountIds)
                .orderByAsc(getColId())
                .select(cols)
                .list();
    }
}
