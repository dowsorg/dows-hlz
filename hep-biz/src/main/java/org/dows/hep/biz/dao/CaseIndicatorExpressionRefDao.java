package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.CaseIndicatorExpressionRefService;
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
public class CaseIndicatorExpressionRefDao extends BaseDao<CaseIndicatorExpressionRefService, CaseIndicatorExpressionRefEntity> {
    public CaseIndicatorExpressionRefDao(){
        super("表达式关联主体不存在");

    }

    @Autowired
    protected CaseIndicatorExpressionDao caseIndicatorExpressionDao;
    @Override
    protected SFunction<CaseIndicatorExpressionRefEntity, String> getColId() {
        return CaseIndicatorExpressionRefEntity::getCaseIndicatorExpressionRefId;
    }

    @Override
    protected SFunction<String, ?> setColId(CaseIndicatorExpressionRefEntity item) {
        return item::setCaseIndicatorExpressionRefId;
    }

    @Override
    protected SFunction<CaseIndicatorExpressionRefEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(CaseIndicatorExpressionRefEntity item) {
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
        caseIndicatorExpressionDao.tranDelete(ids,true);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranDeleteByReasonId(List<String> ids){
        List<String> expressionIds=ShareUtil.XCollection.map(this.getByReasonId(null,ids,CaseIndicatorExpressionRefEntity::getIndicatorExpressionId),
                CaseIndicatorExpressionRefEntity::getIndicatorExpressionId);

        if(ShareUtil.XObject.isEmpty(expressionIds)){
            return true;
        }
        AssertUtil.falseThenThrow(delByReasonId(ids,false))
                .throwMessage("表达式关联主体不存在");
        caseIndicatorExpressionDao.tranDelete(expressionIds,true);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranSaveBatch(List<CaseIndicatorExpressionRefEntity> expressionRefs, List<CaseIndicatorExpressionEntity> expressions, List<CaseIndicatorExpressionItemEntity> expressionItems) {
        this.saveOrUpdateBatch(expressionRefs,true,true);
        caseIndicatorExpressionDao.tranSaveBatch(expressions,expressionItems,true,true);
        return true;
    }


    //endregion

    //region retrieve
    public List<CaseIndicatorExpressionRefEntity> getByReasonId(String appId,String reasonId,SFunction<CaseIndicatorExpressionRefEntity,?>... cols){
        if (ShareUtil.XObject.isEmpty(reasonId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId),CaseIndicatorExpressionRefEntity::getAppId,appId)
                .eq(CaseIndicatorExpressionRefEntity::getReasonId,reasonId)
                .select(cols)
                .list();
    }
    public List<CaseIndicatorExpressionRefEntity> getByReasonId(String appId,Collection<String> reasonIds,SFunction<CaseIndicatorExpressionRefEntity,?>... cols){
        if (ShareUtil.XObject.isEmpty(reasonIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag=reasonIds.size()==1;
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId),CaseIndicatorExpressionRefEntity::getAppId,appId)
                .eq(oneFlag, CaseIndicatorExpressionRefEntity::getReasonId,reasonIds.iterator().next())
                .in(!oneFlag, CaseIndicatorExpressionRefEntity::getReasonId,reasonIds)
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
        return service.update(Wrappers.<CaseIndicatorExpressionRefEntity>lambdaUpdate()
                .set(CaseIndicatorExpressionRefEntity::getReasonId, reasonId)
                .eq(oneFlag, CaseIndicatorExpressionRefEntity::getIndicatorExpressionId, expressionIds.iterator().next())
                .in(!oneFlag, CaseIndicatorExpressionRefEntity::getIndicatorExpressionId, expressionIds));
    }
    public boolean updateReasonId(Map<String, List<String>> mapReasonId){
        if(ShareUtil.XObject.isEmpty(mapReasonId)){
            return true;
        }
        boolean rst=true;
        for(Map.Entry<String,List<String>> item: mapReasonId.entrySet()) {
            rst &= updateReasonId(item.getKey(), item.getValue());
        }
        return rst;
    }
    //endregion

    //region delete
    public boolean delByReasonId(String reasonId,boolean dftIfEmpty){
        if (ShareUtil.XObject.isEmpty(reasonId)) {
            return dftIfEmpty;
        }
        return service.remove(Wrappers.<CaseIndicatorExpressionRefEntity>lambdaQuery()
                .eq(CaseIndicatorExpressionRefEntity::getReasonId,reasonId));
    }
    public boolean delByReasonId(Collection<String> ids,boolean dftIfEmpty){
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return service.remove(Wrappers.<CaseIndicatorExpressionRefEntity>lambdaQuery()
                .eq(oneFlag, CaseIndicatorExpressionRefEntity::getReasonId, ids.iterator().next())
                .in(!oneFlag, CaseIndicatorExpressionRefEntity::getReasonId, ids));
    }
    public boolean delByExpressionId(List<String> ids, boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return service.remove(Wrappers.<CaseIndicatorExpressionRefEntity>lambdaQuery()
                .eq(oneFlag, CaseIndicatorExpressionRefEntity::getIndicatorExpressionId, ids.get(0))
                .in(!oneFlag, CaseIndicatorExpressionRefEntity::getIndicatorExpressionId, ids));
    }

    //endregion


}
