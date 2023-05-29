package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.EventActionEntity;
import org.dows.hep.entity.EventActionIndicatorEntity;
import org.dows.hep.service.EventActionIndicatorService;
import org.dows.hep.service.EventActionService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/5/15 10:06
 */
@Component
public class EventActionDao extends BaseSubDao<EventActionService, EventActionEntity, EventActionIndicatorService, EventActionIndicatorEntity> {

    protected EventActionDao() {
        super("处理措施不存在或已删除，请刷新");
    }

    @Override
    protected SFunction<EventActionEntity, String> getColCateg() {
        return null;
    }

    @Override
    protected SFunction<EventActionEntity, String> getColId() {
        return EventActionEntity::getEventActionId;
    }

    @Override
    protected SFunction<String, ?> setColId(EventActionEntity item) {
        return item::setEventActionId;
    }

    @Override
    protected SFunction<EventActionEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(EventActionEntity item) {
        return null;
    }

    @Override
    protected SFunction<EventActionIndicatorEntity, String> getColLeadId() {
        return EventActionIndicatorEntity::getEventActionId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(EventActionIndicatorEntity item) {
        return item::setEventActionId;
    }

    @Override
    protected SFunction<EventActionIndicatorEntity, String> getColSubId() {
        return EventActionIndicatorEntity::getEventActionIndicatorId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(EventActionIndicatorEntity item) {
        return item::setEventActionIndicatorId;
    }

    @Override
    protected SFunction<EventActionIndicatorEntity, Integer> getColSubSeq() {
        return EventActionIndicatorEntity::getSeq;
    }

    @Override
    protected SFunction<Integer, ?> setColSubSeq(EventActionIndicatorEntity item) {
        return null;
    }


    //region save
    public boolean saveOrUpdateBatch(String eventId, LinkedHashMap<EventActionEntity,List<EventActionIndicatorEntity>> actions, boolean useLogicId, boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(actions)) {
            return dftIfEmpty;
        }
        EventActionEntity kItem;
        List<EventActionIndicatorEntity> vItem;
        String leadId;
        int vSeq=0;
        List<EventActionIndicatorEntity> rowsSub=new ArrayList<>();
        for(Map.Entry<EventActionEntity,List<EventActionIndicatorEntity>> item:actions.entrySet()) {
            kItem = item.getKey();
            vItem = item.getValue();
            if (null != kItem) {
                if (ShareUtil.XObject.isEmpty(kItem.getEventId())) {
                    kItem.setEventId(eventId);
                }
                if (ShareUtil.XObject.isEmpty(getColId().apply(kItem))) {
                    setColId(kItem).apply(idGenerator.nextIdStr());
                }
            }
            leadId = null == kItem ? "" : getColId().apply(kItem);
            vSeq = 0;
            for (EventActionIndicatorEntity item2 : vItem) {
                rowsSub.add(item2.setEventId(eventId)
                        .setEventActionId(leadId)
                        .setSeq(++vSeq));
            }
        }

        if(!saveOrUpdateBatch(actions.keySet().stream().filter(Objects::nonNull).collect(Collectors.toList()),useLogicId,dftIfEmpty)){
            return false;
        }
        return saveOrUpdateBatch(null,rowsSub,useLogicId,dftIfEmpty);


    }
    //endregion

    //region getByEventId
    //获取事件措施列表
    public List<EventActionEntity> getByEventId(String eventId,SFunction<EventActionEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(eventId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(EventActionEntity::getEventId,eventId)
                .orderByAsc(EventActionEntity::getId)
                .select(cols)
                .list();
    }
    //获取事件措施列表
    public List<EventActionEntity> getByEventIds(List<String> eventIds,SFunction<EventActionEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(eventIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag=eventIds.size()==1;
        return service.lambdaQuery()
                .eq(oneFlag, EventActionEntity::getEventId,eventIds.iterator().next())
                .in(!oneFlag, EventActionEntity::getEventId,eventIds)
                .orderByAsc(EventActionEntity::getEventId)
                .select(cols)
                .list();
    }

    //获取事件措施影响指标列表
    public List<EventActionIndicatorEntity> getSubByEventId(String eventId,SFunction<EventActionIndicatorEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(eventId)) {
            return Collections.emptyList();
        }
        return subService.lambdaQuery()
                .eq(EventActionIndicatorEntity::getEventId,eventId)
                .orderByAsc(EventActionIndicatorEntity::getEventActionId,EventActionIndicatorEntity::getSeq, EventActionIndicatorEntity::getId)
                .select(cols)
                .list();
    }
    //获取事件措施影响指标列表
    public List<EventActionIndicatorEntity> getSubByEventIds(List<String> eventIds,SFunction<EventActionIndicatorEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(eventIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag=eventIds.size()==1;
        return subService.lambdaQuery()
                .eq(oneFlag, EventActionIndicatorEntity::getEventId,eventIds.iterator().next())
                .in(!oneFlag, EventActionIndicatorEntity::getEventId,eventIds)
                .orderByAsc(EventActionIndicatorEntity::getEventId, EventActionIndicatorEntity::getEventActionId,EventActionIndicatorEntity::getSeq, EventActionIndicatorEntity::getId)
                .select(cols)
                .list();
    }

    //endregion


    //region deleteByEventId
    //删除事件措施
    public boolean delByEventId(String eventId, boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(eventId)) {
            return dftIfEmpty;
        }
        return service.remove(Wrappers.<EventActionEntity>lambdaQuery()
                .eq(EventActionEntity::getEventId,eventId));
    }

    public boolean delByEventId(List<String> ids, boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return service.remove(Wrappers.<EventActionEntity>lambdaQuery()
                .eq(oneFlag, EventActionEntity::getEventId, ids.get(0))
                .in(!oneFlag, EventActionEntity::getEventId, ids));
    }
    //删除事件措施影响指标
    public boolean delSubByEventId(String eventId, boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(eventId)) {
            return dftIfEmpty;
        }
        return subService.remove(Wrappers.<EventActionIndicatorEntity>lambdaQuery()
                .eq(EventActionIndicatorEntity::getEventId,eventId));

    }
    public boolean delSubByEventId(List<String> ids, boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return subService.remove(Wrappers.<EventActionIndicatorEntity>lambdaQuery()
                .eq(oneFlag, EventActionIndicatorEntity::getEventId, ids.get(0))
                .in(!oneFlag, EventActionIndicatorEntity::getEventId, ids));

    }

    //endregion
}
