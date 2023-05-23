package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.request.FindEventRequest;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.EventActionEntity;
import org.dows.hep.entity.EventActionIndicatorEntity;
import org.dows.hep.entity.EventEntity;
import org.dows.hep.entity.EventEvalEntity;
import org.dows.hep.service.EventEvalService;
import org.dows.hep.service.EventService;
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
public class EventDao extends BaseSubDao<EventService, EventEntity, EventEvalService, EventEvalEntity>
    implements IPageDao<EventEntity, FindEventRequest>,ICheckCategRef  {

    public EventDao(){
        super("突发事件不存在或已删除,请刷新");
    }

    @Autowired
    protected EventActionDao subDao;


    //region override

    @Override
    protected SFunction<EventEntity, String> getColId() {
        return EventEntity::getEventId;
    }

    @Override
    protected SFunction<String, ?> setColId(EventEntity item) {
        return item::setEventId;
    }

    @Override
    protected SFunction<EventEntity, Integer> getColState() {
        return EventEntity::getState;
    }

    @Override
    protected SFunction<Integer, ?> setColState(EventEntity item) {
        return item::setState;
    }

    @Override
    protected SFunction<EventEntity,String> getColCateg(){
        return EventEntity::getEventCategId;
    }

    @Override
    protected SFunction<EventEvalEntity, String> getColLeadId() {
        return EventEvalEntity::getEventId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(EventEvalEntity item) {
        return item::setEventId;
    }

    @Override
    protected SFunction<EventEvalEntity, String> getColSubId() {
        return EventEvalEntity::getEventEvalId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(EventEvalEntity item) {
        return item::setEventEvalId;
    }



    //endregion



    @Override
    public IPage<EventEntity> pageByCondition(FindEventRequest req, SFunction<EventEntity, ?>... cols) {
        final String categId = req.getCategIdLv1();
        final String keyWords = req.getKeywords();
        Page<EventEntity> page = Page.of(req.getPageNo(), req.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return service.page(page, Wrappers.<EventEntity>lambdaQuery()
                .likeRight(ShareUtil.XString.hasLength(categId), EventEntity::getCategIdPath, categId)
                .like(ShareUtil.XString.hasLength(keyWords), EventEntity::getEventName, keyWords)
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .eq(ShareUtil.XObject.notEmpty(req.getTriggerType()), EventEntity::getTriggerType, req.getTriggerType())
                .select(cols));
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(EventEntity lead, List<EventEvalEntity> subs, LinkedHashMap<EventActionEntity,List<EventActionIndicatorEntity>> subsX) {
        AssertUtil.falseThenThrow(coreTranSave(lead, subs, subsX, false, true))
                .throwMessage(failedSaveMessage);
        return true;
    }

    //region save

    protected boolean coreTranSave(EventEntity lead, List<EventEvalEntity> subs, LinkedHashMap<EventActionEntity,List<EventActionIndicatorEntity>> subsX, boolean delSubBefore,  boolean useLogicId) {
        if (!super.coreTranSave(lead, subs, delSubBefore, useLogicId)) {
            return false;
        }
        return subDao.saveOrUpdateBatch(lead.getEventId(),subsX,useLogicId,true);
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
