package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CaseEventActionEntity;
import org.dows.hep.entity.CaseEventActionIndicatorEntity;
import org.dows.hep.service.CaseEventActionIndicatorService;
import org.dows.hep.service.CaseEventActionService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/5/15 10:06
 */
@Component
public class CaseEventActionDao extends BaseSubDao<CaseEventActionService, CaseEventActionEntity, CaseEventActionIndicatorService, CaseEventActionIndicatorEntity> {

    protected CaseEventActionDao() {
        super("处理措施不存在或已删除，请刷新");
    }

    @Override
    protected SFunction<CaseEventActionEntity, String> getColCateg() {
        return null;
    }

    @Override
    protected SFunction<CaseEventActionEntity, String> getColId() {
        return CaseEventActionEntity::getCaseEventActionId;
    }

    @Override
    protected SFunction<String, ?> setColId(CaseEventActionEntity item) {
        return item::setCaseEventActionId;
    }

    @Override
    protected SFunction<CaseEventActionEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(CaseEventActionEntity item) {
        return null;
    }

    @Override
    protected SFunction<CaseEventActionIndicatorEntity, String> getColLeadId() {
        return CaseEventActionIndicatorEntity::getCaseEventActionId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(CaseEventActionIndicatorEntity item) {
        return item::setCaseEventActionId;
    }

    @Override
    protected SFunction<CaseEventActionIndicatorEntity, String> getColSubId() {
        return CaseEventActionIndicatorEntity::getCaseEventActionIndicatorId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(CaseEventActionIndicatorEntity item) {
        return item::setCaseEventActionIndicatorId;
    }

    @Override
    protected SFunction<CaseEventActionIndicatorEntity, Integer> getColSubSeq() {
        return CaseEventActionIndicatorEntity::getSeq;
    }

    @Override
    protected SFunction<Integer, ?> setColSubSeq(CaseEventActionIndicatorEntity item) {
        return null;
    }


    //region save
    public boolean saveOrUpdateBatch(String eventId, LinkedHashMap<CaseEventActionEntity,List<CaseEventActionIndicatorEntity>> actions, boolean useLogicId, boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(actions)) {
            return dftIfEmpty;
        }
        CaseEventActionEntity kItem;
        List<CaseEventActionIndicatorEntity> vItem;
        String leadId;
        int vSeq=0;
        List<CaseEventActionIndicatorEntity> rowsSub=new ArrayList<>();
        for(Map.Entry<CaseEventActionEntity,List<CaseEventActionIndicatorEntity>> item:actions.entrySet()) {
            kItem = item.getKey();
            vItem = item.getValue();
            if (null != kItem) {
                if (ShareUtil.XObject.isEmpty(kItem.getCaseEventId())) {
                    kItem.setCaseEventId(eventId);
                }
                if (ShareUtil.XObject.isEmpty(getColId().apply(kItem))) {
                    setColId(kItem).apply(idGenerator.nextIdStr());
                }
            }
            leadId = null == kItem ? "" : getColId().apply(kItem);
            vSeq = 0;
            for (CaseEventActionIndicatorEntity item2 : vItem) {
                rowsSub.add(item2.setCaseEventId(eventId)
                        .setCaseEventActionId(leadId)
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
    public List<CaseEventActionEntity> getByEventId(String eventId,SFunction<CaseEventActionEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(eventId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(CaseEventActionEntity::getCaseEventId,eventId)
                .orderByAsc(CaseEventActionEntity::getId)
                .select(cols)
                .list();
    }
    public List<CaseEventActionEntity> getByEventIds(List<String> eventIds, SFunction<CaseEventActionEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(eventIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag=eventIds.size()==1;
        return service.lambdaQuery()
                .eq(oneFlag, CaseEventActionEntity::getCaseEventId,eventIds.iterator().next())
                .in(!oneFlag, CaseEventActionEntity::getCaseEventId,eventIds)
                .orderByAsc(CaseEventActionEntity::getCaseEventId)
                .select(cols)
                .list();
    }
    //获取事件措施影响指标列表
    public List<CaseEventActionIndicatorEntity> getSubByEventId(String eventId,SFunction<CaseEventActionIndicatorEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(eventId)) {
            return Collections.emptyList();
        }
        return subService.lambdaQuery()
                .eq(CaseEventActionIndicatorEntity::getCaseEventId,eventId)
                .orderByAsc(CaseEventActionIndicatorEntity::getCaseEventActionId,CaseEventActionIndicatorEntity::getSeq, CaseEventActionIndicatorEntity::getId)
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
        return service.remove(Wrappers.<CaseEventActionEntity>lambdaQuery()
                .eq(CaseEventActionEntity::getCaseEventId,eventId));
    }

    public boolean delByEventId(List<String> ids, boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return service.remove(Wrappers.<CaseEventActionEntity>lambdaQuery()
                .eq(oneFlag, CaseEventActionEntity::getCaseEventId, ids.get(0))
                .in(!oneFlag, CaseEventActionEntity::getCaseEventId, ids));
    }
    //删除事件措施影响指标
    public boolean delSubByEventId(String eventId, boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(eventId)) {
            return dftIfEmpty;
        }
        return subService.remove(Wrappers.<CaseEventActionIndicatorEntity>lambdaQuery()
                .eq(CaseEventActionIndicatorEntity::getCaseEventId,eventId));

    }
    public boolean delSubByEventId(List<String> ids, boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return subService.remove(Wrappers.<CaseEventActionIndicatorEntity>lambdaQuery()
                .eq(oneFlag, CaseEventActionIndicatorEntity::getCaseEventId, ids.get(0))
                .in(!oneFlag, CaseEventActionIndicatorEntity::getCaseEventId, ids));

    }

    //endregion
}
