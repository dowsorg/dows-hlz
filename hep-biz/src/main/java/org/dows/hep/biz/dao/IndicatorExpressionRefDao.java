package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorExpressionRefEntity;
import org.dows.hep.service.IndicatorExpressionRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/6/12 15:57
 */
@Component
public class IndicatorExpressionRefDao extends BaseDao<IndicatorExpressionRefService, IndicatorExpressionRefEntity> {
    public IndicatorExpressionRefDao(){
        super("表达式关联主体不存在");

    }

    @Autowired
    protected IndicatorExpressionDao indicatorExpressionDao;
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

    @Transactional(rollbackFor = Exception.class)
    public boolean tranDeleteByExpressionId(List<String> ids){
        AssertUtil.falseThenThrow(delByExpressionId(ids,false))
                .throwMessage("表达式不存在");
        indicatorExpressionDao.tranDelete(ids,true);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranDeleteByReasonId(List<String> ids){
        List<String> expressionIds=ShareUtil.XCollection.map(this.getByReasonId(null,ids,IndicatorExpressionRefEntity::getIndicatorExpressionId),
                IndicatorExpressionRefEntity::getIndicatorExpressionId);
        if(ShareUtil.XObject.isEmpty(expressionIds)){
            return true;
        }
        AssertUtil.falseThenThrow(delByReasonId(ids,false))
                .throwMessage("表达式关联主体不存在");
        indicatorExpressionDao.tranDelete(expressionIds,true);
        return true;
    }


    //endregion

    //region retrieve
    public List<IndicatorExpressionRefEntity> getByReasonId(String appId,String reasonId,SFunction<IndicatorExpressionRefEntity,?>... cols){
        if (ShareUtil.XObject.isEmpty(reasonId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId),IndicatorExpressionRefEntity::getAppId,appId)
                .eq(IndicatorExpressionRefEntity::getReasonId,reasonId)
                .select(cols)
                .list();
    }
    public List<IndicatorExpressionRefEntity> getByReasonId(String appId,Collection<String> reasonIds,SFunction<IndicatorExpressionRefEntity,?>... cols){
        if (ShareUtil.XObject.isEmpty(reasonIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag=reasonIds.size()==1;
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId),IndicatorExpressionRefEntity::getAppId,appId)
                .eq(oneFlag, IndicatorExpressionRefEntity::getReasonId,reasonIds.iterator().next())
                .in(!oneFlag, IndicatorExpressionRefEntity::getReasonId,reasonIds)
                .select(cols)
                .list();
    }
    //endregion

    //region save
    public boolean updateReasonId(String reasonId, Collection<String> expressionIds) {
        if(ShareUtil.XObject.isEmpty(expressionIds)){
            return true;
        }
        final boolean oneFlag = expressionIds.size() == 1;
        return service.update(Wrappers.<IndicatorExpressionRefEntity>lambdaUpdate()
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

    //region delete
    public boolean delByReasonId(String reasonId,boolean dftIfEmpty){
        if (ShareUtil.XObject.isEmpty(reasonId)) {
            return dftIfEmpty;
        }
        return service.remove(Wrappers.<IndicatorExpressionRefEntity>lambdaQuery()
                .eq(IndicatorExpressionRefEntity::getReasonId,reasonId));
    }
    public boolean delByReasonId(List<String> ids,boolean dftIfEmpty){
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return service.remove(Wrappers.<IndicatorExpressionRefEntity>lambdaQuery()
                .eq(oneFlag, IndicatorExpressionRefEntity::getReasonId, ids.get(0))
                .in(!oneFlag, IndicatorExpressionRefEntity::getReasonId, ids));
    }
    public boolean delByExpressionId(List<String> ids, boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return service.remove(Wrappers.<IndicatorExpressionRefEntity>lambdaQuery()
                .eq(oneFlag, IndicatorExpressionRefEntity::getIndicatorExpressionId, ids.get(0))
                .in(!oneFlag, IndicatorExpressionRefEntity::getIndicatorExpressionId, ids));
    }

    //endregion


}
