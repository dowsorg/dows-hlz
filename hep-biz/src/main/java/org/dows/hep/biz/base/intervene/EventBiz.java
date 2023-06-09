package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.EventInfoResponse;
import org.dows.hep.api.base.intervene.response.EventResponse;
import org.dows.hep.api.base.intervene.vo.EventActionVO;
import org.dows.hep.api.base.intervene.vo.EventEvalVO;
import org.dows.hep.api.base.intervene.vo.EventIndicatorVO;
import org.dows.hep.api.enums.EnumCategFamily;
import org.dows.hep.api.enums.EnumEventTriggerSpan;
import org.dows.hep.api.enums.EnumEventTriggerType;
import org.dows.hep.biz.cache.EventCategCache;
import org.dows.hep.biz.dao.EnumCheckCategPolicy;
import org.dows.hep.biz.dao.EventActionDao;
import org.dows.hep.biz.dao.EventCategDao;
import org.dows.hep.biz.dao.EventDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
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

    private final IdGenerator idGenerator;

    private final EnumCategFamily evencategFamily =EnumCategFamily.EVENT;


    protected EventCategCache getCategCache(){
        return EventCategCache.Instance;
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

        final String pid=ShareUtil.XString.defaultIfEmpty(findEventCateg.getPid(), evencategFamily.getCode());
        return getCategCache().getByParentId(pid, Optional.ofNullable(findEventCateg.getWithChild()).orElse(0)>0);

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
        final String pid=saveEventCateg.get(0).getCategPid();
        CategVO parent = ShareUtil.XObject.defaultIfNull(getCategCache().getById(pid),new CategVO());
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(pid) && ShareUtil.XObject.isEmpty(parent.getCategIdPath()))
                .throwMessage("父类别不存在");
        List<EventCategEntity> rows=ShareUtil.XCollection.map(saveEventCateg, i-> {
            if(ShareUtil.XObject.isEmpty(i.getCategId())){
                i.setCategId(idGenerator.nextIdStr());
            }
            return CopyWrapper.create(EventCategEntity::new)
                    .endFrom(i)
                    .setFamily(evencategFamily.getCode())
                    .setEventCategId(i.getCategId())
                    .setCategPid(pid)
                    .setCategIdPath(getCategCache().getCategPath(parent.getCategIdPath(), i.getCategId()))
                    .setCategNamePath(getCategCache().getCategPath(parent.getCategNamePath(), i.getCategName()));
        });

        eventCategDao.tranSaveBatch(rows);
        getCategCache().clear();
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
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(delEventCateg.getIds()))
                .throwMessage("缺少必要参数");
        delEventCateg.getIds().forEach(i -> {
            AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(getCategCache().getByParentId(i, true)))
                    .throwMessage("类别包含子级类别，不可删除");
            AssertUtil.trueThenThrow(EnumCheckCategPolicy.EVENT.checkCategRef(i))
                    .throwMessage("类别已被引用，不可删除");
        });
        eventCategDao.tranDelete(delEventCateg.getIds());
        getCategCache().clear();
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

        return ShareBiz.buildPage(eventDao.pageByCondition(findEvent),i-> CopyWrapper.create(EventResponse::new)
                .endFrom( refreshCateg(i) )
                .setCategIdLv1(getCategCache().getCategLv1(i.getCategIdPath() ,i.getEventCategId()))
                .setCategNameLv1(getCategCache().getCategLv1(i.getCategNamePath() ,i.getCategName())));

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
    public EventInfoResponse getEvent(String eventId ) {
        EventEntity row = AssertUtil.getNotNull(eventDao.getById(eventId))
                .orElseThrow("突发事件不存在或已删除，请刷新");
        List<EventEvalEntity> rowsEval = eventDao.getSubByLeadId(eventId,
                EventEvalEntity::getId,
                EventEvalEntity::getEventEvalId,
                EventEvalEntity::getExpression,
                EventEvalEntity::getExpressionDescr);
        List<EventActionEntity> rowsAction = eventActionDao.getByEventId(eventId,
                EventActionEntity::getId,
                EventActionEntity::getEventActionId,
                EventActionEntity::getActionDesc);
        List<EventActionIndicatorEntity> rowsIndicator = eventActionDao.getSubByEventId(eventId,
                EventActionIndicatorEntity::getId,
                EventActionIndicatorEntity::getEventActionIndicatorId,
                EventActionIndicatorEntity::getEventActionId,
                EventActionIndicatorEntity::getInitFlag,
                EventActionIndicatorEntity::getIndicatorInstanceId,
                EventActionIndicatorEntity::getIndicatorCategoryId,
                EventActionIndicatorEntity::getExpression,
                EventActionIndicatorEntity::getExpressionDescr,
                EventActionIndicatorEntity::getExpressionVars,
                EventActionIndicatorEntity::getExpressionNames,
                EventActionIndicatorEntity::getSeq);
        final String EMPTYActionId="";
        Map<String, List<EventIndicatorVO>> mapIndicators = ShareUtil.XCollection.groupBy(rowsIndicator,
                i -> CopyWrapper.create(EventIndicatorVO::new)
                        .endFrom(i, v -> v.setRefId(i.getEventActionIndicatorId())),
                i -> ShareUtil.XObject.defaultIfNull(i.getEventActionId(), EMPTYActionId));
        List<EventEvalVO> vosEval = ShareUtil.XCollection.map(rowsEval,
                i -> CopyWrapper.create(EventEvalVO::new).endFrom(i, v -> v.setRefId(i.getEventEvalId())));
        List<EventIndicatorVO> vosIndicator = mapIndicators.getOrDefault(EMPTYActionId, Collections.emptyList());
        List<EventActionVO> vosAction = ShareUtil.XCollection.map(rowsAction,
                i -> CopyWrapper.create(EventActionVO::new)
                        .endFrom(i, v -> v.setRefId(i.getEventActionId())
                                .setIndicators(mapIndicators.get(i.getEventActionId()))));
        mapIndicators.clear();
        return CopyWrapper.create(EventInfoResponse::new)
                .endFrom(refreshCateg(row))
                .setEvals(vosEval)
                .setIndicators(vosIndicator)
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
        LoginContextVO voLogin=ShareBiz.getLoginUser(request);
        //TODO checkLogin
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveEvent.getEventId())
                        && eventDao.getById(saveEvent.getEventId(), EventEntity::getId).isEmpty())
                .throwMessage("突发事件不存在或已删除");
        CategVO categVO = null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveEvent.getEventCategId())
                        || null == (categVO = getCategCache().getById(saveEvent.getEventCategId())))
                .throwMessage("事件类别不存在");
        EnumEventTriggerType triggerType=EnumEventTriggerType.of(saveEvent.getTriggerType());
        AssertUtil.trueThenThrow(triggerType==EnumEventTriggerType.CONDITION&&ShareUtil.XCollection.notEmpty(saveEvent.getIndicators()))
                        .throwMessage("条件触发时不支持定义影响的指标");
        AssertUtil.trueThenThrow(triggerType!=EnumEventTriggerType.CONDITION&&ShareUtil.XCollection.notEmpty(saveEvent.getEvals()))
                .throwMessage("时间触发时不支持定义触发条件");
        AssertUtil.trueThenThrow(triggerType!=EnumEventTriggerType.CONDITION&& EnumEventTriggerSpan.of(saveEvent.getTriggerSpan())==EnumEventTriggerSpan.NONE)
                .throwMessage("请选择正确的触发时间段");

        //重复指标检查
        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveEvent.getIndicators())
                        && saveEvent.getIndicators().stream()
                        .map(EventIndicatorVO::getIndicatorInstanceId)
                        .collect(Collectors.toSet())
                        .size() < saveEvent.getIndicators().size())
                .throwMessage("存在重复的影响指标，请检查");
        LinkedHashMap<EventActionEntity, List<EventActionIndicatorEntity>> mapActions = new LinkedHashMap<>();
        saveEvent.setActions(ShareUtil.XObject.defaultIfNull(saveEvent.getActions(), new ArrayList<>()));
        saveEvent.getActions().forEach(item -> {
            List<EventActionIndicatorEntity> indicators = ShareUtil.XCollection.map(item.getIndicators(),
                    e -> CopyWrapper.create(EventActionIndicatorEntity::new).endFrom(e, v -> v.setEventActionIndicatorId(e.getRefId()).setInitFlag(false)));
            AssertUtil.trueThenThrow(indicators.stream().map(EventActionIndicatorEntity::getIndicatorInstanceId).collect(Collectors.toSet()).size() < indicators.size())
                    .throwMessage(String.format("措施\"%s\"存在重复的关联指标", item.getActionDesc()));
            mapActions.put(CopyWrapper.create(EventActionEntity::new).endFrom(item, v -> v.setEventActionId(item.getRefId())), indicators);
        });
        if (ShareUtil.XCollection.notEmpty(saveEvent.getIndicators())) {
            mapActions.put(null, ShareUtil.XCollection.map(saveEvent.getIndicators(),
                    e -> CopyWrapper.create(EventActionIndicatorEntity::new).endFrom(e, v -> v.setEventActionIndicatorId(e.getRefId()).setInitFlag(true))));
        }

        //build po
        EventEntity row = CopyWrapper.create(EventEntity::new)
                .endFrom(saveEvent)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath())
                .setCreateAccountId(voLogin.getAccountId())
                .setCreateAccountName(voLogin.getAccountName())
                .setTriggerType(triggerType.getCode());

        List<EventEvalEntity> rowEvals = ShareUtil.XCollection.map(saveEvent.getEvals(),
                e -> CopyWrapper.create(EventEvalEntity::new).endFrom(e, v -> v.setEventEvalId(e.getRefId())));
        return eventDao.tranSave(row, rowEvals, mapActions);
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
        return eventDao.tranDeleteSub(delRefItemRequest.getIds(),"条件不存在或已删除");
    }

    /**
     * 删除处理措施
     * @param delRefItemRequest
     * @return
     */
    public Boolean delRefAction(DelRefItemRequest delRefItemRequest){
        return eventActionDao.tranDelete(delRefItemRequest.getIds(),true);
    }

    /**
     * 删除事件影响指标
     * @param delRefItemRequest
     * @return
     */

    public Boolean delRefEventIndicator(DelRefItemRequest delRefItemRequest){
        return eventActionDao.tranDeleteSub(delRefItemRequest.getIds(),"关联指标不存在或已删除");
    }

    /**
     * 删除处理措施影响指标
     * @param delRefItemRequest
     * @return
     */

    public Boolean delRefActionIndicator(DelRefItemRequest delRefItemRequest){
        return eventActionDao.tranDeleteSub(delRefItemRequest.getIds(),"关联指标不存在或已删除");
    }

    /**
     * 获取缓存最新分类信息
     * @param src
     * @return
     */
    protected EventEntity refreshCateg(EventEntity src) {
        if (ShareUtil.XObject.isEmpty(src.getEventCategId())) {
            return src;
        }
        CategVO cacheItem = getCategCache().getById(src.getEventCategId());
        if (null == cacheItem) {
            return src;
        }
        return src.setCategName(cacheItem.getCategName())
                .setCategIdPath(cacheItem.getCategIdPath())
                .setCategNamePath(cacheItem.getCategNamePath());

    }
}