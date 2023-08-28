package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.request.FindEventRequest;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.EventActionEntity;
import org.dows.hep.entity.EventEntity;
import org.dows.hep.entity.EventEvalEntity;
import org.dows.hep.service.EventEvalService;
import org.dows.hep.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Autowired
    protected IndicatorExpressionRefDao indicatorExpressionRefDao;


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
        final String keyWords = req.getKeywords();
        Page<EventEntity> page = Page.of(req.getPageNo(), req.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(req.getAppId()), EventEntity::getAppId,req.getAppId())
                .like(ShareUtil.XString.hasLength(keyWords), EventEntity::getEventName, keyWords)
                .in(ShareUtil.XCollection.notEmpty(req.getCategIdLv1()), EventEntity::getEventCategId, req.getCategIdLv1())
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .in(ShareUtil.XObject.notEmpty(req.getTriggerType()), EventEntity::getTriggerType, req.getTriggerType())
                .select(cols)
                .page(page);
    }





    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(EventEntity lead, List<EventActionEntity> actions, Map<String,List<String>> mapExpressions) {
        AssertUtil.falseThenThrow(coreTranSave(lead, actions,  true))
                .throwMessage(failedSaveMessage);
        return indicatorExpressionRefDao.tranUpdateReasonId(mapExpressions);
    }

    //region save


    protected boolean coreTranSave(EventEntity lead, List<EventActionEntity> actions, boolean useLogicId) {
        if (!saveOrUpdate(lead, useLogicId)) {
            return false;
        }
        return subDao.saveOrUpdateBatch(lead.getEventId(), actions,useLogicId,true);
    }


    //endregion



    //region delete
    @Override
    protected boolean coreTranDelete(List<String> ids, boolean delSub, boolean dftIfSubEmpty) {
        List<String> actionIds=delSub?ShareUtil.XCollection.map(subDao.getByEventIds(ids, EventActionEntity::getEventActionId),
                EventActionEntity::getEventActionId): Collections.emptyList();

        if(!super.coreTranDelete(ids, false, dftIfSubEmpty)){
            return false;
        }
        if(!delSub)
            return true;
        subDao.delByEventId(ids,dftIfSubEmpty);
        ids.addAll(actionIds);
        indicatorExpressionRefDao.tranDeleteByReasonId(ids);
        return true;
    }
    //endregion


}
