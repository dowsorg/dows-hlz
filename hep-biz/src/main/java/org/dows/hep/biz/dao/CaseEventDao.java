package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.tenant.casus.request.FindCaseEventRequest;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.CaseEventEvalService;
import org.dows.hep.service.CaseEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/5/11 12:00
 */

@Component
public class CaseEventDao extends BaseSubDao<CaseEventService, CaseEventEntity, CaseEventEvalService, CaseEventEvalEntity>
    implements IPageDao<CaseEventEntity, FindCaseEventRequest>,ICheckCategRef  {

    public CaseEventDao(){
        super("突发事件不存在或已删除,请刷新");
    }

    @Autowired
    protected CaseEventActionDao subDao;

    @Autowired
    protected CaseIndicatorExpressionRefDao caseIndicatorExpressionRefDao;




    //region override

    @Override
    protected SFunction<CaseEventEntity, String> getColId() {
        return CaseEventEntity::getCaseEventId;
    }

    @Override
    protected SFunction<String, ?> setColId(CaseEventEntity item) {
        return item::setCaseEventId;
    }

    @Override
    protected SFunction<CaseEventEntity, Integer> getColState() {
        return CaseEventEntity::getState;
    }

    @Override
    protected SFunction<Integer, ?> setColState(CaseEventEntity item) {
        return item::setState;
    }

    @Override
    protected SFunction<CaseEventEntity,String> getColCateg(){
        return CaseEventEntity::getEventCategId;
    }

    @Override
    protected SFunction<CaseEventEvalEntity, String> getColLeadId() {
        return CaseEventEvalEntity::getCaseEventId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(CaseEventEvalEntity item) {
        return item::setCaseEventId;
    }

    @Override
    protected SFunction<CaseEventEvalEntity, String> getColSubId() {
        return CaseEventEvalEntity::getCaseEventEvalId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(CaseEventEvalEntity item) {
        return item::setCaseEventEvalId;
    }



    //endregion



    @Override
    public IPage<CaseEventEntity> pageByCondition(FindCaseEventRequest req, SFunction<CaseEventEntity, ?>... cols) {
        final String keyWords = req.getKeywords();
        Page<CaseEventEntity> page = Page.of(req.getPageNo(), req.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(req.getAppId()), CaseEventEntity::getAppId,req.getAppId())
                .eq(CaseEventEntity::getPersonId, req.getPersonId())
                .like(ShareUtil.XString.hasLength(keyWords), CaseEventEntity::getCaseEventName, keyWords)
                .in(ShareUtil.XCollection.notEmpty(req.getCategIdLv1()), CaseEventEntity::getEventCategId, req.getCategIdLv1())
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .in(ShareUtil.XObject.notEmpty(req.getTriggerType()), CaseEventEntity::getTriggerType, req.getTriggerType())
                .select(cols)
                .page(page);
    }





    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(CaseEventEntity lead, List<CaseEventActionEntity> actions, Map<String,List<String>> mapExpressions) {
        AssertUtil.falseThenThrow(coreTranSave(lead, actions,  true))
                .throwMessage(failedSaveMessage);
        return caseIndicatorExpressionRefDao.tranUpdateReasonId(mapExpressions);

    }


    @Transactional(rollbackFor = Exception.class)
    public boolean tranSaveBatch(List<CaseEventEntity> events,List<CaseEventActionEntity> actions,
                                 List<CaseIndicatorExpressionRefEntity> expressionRefs, List<CaseIndicatorExpressionEntity> expressions, List<CaseIndicatorExpressionItemEntity> expressionItems) {
        this.tranSaveBatch(events,true,false);
        subDao.tranSaveBatch(actions,null,true,true);
        caseIndicatorExpressionRefDao.tranSaveBatch(expressionRefs,expressions,expressionItems);
        return true;
    }

    //region save


    protected boolean coreTranSave(CaseEventEntity lead, List<CaseEventActionEntity> actions, boolean useLogicId) {
        if (!saveOrUpdate(lead, useLogicId)) {
            return false;
        }
        return subDao.saveOrUpdateBatch(lead.getCaseEventId(), actions,useLogicId,true);
    }

    //endregion



    //region delete
    @Override
    protected boolean coreTranDelete(List<String> ids, boolean delSub, boolean dftIfSubEmpty) {
        List<String> actionIds=delSub?ShareUtil.XCollection.map(subDao.getByEventIds(ids, CaseEventActionEntity::getCaseEventActionId),
                CaseEventActionEntity::getCaseEventActionId): Collections.emptyList();
        if(!super.coreTranDelete(ids, false, dftIfSubEmpty)){
            return false;
        }
        if(!delSub)
            return true;
        subDao.delByEventId(ids,dftIfSubEmpty);
        ids.addAll(actionIds);
        caseIndicatorExpressionRefDao.tranDeleteByReasonId(ids);
        return true;
    }
    //endregion

    public List<CaseEventEntity> getCaseEventsByPersonIds(Collection<String> personIds, SFunction<CaseEventEntity,?>...cols){
        if(ShareUtil.XObject.isEmpty(personIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=personIds.size()==1;
        return service.lambdaQuery()
                .eq(oneFlag, CaseEventEntity::getPersonId,personIds.iterator().next())
                .in(!oneFlag, CaseEventEntity::getPersonId,personIds)
                .select(cols)
                .list();
    }

    public List<CaseEventEntity> getByCaseInstanceId(String caseInstanceId,SFunction<CaseEventEntity,?>...cols){
        return service.lambdaQuery()
                .eq(CaseEventEntity::getCaseInstanceId, caseInstanceId)
                .select(cols)
                .list();
    }


}
