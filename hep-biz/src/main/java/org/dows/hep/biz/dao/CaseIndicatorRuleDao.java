package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.api.enums.EnumIndicatorRuleType;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CaseIndicatorRuleEntity;
import org.dows.hep.service.CaseIndicatorRuleService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author : wuzl
 * @date : 2023/5/18 19:49
 */
@Component
public class CaseIndicatorRuleDao extends BaseDao<CaseIndicatorRuleService, CaseIndicatorRuleEntity>  {

    public CaseIndicatorRuleDao(){
       super("案例指标规则不存在或已删除","案例指标规则保存失败");
    }

    @Override
    protected SFunction<CaseIndicatorRuleEntity, String> getColId() {
        return CaseIndicatorRuleEntity::getCaseIndicatorRuleId;
    }

    @Override
    protected SFunction<String, ?> setColId(CaseIndicatorRuleEntity item) {
        return item::setCaseIndicatorRuleId;
    }

    @Override
    protected SFunction<CaseIndicatorRuleEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(CaseIndicatorRuleEntity item) {
        return null;
    }


    public List<CaseIndicatorRuleEntity> getByIndicatorIds(Collection<String> indicatorIds, SFunction<CaseIndicatorRuleEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(indicatorIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=indicatorIds.size()==1;
        return service.lambdaQuery()
                .eq(CaseIndicatorRuleEntity::getRuleType, EnumIndicatorRuleType.INDICATOR.getCode())
                .eq(oneFlag, CaseIndicatorRuleEntity::getVariableId,indicatorIds.iterator().next())
                .in(!oneFlag, CaseIndicatorRuleEntity::getVariableId,indicatorIds)
                .select(cols)
                .list();
    }

    public boolean updateIndicatorDef(String indicatorId,String def){
        return service.lambdaUpdate()
                .eq(CaseIndicatorRuleEntity::getVariableId,indicatorId)
                .set(CaseIndicatorRuleEntity::getDef,def)
                .update();
    }

    public boolean delByIndicatorIds(Collection<String> indicatorIds){
        if(ShareUtil.XObject.isEmpty(indicatorIds)){
            return false;
        }
        return service.lambdaUpdate()
                .eq(indicatorIds.size()==1,CaseIndicatorRuleEntity::getVariableId,indicatorIds.iterator().next())
                .in(indicatorIds.size()>1, CaseIndicatorRuleEntity::getVariableId,indicatorIds)
                .remove();
    }





}
