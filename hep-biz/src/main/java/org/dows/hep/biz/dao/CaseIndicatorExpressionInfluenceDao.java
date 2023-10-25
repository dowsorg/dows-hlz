package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CaseIndicatorExpressionInfluenceEntity;
import org.dows.hep.service.CaseIndicatorExpressionInfluenceService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/12 14:37
 */
@Component
public class CaseIndicatorExpressionInfluenceDao extends BaseDao<CaseIndicatorExpressionInfluenceService, CaseIndicatorExpressionInfluenceEntity>  {

    public CaseIndicatorExpressionInfluenceDao(){
        super("指标作用关系不存在或已删除","指标作用关系保存失败");
    }

    @Override
    protected SFunction<CaseIndicatorExpressionInfluenceEntity, String> getColId() {
        return CaseIndicatorExpressionInfluenceEntity::getCaseIndicatorExpressionInfluenceId;
    }

    @Override
    protected SFunction<String, ?> setColId(CaseIndicatorExpressionInfluenceEntity item) {
        return item::setCaseIndicatorExpressionInfluenceId;
    }

    @Override
    protected SFunction<CaseIndicatorExpressionInfluenceEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(CaseIndicatorExpressionInfluenceEntity item) {
        return null;
    }


    public List<CaseIndicatorExpressionInfluenceEntity> getByIndicatorIds(Collection<String> indicatorIds, SFunction<CaseIndicatorExpressionInfluenceEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(indicatorIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag = indicatorIds.size() == 1;
        return service.lambdaQuery()
                .eq(oneFlag, CaseIndicatorExpressionInfluenceEntity::getIndicatorInstanceId, indicatorIds.iterator().next())
                .in(!oneFlag, CaseIndicatorExpressionInfluenceEntity::getIndicatorInstanceId, indicatorIds)
                .orderByAsc(CaseIndicatorExpressionInfluenceEntity::getId)
                .select(cols)
                .list();
    }

    public boolean delByIndicatorIds(Collection<String> indicatorIds){
        if(ShareUtil.XObject.isEmpty(indicatorIds)){
            return false;
        }
        return service.lambdaUpdate()
                .eq(indicatorIds.size()==1, CaseIndicatorExpressionInfluenceEntity::getIndicatorInstanceId,indicatorIds.iterator().next())
                .in(indicatorIds.size()>1, CaseIndicatorExpressionInfluenceEntity::getIndicatorInstanceId,indicatorIds)
                .remove();
    }






}
