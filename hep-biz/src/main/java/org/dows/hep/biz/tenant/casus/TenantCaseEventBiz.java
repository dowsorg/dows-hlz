package org.dows.hep.biz.tenant.casus;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.DelRefItemRequest;
import org.dows.hep.api.base.intervene.vo.EventActionVO;
import org.dows.hep.api.base.intervene.vo.EventEvalVO;
import org.dows.hep.api.base.intervene.vo.EventIndicatorVO;
import org.dows.hep.api.enums.EnumEventTriggerType;
import org.dows.hep.api.enums.EnumStatus;
import org.dows.hep.api.tenant.casus.request.CopyCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.DelCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.FindCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.SaveCaseEventRequest;
import org.dows.hep.api.tenant.casus.response.CaseEventInfoResponse;
import org.dows.hep.api.tenant.casus.response.CaseEventResponse;
import org.dows.hep.biz.cache.EventCategCache;
import org.dows.hep.biz.dao.CaseEventActionDao;
import org.dows.hep.biz.dao.CaseEventDao;
import org.dows.hep.biz.dao.EventActionDao;
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
* @description project descr:案例:案例人物事件
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class TenantCaseEventBiz {

    private final CaseEventDao caseEventDao;

    private final CaseEventActionDao caseEventActionDao;

    private final EventDao eventDao;

    private final EventActionDao eventActionDao;

    private final IdGenerator idGenerator;

    protected EventCategCache getCategCache(){
        return EventCategCache.Instance;
    }

    /**
    * @param
    * @return
    * @说明: 获取人物事件列表
    * @关联表: case_event
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Page<CaseEventResponse> pageCaseEvent(FindCaseEventRequest findEvent ) {
        return ShareBiz.buildPage(caseEventDao.pageByCondition(findEvent), i-> CopyWrapper.create(CaseEventResponse::new)
                .endFrom( refreshCateg(i) )
                .setCategIdLv1(getCategCache().getCategLv1(i.getCategIdPath() ,i.getEventCategId()))
                .setCategNameLv1(getCategCache().getCategLv1(i.getCategNamePath() ,i.getCategName())));

    }
    /**
    * @param
    * @return
    * @说明: 获取人物事件详细
    * @关联表: case_event,case_event_eval,case_event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public CaseEventInfoResponse getCaseEvent(String caseEventId ) {
        CaseEventEntity row= AssertUtil.getNotNull(caseEventDao.getById(caseEventId))
                .orElseThrow("人物事件不存在或已删除，请刷新");
        List<CaseEventEvalEntity> rowsEval= caseEventDao.getSubByLeadId(caseEventId,
                CaseEventEvalEntity::getId,
                CaseEventEvalEntity::getCaseEventEvalId,
                CaseEventEvalEntity::getExpression,
                CaseEventEvalEntity::getExpressionDescr);
        List<CaseEventActionEntity> rowsAction= caseEventActionDao.getByEventId(caseEventId,
                CaseEventActionEntity::getId,
                CaseEventActionEntity::getCaseEventActionId,
                CaseEventActionEntity::getActionDesc);
        List<CaseEventActionIndicatorEntity> rowsIndicator= caseEventActionDao.getSubByEventId(caseEventId,
                CaseEventActionIndicatorEntity::getId,
                CaseEventActionIndicatorEntity::getCaseEventActionIndicatorId,
                CaseEventActionIndicatorEntity::getCaseEventActionId,
                CaseEventActionIndicatorEntity::getInitFlag,
                CaseEventActionIndicatorEntity::getIndicatorInstanceId,
                CaseEventActionIndicatorEntity::getExpression,
                CaseEventActionIndicatorEntity::getExpressionDescr,
                CaseEventActionIndicatorEntity::getSeq);
        final String EMPTYActionId="";
        Map<String, List<EventIndicatorVO>> mapIndicators = ShareUtil.XCollection.toGroup(rowsIndicator,
                i -> CopyWrapper.create(EventIndicatorVO::new)
                        .endFrom(i, v -> v.setRefId(i.getCaseEventActionIndicatorId())
                                .setEventActionId(i.getCaseEventActionId())),
                i -> ShareUtil.XObject.defaultIfNull(i.getEventActionId(), EMPTYActionId));

        List<EventEvalVO> vosEval=ShareUtil.XCollection.map(rowsEval,
                i->CopyWrapper.create(EventEvalVO::new).endFrom(i,v->v.setRefId(i.getCaseEventEvalId())));
        List<EventIndicatorVO> vosIndicator=mapIndicators.getOrDefault(EMPTYActionId, Collections.emptyList());
        List<EventActionVO> vosAction=ShareUtil.XCollection.map(rowsAction,
                i->CopyWrapper.create(EventActionVO::new)
                        .endFrom(i,v->v.setRefId(i.getCaseEventActionId())
                                .setIndicators(mapIndicators.get(i.getCaseEventActionId()))));
        mapIndicators.clear();
        return CopyWrapper.create(CaseEventInfoResponse::new)
                .endFrom(refreshCateg(row))
                .setEvals(vosEval)
                .setIndicators(vosIndicator)
                .setActions(vosAction);
    }
    /**
    * @param
    * @return
    * @说明: 保存人物事件
    * @关联表: case_event,case_event_eval,case_event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveCaseEvent(SaveCaseEventRequest saveCaseEvent , HttpServletRequest request) {
        LoginContextVO voLogin=ShareBiz.getLoginUser(request);
        //TODO checkLogin
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveCaseEvent.getEventId())
                        && caseEventDao.getById(saveCaseEvent.getEventId(), CaseEventEntity::getId).isEmpty())
                .throwMessage("人物事件不存在或已删除");
        CategVO categVO = null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveCaseEvent.getEventCategId())
                        || null == (categVO = getCategCache().getById(saveCaseEvent.getEventCategId())))
                .throwMessage("事件类别不存在");
        EnumEventTriggerType triggerType=EnumEventTriggerType.of(saveCaseEvent.getTriggerType());
        AssertUtil.trueThenThrow(triggerType==EnumEventTriggerType.CONDITION&&ShareUtil.XCollection.notEmpty(saveCaseEvent.getIndicators()))
                .throwMessage("条件触发时不支持定义影响的指标");
        AssertUtil.trueThenThrow(triggerType!=EnumEventTriggerType.CONDITION&&ShareUtil.XCollection.notEmpty(saveCaseEvent.getEvals()))
                .throwMessage("时间触发时不支持定义触发条件");


        //重复指标检查
        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveCaseEvent.getIndicators())
                        && saveCaseEvent.getIndicators().stream()
                        .map(EventIndicatorVO::getIndicatorInstanceId)
                        .collect(Collectors.toSet())
                        .size() < saveCaseEvent.getIndicators().size())
                .throwMessage("存在重复的影响指标，请检查");
        LinkedHashMap<CaseEventActionEntity, List<CaseEventActionIndicatorEntity>> mapActions = new LinkedHashMap<>();
        saveCaseEvent.setActions(ShareUtil.XObject.defaultIfNull(saveCaseEvent.getActions(), new ArrayList<>()));
        saveCaseEvent.getActions().forEach(item -> {
            List<CaseEventActionIndicatorEntity> indicators = ShareUtil.XCollection.map(item.getIndicators(),
                    e -> CopyWrapper.create(CaseEventActionIndicatorEntity::new).endFrom(e, v -> v.setCaseEventActionIndicatorId(e.getRefId()).setInitFlag(false)));
            AssertUtil.trueThenThrow(indicators.stream().map(CaseEventActionIndicatorEntity::getIndicatorInstanceId).collect(Collectors.toSet()).size() < indicators.size())
                    .throwMessage(String.format("措施\"%s\"存在重复的关联指标", item.getActionDesc()));
            mapActions.put(CopyWrapper.create(CaseEventActionEntity::new).endFrom(item, v -> v.setCaseEventActionId(item.getRefId())), indicators);
        });
        if (ShareUtil.XCollection.notEmpty(saveCaseEvent.getIndicators())) {
            mapActions.put(null, ShareUtil.XCollection.map(saveCaseEvent.getIndicators(),
                    e -> CopyWrapper.create(CaseEventActionIndicatorEntity::new).endFrom(e, v -> v.setCaseEventActionIndicatorId(e.getRefId()).setInitFlag(true))));
        }
        //build po
        CaseEventEntity row = CopyWrapper.create(CaseEventEntity::new)
                .endFrom(saveCaseEvent)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath())
                .setCreateAccountId(voLogin.getAccountId())
                .setCreateAccountName(voLogin.getAccountName());

        List<CaseEventEvalEntity> rowEvals = ShareUtil.XCollection.map(saveCaseEvent.getEvals(),
                e -> CopyWrapper.create(CaseEventEvalEntity::new).endFrom(e, v -> v.setCaseEventEvalId(e.getRefId())));
        return caseEventDao.tranSave(row, rowEvals, mapActions);
    }

    /**
     * 批量添加事件
     * @param saveCaseEvent
     * @param request
     * @return
     */
    public Boolean copyCaseEvent(CopyCaseEventRequest saveCaseEvent , HttpServletRequest request) {
        LoginContextVO voLogin = ShareBiz.getLoginUser(request);
        //TODO checkLogin
        final List<String> eventIds = saveCaseEvent.getIds();
        List<EventEntity> rowsEvent = eventDao.getByIds(eventIds);
        AssertUtil.trueThenThrow(ShareUtil.XCollection.isEmpty(rowsEvent))
                .throwMessage("数据库事件不存在或已删除");
        List<EventEvalEntity> rowsEval = eventDao.getSubByLeadIds(eventIds);
        List<EventActionEntity> rowsAction = eventActionDao.getByEventIds(eventIds);
        List<EventActionIndicatorEntity> rowsIndicator = eventActionDao.getSubByEventIds(eventIds);
        final String personId = saveCaseEvent.getPersonId();
        final String personName = saveCaseEvent.getPersonName();
        final String caseInstanceId = saveCaseEvent.getCaseInstanceId();
        Map<String, String> mapEventIds = new HashMap<>();
        Map<String, String> mapEventActionIds = new HashMap<>();
        List<CaseEventEntity> rowsCaseEvent = ShareUtil.XCollection.map(rowsEvent, i ->
                CopyWrapper.create(CaseEventEntity::new)
                        .endFrom(i)
                        .setCaseInstanceId(caseInstanceId)
                        .setPersonId(personId)
                        .setPersonName(personName)
                        .setCreateAccountId(voLogin.getAccountId())
                        .setCreateAccountName(voLogin.getAccountName())
                        .setId(null)
                        .setState(EnumStatus.ENABLE.getCode())
                        .setEventId(i.getEventId())
                        .setCaseEventId(mapEventIds.computeIfAbsent(i.getEventId(), v -> idGenerator.nextIdStr()))
        );
        List<CaseEventEvalEntity> rowsCaseEval = ShareUtil.XCollection.map(rowsEval, i ->
                CopyWrapper.create(CaseEventEvalEntity::new)
                        .endFrom(i)
                        .setId(null)
                        .setCaseEventId(mapEventIds.computeIfAbsent(i.getEventId(), v -> idGenerator.nextIdStr()))
                        .setCaseEventEvalId(idGenerator.nextIdStr())
        );
        List<CaseEventActionEntity> rowsCaseAction = ShareUtil.XCollection.map(rowsAction, i ->
                CopyWrapper.create(CaseEventActionEntity::new)
                        .endFrom(i)
                        .setId(null)
                        .setCaseEventId(mapEventIds.computeIfAbsent(i.getEventId(), v -> idGenerator.nextIdStr()))
                        .setCaseEventActionId(mapEventActionIds.computeIfAbsent(i.getEventActionId(), v -> idGenerator.nextIdStr()))
        );
        List<CaseEventActionIndicatorEntity> rowsCaseIndicator = ShareUtil.XCollection.map(rowsIndicator, i ->
                CopyWrapper.create(CaseEventActionIndicatorEntity::new)
                        .endFrom(i)
                        .setId(null)
                        .setCaseEventId(mapEventIds.computeIfAbsent(i.getEventId(), v -> idGenerator.nextIdStr()))
                        .setCaseEventActionId(mapEventActionIds.get(i.getEventActionId()))
                        .setCaseEventActionIndicatorId(idGenerator.nextIdStr())
        );
        mapEventIds.clear();
        mapEventActionIds.clear();
        return caseEventDao.tranSaveBatch(rowsCaseEvent, rowsCaseEval, rowsCaseAction, rowsCaseIndicator);
    }

    /**
    * @param
    * @return
    * @说明: 删除事件
    * @关联表: case_event,case_event_eval,case_event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delCaseEvent(DelCaseEventRequest delCaseEvent ) {
        return caseEventDao.tranDelete(delCaseEvent.getIds(),true);
    }

    /**
     * 删除事件触发条件
     * @param delRefItemRequest
     * @return
     */
    public Boolean delRefEval(DelRefItemRequest delRefItemRequest ) {
        return caseEventDao.tranDeleteSub(delRefItemRequest.getIds(),"条件不存在或已删除");
    }

    /**
     * 删除处理措施
     * @param delRefItemRequest
     * @return
     */
    public Boolean delRefAction(DelRefItemRequest delRefItemRequest){
        return caseEventActionDao.tranDelete(delRefItemRequest.getIds(),true);
    }

    /**
     * 删除事件影响指标
     * @param delRefItemRequest
     * @return
     */

    public Boolean delRefEventIndicator(DelRefItemRequest delRefItemRequest){
        return caseEventActionDao.tranDeleteSub(delRefItemRequest.getIds(),"关联指标不存在或已删除");
    }

    /**
     * 删除处理措施影响指标
     * @param delRefItemRequest
     * @return
     */

    public Boolean delRefActionIndicator(DelRefItemRequest delRefItemRequest){
        return caseEventActionDao.tranDeleteSub(delRefItemRequest.getIds(),"关联指标不存在或已删除");
    }

    /**
     * 获取缓存最新分类信息
     * @param src
     * @return
     */
    protected CaseEventEntity refreshCateg(CaseEventEntity src) {
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