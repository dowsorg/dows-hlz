package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

import java.util.LinkedHashMap;
import java.util.List;

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
        return service.page(page, Wrappers.<CaseEventEntity>lambdaQuery()
                .eq(CaseEventEntity::getPersonId, req.getPersonId())
                //.likeRight(ShareUtil.XString.hasLength(categId), CaseEventEntity::getCategIdPath, categId)
                .like(ShareUtil.XString.hasLength(keyWords), CaseEventEntity::getCaseEventName, keyWords)
                .in(ShareUtil.XCollection.notEmpty(req.getCategIdLv1()), CaseEventEntity::getEventCategId, req.getCategIdLv1())
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .eq(ShareUtil.XObject.notEmpty(req.getTriggerType()), CaseEventEntity::getTriggerType, req.getTriggerType())
                .select(cols));
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(CaseEventEntity lead, List<CaseEventEvalEntity> subs, LinkedHashMap<CaseEventActionEntity,List<CaseEventActionIndicatorEntity>> subsX) {
        AssertUtil.falseThenThrow(coreTranSave(lead, subs, subsX, false, true))
                .throwMessage(failedSaveMessage);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranSaveBatch(List<CaseEventEntity> events, List<CaseEventEvalEntity> evals,  List<CaseEventActionEntity> actions,List<CaseEventActionIndicatorEntity> indicators ) {
        this.tranSaveBatch(events,evals,false);
        subDao.tranSaveBatch(actions,indicators,true);
        return true;
    }

    //region save

    protected boolean coreTranSave(CaseEventEntity lead, List<CaseEventEvalEntity> subs, LinkedHashMap<CaseEventActionEntity,List<CaseEventActionIndicatorEntity>> subsX, boolean delSubBefore,  boolean useLogicId) {
        if (!super.coreTranSave(lead, subs, delSubBefore, useLogicId)) {
            return false;
        }
        return subDao.saveOrUpdateBatch(lead.getCaseEventId(),subsX,useLogicId,true);
    }

    //endregion



    //region delete
    @Override
    protected boolean coreTranDelete(List<String> ids, boolean delSub, boolean dftIfSubEmpty) {
        if(!super.coreTranDelete(ids, delSub, dftIfSubEmpty)){
            return false;
        }
        if(!delSub)
            return true;
        if(!subDao.delByEventId(ids,dftIfSubEmpty) ){
            return false;
        }
        subDao.delSubByEventId(ids,dftIfSubEmpty);
        return true;
    }
    //endregion


}
