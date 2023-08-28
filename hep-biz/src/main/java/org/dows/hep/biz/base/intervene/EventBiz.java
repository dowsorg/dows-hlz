package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.EventInfoResponse;
import org.dows.hep.api.base.intervene.response.EventResponse;
import org.dows.hep.api.base.intervene.vo.EventActionInfoVO;
import org.dows.hep.api.base.intervene.vo.IndicatorExpressionVO;
import org.dows.hep.api.enums.EnumCategFamily;
import org.dows.hep.api.enums.EnumEventTriggerSpan;
import org.dows.hep.api.enums.EnumEventTriggerType;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.base.indicator.IndicatorExpressionBiz;
import org.dows.hep.biz.cache.CategCache;
import org.dows.hep.biz.cache.CategCacheFactory;
import org.dows.hep.biz.dao.*;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.EventActionEntity;
import org.dows.hep.entity.EventCategEntity;
import org.dows.hep.entity.EventEntity;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:干预:数据库事件
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class EventBiz{

    private final EventCategDao eventCategDao;

    private final EventDao eventDao;

    private final EventActionDao eventActionDao;

    private final IndicatorExpressionRefDao indicatorExpressionRefDao;

    private final IndicatorExpressionBiz indicatorExpressionBiz;

    private final IdGenerator idGenerator;

    private final EnumCategFamily evencategFamily =EnumCategFamily.EVENT;


    protected CategCache getCategCache(){
        /*if(SnapshotRequestHolder.hasSnapshotRequest()){
            return CategCacheFactory.EVENT.getExptCache();
        }*/
        return CategCacheFactory.EVENT.getCache();
    }

    /**
    * @param
    * @return
    * @说明: 获取类别
    * @关联表: event_categ
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<CategVO> listEventCateg(FindEventCategRequest findEventCateg ) {
        final String appId=findEventCateg.getAppId();
        final String pid=ShareUtil.XString.defaultIfEmpty(findEventCateg.getPid(), evencategFamily.getCode());
        return getCategCache().getByParentId(appId,pid, Optional.ofNullable(findEventCateg.getWithChild()).orElse(0)>0);

    }
    /**
    * @param
    * @return
    * @说明: 保存类别
    * @关联表: event_categ
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间:
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveEventCategs(List<SaveEventCategRequest> saveEventCateg ) {
        if(ShareUtil.XObject.isEmpty(saveEventCateg)){
            return false;
        }
        final String appId=saveEventCateg.get(0).getAppId();
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(appId))
                .throwMessage("appId不可为空");
        final String pid=saveEventCateg.get(0).getCategPid();
        final CategCache cache=getCategCache();
        CategVO parent = ShareUtil.XObject.defaultIfNull(cache.getById(appId, pid),new CategVO());
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(pid) && ShareUtil.XObject.isEmpty(parent.getCategIdPath()))
                .throwMessage("父类别不存在");
        List<EventCategEntity> rows=ShareUtil.XCollection.map(saveEventCateg, i-> {
            if(ShareUtil.XObject.isEmpty(i.getCategId())){
                i.setCategId(idGenerator.nextIdStr());
            }
            return CopyWrapper.create(EventCategEntity::new)
                    .endFrom(i)
                    .setAppId(appId)
                    .setFamily(evencategFamily.getCode())
                    .setEventCategId(i.getCategId())
                    .setCategPid(pid)
                    .setCategIdPath(cache.buildCategPath(parent.getCategIdPath(), i.getCategId()))
                    .setCategNamePath(cache.buildCategPath(parent.getCategNamePath(), i.getCategName()));
        });

        eventCategDao.tranSaveBatch(rows,true,false);
        cache.clear();
        return true;
    }
    /**
    * @param
    * @return
    * @说明: 删除类别
    * @关联表: event_categ
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delEventCateg(DelEventCategRequest delEventCateg ) {
        final String appId= delEventCateg.getAppId();
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(delEventCateg.getIds()))
                .throwMessage("缺少必要参数");
        final CategCache cache=getCategCache();
        delEventCateg.getIds().forEach(i -> {
            AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(cache.getByParentId(appId, i, true)))
                    .throwMessage("类别包含子级类别，不可删除");
            AssertUtil.trueThenThrow(EnumCheckCategPolicy.EVENT.checkCategRef(i))
                    .throwMessage("类别已被引用，不可删除");
        });
        eventCategDao.tranDelete(delEventCateg.getIds());
        cache.clear();
        return true;
    }
    /**
    * @param
    * @return
    * @说明: 获取事件列表
    * @关联表: event
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Page<EventResponse> pageEvent(FindEventRequest findEvent ) {
        final CategCache cache=getCategCache();
        return ShareBiz.buildPage(eventDao.pageByCondition(findEvent),i->
                CopyWrapper.create(EventResponse::new).endFrom( refreshCateg(cache,i)));

    }
    /**
    * @param
    * @return
    * @说明: 获取事件详细
    * @关联表: event,event_eval,event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public EventInfoResponse getEvent(String appId,  String eventId) {
        EventEntity row = AssertUtil.getNotNull(eventDao.getById(eventId))
                .orElseThrow("突发事件不存在或已删除，请刷新");
        List<EventActionEntity> rowsAction = eventActionDao.getByEventId(eventId,
                EventActionEntity::getId,
                EventActionEntity::getEventActionId,
                EventActionEntity::getActionDesc);
        //fill 公式
        Set<String> reasonIds=new HashSet<>(rowsAction.size()+1);
        reasonIds.add(eventId);
        rowsAction.forEach(i->reasonIds.add(i.getEventActionId()));
        Map<String, List<IndicatorExpressionResponseRs>> mapExressions=ShareBiz.getExpressionsByReasonIds(indicatorExpressionBiz,appId,reasonIds);

        List<IndicatorExpressionResponseRs> conditions=new ArrayList<>();
        List<IndicatorExpressionResponseRs> effects=new ArrayList<>();
        mapExressions.getOrDefault(eventId,Collections.emptyList())
                .forEach(i->{
                    switch (EnumIndicatorExpressionSource.of( i.getSource())){
                        case EMERGENCY_TRIGGER_CONDITION:
                            conditions.add(i);
                            break;
                        case EMERGENCY_INFLUENCE_INDICATOR:
                            effects.add(i);
                            break;
                    }
                });
        List<EventActionInfoVO> vosAction = ShareUtil.XCollection.map(rowsAction,
                i -> CopyWrapper.create(EventActionInfoVO::new)
                        .endFrom(i, v -> v.setRefId(i.getEventActionId())
                                .setActionExpresssions(mapExressions.get(i.getEventActionId()))));
        mapExressions.clear();
        final CategCache cache=getCategCache();
        return CopyWrapper.create(EventInfoResponse::new)
                .endFrom(refreshCateg(cache,row))
                .setConditionExpresssions(conditions)
                .setEffectExpresssions(effects)
                .setActions(vosAction);

    }
    /**
    * @param
    * @return
    * @说明: 保存事件
    * @关联表: event,event_eval,event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveEvent(SaveEventRequest saveEvent, HttpServletRequest request) {
        final String appId=saveEvent.getAppId();
        LoginContextVO voLogin=ShareBiz.getLoginUser(request);
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveEvent.getEventId())
                        && eventDao.getById(saveEvent.getEventId(), EventEntity::getId).isEmpty())
                .throwMessage("突发事件不存在或已删除");
        CategVO categVO = null;
        final CategCache cache=getCategCache();
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveEvent.getEventCategId())
                        || null == (categVO = cache.getById(appId,saveEvent.getEventCategId())))
                .throwMessage("事件类别不存在");
        EnumEventTriggerType triggerType=EnumEventTriggerType.of(saveEvent.getTriggerType());
        AssertUtil.trueThenThrow(triggerType==EnumEventTriggerType.CONDITION&&ShareUtil.XCollection.notEmpty(saveEvent.getEffectExpresssions()))
                        .throwMessage("条件触发时不支持定义影响的指标");
        AssertUtil.trueThenThrow(triggerType!=EnumEventTriggerType.CONDITION&&ShareUtil.XCollection.notEmpty(saveEvent.getConditionExpresssionIds()))
                .throwMessage("时间触发时不支持定义触发条件");
        AssertUtil.trueThenThrow(triggerType!=EnumEventTriggerType.CONDITION&& EnumEventTriggerSpan.of(saveEvent.getTriggerSpan())==EnumEventTriggerSpan.NONE)
                .throwMessage("请选择正确的触发时间段");

        //重复指标检查
        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveEvent.getEffectExpresssions())
                        && saveEvent.getEffectExpresssions().stream()
                        .map(IndicatorExpressionVO::getIndicatorInstanceId)
                        .collect(Collectors.toSet())
                        .size() < saveEvent.getEffectExpresssions().size())
                .throwMessage("存在重复的影响指标，请检查");
        saveEvent.setActions(ShareUtil.XObject.defaultIfNull(saveEvent.getActions(), new ArrayList<>()));
        saveEvent.getActions().forEach(item -> {
            AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(item.getActionExpresssions())&&item.getActionExpresssions().stream()
                            .map(IndicatorExpressionVO::getIndicatorInstanceId)
                            .collect(Collectors.toSet()).size() < item.getActionExpresssions().size())
                    .throwMessage(String.format("措施\"%s\"存在重复的关联指标", item.getActionDesc()));

        });

        //build po
        if(ShareUtil.XObject.isEmpty(saveEvent.getEventId())){
            saveEvent.setEventId(idGenerator.nextIdStr());
        }
        saveEvent.getActions().forEach(i->i.setRefId(ShareUtil.XString.defaultIfEmpty(i.getRefId(),()->idGenerator.nextIdStr())));
        EventEntity row = CopyWrapper.create(EventEntity::new)
                .endFrom(saveEvent)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath())
                .setCategIdLv1(categVO.getCategIdLv1())
                .setCategNameLv1(categVO.getCategNameLv1())
                .setCreateAccountId(voLogin.getAccountId())
                .setCreateAccountName(voLogin.getAccountName())
                .setTriggerType(triggerType.getCode());
        List<EventActionEntity> rowActions=ShareUtil.XCollection.map(saveEvent.getActions(), i->
                CopyWrapper.create(EventActionEntity::new)
                        .endFrom(i)
                        .setEventActionId(i.getRefId()));

        //expression
        final String eventId=row.getEventId();
        Map<String,List<String>> mapExpressions=new HashMap<>();
        if(ShareUtil.XObject.notEmpty(saveEvent.getConditionExpresssionIds())){
            mapExpressions.computeIfAbsent(eventId, key->new ArrayList<>()).addAll(saveEvent.getConditionExpresssionIds());
        }
        if(ShareUtil.XObject.notEmpty(saveEvent.getEffectExpresssions())) {
            List<String> dst=mapExpressions.computeIfAbsent(eventId, key -> new ArrayList<>());
            saveEvent.getEffectExpresssions().forEach(i ->dst.add(i.getIndicatorExpressionId()));
        }
        saveEvent.getActions().forEach(i->{
            if(ShareUtil.XObject.isEmpty(i.getActionExpresssions())){
                return;
            }
            List<String> dst= mapExpressions.computeIfAbsent(i.getRefId(), key->new ArrayList<>());
            i.getActionExpresssions().forEach(vo->dst.add(vo.getIndicatorExpressionId()));
        });

        return eventDao.tranSave(row, rowActions, mapExpressions);
    }
    /**
    * @param
    * @return
    * @说明: 删除事件
    * @关联表: event,event_eval,event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delEvent(DelEventRequest delEvent ) {
        return eventDao.tranDelete(delEvent.getIds(),true);
    }
    /**
    * @param
    * @return
    * @说明: 启用、禁用事件
    * @关联表: event
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean setEventState(SetEventStateRequest setEventState ) {
        return eventDao.tranSetState(setEventState.getEventId(), setEventState.getState());
    }

    /**
     * 删除事件触发条件
     * @param delRefItemRequest
     * @return
     */
    public Boolean delRefEval(DelRefItemRequest delRefItemRequest ) {
        return indicatorExpressionRefDao.tranDeleteByExpressionId(delRefItemRequest.getIds());
    }

    /**
     * 删除处理措施
     * @param delRefItemRequest
     * @return
     */
    public Boolean delRefAction(DelRefItemRequest delRefItemRequest){
        eventActionDao.tranDelete(delRefItemRequest.getIds());
        return indicatorExpressionRefDao.tranDeleteByReasonId(delRefItemRequest.getIds());
    }

    /**
     * 删除事件影响指标
     * @param delRefItemRequest
     * @return
     */

    public Boolean delRefEventIndicator(DelRefItemRequest delRefItemRequest){
        return indicatorExpressionRefDao.tranDeleteByExpressionId(delRefItemRequest.getIds());
    }

    /**
     * 删除处理措施影响指标
     * @param delRefItemRequest
     * @return
     */

    public Boolean delRefActionIndicator(DelRefItemRequest delRefItemRequest){
        return indicatorExpressionRefDao.tranDeleteByExpressionId(delRefItemRequest.getIds());
    }

    /**
     * 获取缓存最新分类信息
     * @param src
     * @return
     */
    protected EventEntity refreshCateg(CategCache cache, EventEntity src) {
        if (ShareUtil.XObject.isEmpty(src.getEventCategId())) {
            return src;
        }
        CategVO cacheItem = cache.getById(src.getAppId(), src.getEventCategId());
        if (null == cacheItem) {
            return src;
        }
        return src.setCategName(cacheItem.getCategName())
                .setCategIdPath(cacheItem.getCategIdPath())
                .setCategNamePath(cacheItem.getCategNamePath())
                .setCategIdLv1(cacheItem.getCategIdLv1())
                .setCategNameLv1(cacheItem.getCategNameLv1());

    }
}