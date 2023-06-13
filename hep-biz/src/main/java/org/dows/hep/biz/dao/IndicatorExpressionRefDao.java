package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorExpressionRefEntity;
import org.dows.hep.service.IndicatorExpressionRefService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/6/12 15:57
 */
@Component
public class IndicatorExpressionRefDao extends BaseDao<IndicatorExpressionRefService, IndicatorExpressionRefEntity> {
    public IndicatorExpressionRefDao(){
        super("指标公式关联不存在");

    }

    @Override
    protected SFunction<IndicatorExpressionRefEntity, String> getColId() {
        return IndicatorExpressionRefEntity::getIndicatorExpressionRefId;
    }

    @Override
    protected SFunction<String, ?> setColId(IndicatorExpressionRefEntity item) {
        return item::setIndicatorExpressionRefId;
    }

    @Override
    protected SFunction<IndicatorExpressionRefEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(IndicatorExpressionRefEntity item) {
        return null;
    }
    //region tran
    protected String failUpdateReasonIdMessage="表达式绑定失败";
    @Transactional(rollbackFor = Exception.class)
    public boolean tranUpdateReasonId(String reasonId, Collection<String> expressionIds) {
        AssertUtil.falseThenThrow(updateReasonId(reasonId,expressionIds))
                .throwMessage(failUpdateReasonIdMessage);
        return true;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean tranUpdateReasonId(Map<String, List<String>> mapReasonId) {
        AssertUtil.falseThenThrow(updateReasonId(mapReasonId))
                .throwMessage(failUpdateReasonIdMessage);
        return true;
    }
    //endregion

    //region save
    public boolean updateReasonId(String reasonId, Collection<String> expressionIds) {
        if(ShareUtil.XObject.isEmpty(expressionIds)){
            return true;
        }
        final boolean oneFlag = expressionIds.size() == 1;
        return service.update(service.lambdaUpdate()
                .set(IndicatorExpressionRefEntity::getReasonId, reasonId)
                .eq(oneFlag, IndicatorExpressionRefEntity::getIndicatorExpressionId, expressionIds.iterator().next())
                .in(!oneFlag, IndicatorExpressionRefEntity::getIndicatorExpressionId, expressionIds));
    }
    public boolean updateReasonId(Map<String, List<String>> mapReasonId){
        if(ShareUtil.XObject.isEmpty(mapReasonId)){
            return true;
        }
        boolean rst=true;
        for(Map.Entry<String,List<String>> item: mapReasonId.entrySet()){
            rst&=updateReasonId(item.getKey(),item.getValue());
        }
        return rst;
    }
    //endregion
}
