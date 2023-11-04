package org.dows.hep.biz.tenant.casus;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.CaseIndicatorExpressionResponseRs;
import org.dows.hep.api.base.intervene.request.DelRefItemRequest;
import org.dows.hep.api.base.intervene.vo.CaseEventActionInfoVO;
import org.dows.hep.api.base.intervene.vo.IndicatorExpressionVO;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.tenant.casus.request.CopyCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.DelCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.FindCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.SaveCaseEventRequest;
import org.dows.hep.api.tenant.casus.response.CaseEventInfoResponse;
import org.dows.hep.api.tenant.casus.response.CaseEventResponse;
import org.dows.hep.biz.base.indicator.CaseIndicatorExpressionBiz;
import org.dows.hep.biz.cache.CategCache;
import org.dows.hep.biz.cache.CategCacheFactory;
import org.dows.hep.biz.dao.*;
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

    private final CaseIndicatorExpressionRefDao caseIndicatorExpressionRefDao;

    private final CaseIndicatorExpressionDao caseIndicatorExpressionDao;

    private final CaseIndicatorInstanceDao caseIndicatorInstanceDao;

    private final CaseIndicatorExpressionBiz caseIndicatorExpressionBiz;

    private final EventDao eventDao;

    private final EventActionDao eventActionDao;

    private final IndicatorExpressionRefDao indicatorExpressionRefDao;

    private final IndicatorExpressionDao indicatorExpressionDao;

    private final IndicatorInstanceDao indicatorInstanceDao;

    private final CasePersonDao casePersonDao;

    private final IdGenerator idGenerator;

    protected CategCache getCategCache(){
        return CategCacheFactory.EVENT.getCache();
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
                .endFrom( refreshCateg(i) ));

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
    public CaseEventInfoResponse getCaseEvent(String appId,  String caseEventId ) {
        CaseEventEntity row = AssertUtil.getNotNull(caseEventDao.getById(caseEventId))
                .orElseThrow("人物事件不存在或已删除，请刷新");
        List<CaseEventActionEntity> rowsAction = caseEventActionDao.getByEventId(caseEventId,
                CaseEventActionEntity::getId,
                CaseEventActionEntity::getCaseEventActionId,
                CaseEventActionEntity::getActionDesc);

        //fill 公式
        Set<String> reasonIds = new HashSet<>(rowsAction.size() + 1);
        reasonIds.add(caseEventId);
        rowsAction.forEach(i -> reasonIds.add(i.getCaseEventActionId()));
        Map<String, List<CaseIndicatorExpressionResponseRs>> mapExressions = ShareBiz.getCaseExpressionsByReasonIds(caseIndicatorExpressionBiz, appId, reasonIds);

        List<CaseIndicatorExpressionResponseRs> conditions = new ArrayList<>();
        List<CaseIndicatorExpressionResponseRs> effects = new ArrayList<>();
        mapExressions.getOrDefault(caseEventId, Collections.emptyList())
                .forEach(i -> {
                    switch (EnumIndicatorExpressionSource.of(i.getSource())) {
                        case EMERGENCY_TRIGGER_CONDITION:
                            conditions.add(i);
                            break;
                        case EMERGENCY_INFLUENCE_INDICATOR:
                            effects.add(i);
                            break;
                    }
                });
        List<CaseEventActionInfoVO> vosAction = ShareUtil.XCollection.map(rowsAction,
                i -> CopyWrapper.create(CaseEventActionInfoVO::new)
                        .endFrom(i, v -> v.setRefId(i.getCaseEventActionId())
                                .setActionExpresssions(mapExressions.get(i.getCaseEventActionId()))));
        mapExressions.clear();
        return CopyWrapper.create(CaseEventInfoResponse::new)
                .endFrom(refreshCateg(row))
                .setConditionExpresssions(conditions)
                .setEffectExpresssions(effects)
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
        final String appId = saveCaseEvent.getAppId();
        LoginContextVO voLogin = ShareBiz.getLoginUser(request);
        //TODO checkLogin
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveCaseEvent.getCaseEventId())
                        && caseEventDao.getById(saveCaseEvent.getCaseEventId(), CaseEventEntity::getId).isEmpty())
                .throwMessage("人物事件不存在或已删除");
        CategVO categVO = null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveCaseEvent.getEventCategId())
                        || null == (categVO = getCategCache().getById(appId, saveCaseEvent.getEventCategId())))
                .throwMessage("事件类别不存在");
        EnumEventTriggerType triggerType = EnumEventTriggerType.of(saveCaseEvent.getTriggerType());
      /*  AssertUtil.trueThenThrow(triggerType == EnumEventTriggerType.CONDITION && ShareUtil.XCollection.notEmpty(saveCaseEvent.getEffectExpresssions()))
                .throwMessage("条件触发时不支持定义影响的指标");
        AssertUtil.trueThenThrow(triggerType != EnumEventTriggerType.CONDITION && ShareUtil.XCollection.notEmpty(saveCaseEvent.getConditionExpresssionIds()))
                .throwMessage("时间触发时不支持定义触发条件");*/
        AssertUtil.trueThenThrow(triggerType != EnumEventTriggerType.CONDITION && EnumEventTriggerSpan.of(saveCaseEvent.getTriggerSpan()) == EnumEventTriggerSpan.NONE)
                .throwMessage("请选择正确的触发时间段");


        //重复指标检查
        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveCaseEvent.getEffectExpresssions())
                        && saveCaseEvent.getEffectExpresssions().stream()
                        .map(IndicatorExpressionVO::getIndicatorInstanceId)
                        .collect(Collectors.toSet())
                        .size() < saveCaseEvent.getEffectExpresssions().size())
                .throwMessage("存在重复的影响指标，请检查");
        saveCaseEvent.setActions(ShareUtil.XObject.defaultIfNull(saveCaseEvent.getActions(), new ArrayList<>()));
        saveCaseEvent.getActions().forEach(item -> {
            AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(item.getActionExpresssions()) && item.getActionExpresssions().stream()
                            .map(IndicatorExpressionVO::getIndicatorInstanceId)
                            .collect(Collectors.toSet()).size() < item.getActionExpresssions().size())
                    .throwMessage(String.format("措施\"%s\"存在重复的关联指标", item.getActionDesc()));

        });

        //build po
        if (ShareUtil.XObject.isEmpty(saveCaseEvent.getCaseEventId())) {
            saveCaseEvent.setCaseEventId(idGenerator.nextIdStr());
        }
        saveCaseEvent.getActions().forEach(i -> i.setRefId(ShareUtil.XString.defaultIfEmpty(i.getRefId(), () -> idGenerator.nextIdStr())));
        CaseEventEntity row = CopyWrapper.create(CaseEventEntity::new)
                .endFrom(saveCaseEvent)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath())
                .setCategIdLv1(categVO.getCategIdLv1())
                .setCategNameLv1(categVO.getCategNameLv1())
                .setCreateAccountId(voLogin.getAccountId())
                .setCreateAccountName(voLogin.getAccountName())
                .setTriggerType(triggerType.getCode());
        List<CaseEventActionEntity> rowActions = ShareUtil.XCollection.map(saveCaseEvent.getActions(), i ->
                CopyWrapper.create(CaseEventActionEntity::new)
                        .endFrom(i)
                        .setCaseEventActionId(i.getRefId()));

        //expression
        final String caseEventId = row.getCaseEventId();
        Map<String, List<String>> mapExpressions = new HashMap<>();
        if (ShareUtil.XObject.notEmpty(saveCaseEvent.getConditionExpresssionIds())) {
            mapExpressions.computeIfAbsent(caseEventId, key -> new ArrayList<>()).addAll(saveCaseEvent.getConditionExpresssionIds());
        }
        if (ShareUtil.XObject.notEmpty(saveCaseEvent.getEffectExpresssions())) {
            List<String> dst = mapExpressions.computeIfAbsent(caseEventId, key -> new ArrayList<>());
            saveCaseEvent.getEffectExpresssions().forEach(i -> {
                if(ShareUtil.XObject.isEmpty(i.getIndicatorExpressionId())){
                    return;
                }
                dst.add(i.getIndicatorExpressionId());
            });
        }
        saveCaseEvent.getActions().forEach(i -> {
            if (ShareUtil.XObject.isEmpty(i.getActionExpresssions())) {
                return;
            }
            List<String> dst = mapExpressions.computeIfAbsent(i.getRefId(), key -> new ArrayList<>());
            i.getActionExpresssions().forEach(vo -> dst.add(vo.getIndicatorExpressionId()));
        });
        return caseEventDao.tranSave(row, rowActions, mapExpressions);
    }

    public boolean copyCaseEvent4Person(String appId, String dstAccountId,String srcAccountId , String personName) {
        List<CaseEventEntity> rowsEvent=caseEventDao.getCaseEventsByPersonIds(List.of(srcAccountId));
        if(ShareUtil.XObject.isEmpty(rowsEvent)){
            return true;
        }
        HttpServletRequest request= ShareBiz.getHttpRequest();
        LoginContextVO voLogin = ShareBiz.getLoginUser(request);
        List<CasePersonEntity> rowsCasePerson=casePersonDao.getByAccountIds(List.of(srcAccountId),
                CasePersonEntity::getCaseInstanceId,
                CasePersonEntity::getCasePersonId,
                CasePersonEntity::getAccountId
        );
        final String caseInstanceId=ShareUtil.XObject.notEmpty(rowsCasePerson)&&rowsCasePerson.size()>0?rowsCasePerson.get(0).getCaseInstanceId():null;
        final List<String> eventIds=ShareUtil.XCollection.map(rowsEvent, CaseEventEntity::getCaseEventId);
        List<CaseEventActionEntity> rowsAction = caseEventActionDao.getByEventIds(eventIds);
        final List<String> reasonIds=new ArrayList<>(eventIds);
        rowsAction.forEach(i->reasonIds.add(i.getCaseEventActionId()));
        List<CaseIndicatorExpressionRefEntity> rowsExpressionRef = caseIndicatorExpressionRefDao.getByReasonId(appId, reasonIds);
        List<String> expressionIds = ShareUtil.XCollection.map(rowsExpressionRef, CaseIndicatorExpressionRefEntity::getIndicatorExpressionId);
        List<CaseIndicatorExpressionEntity> rowsExpression = caseIndicatorExpressionDao.getByIds(expressionIds);
        Map<String, String> mapDstIndicatorId = ShareUtil.XCollection.toMap(caseIndicatorInstanceDao.getByPersonId(appId, dstAccountId,
                        CaseIndicatorInstanceEntity::getIndicatorInstanceId,
                        CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId),
                CaseIndicatorInstanceEntity::getIndicatorInstanceId, CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId);
        Map<String, String> mapSrcIndicatorId = ShareUtil.XCollection.toMap(caseIndicatorInstanceDao.getByPersonId(appId, srcAccountId,
                        CaseIndicatorInstanceEntity::getIndicatorInstanceId,
                        CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId),
                CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId, CaseIndicatorInstanceEntity::getIndicatorInstanceId);

        Map<String,String> mapIndicatorId=new HashMap<>();
        mapSrcIndicatorId.forEach((k,v)->{
            String dstIndicatorId=mapDstIndicatorId.get(v);
            if(ShareUtil.XObject.isEmpty(dstIndicatorId)){
                assertNotExistsCaseIndicatorId(k);
                return;
            }
            mapIndicatorId.put(k,dstIndicatorId);
        });
        List<String> minMaxExpressionItemIds=new ArrayList<>();
        rowsExpression.forEach(i->{
            if(ShareUtil.XObject.notEmpty(i.getPrincipalId())){
                String caseIndicatorId=mapIndicatorId.get(i.getPrincipalId());
                if(ShareUtil.XObject.isEmpty(caseIndicatorId)){
                    assertNotExistsCaseIndicatorId(i.getPrincipalId());
                }
            }
            if(ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())){
                minMaxExpressionItemIds.add(i.getMinIndicatorExpressionItemId());
            }
            if(ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())){
                minMaxExpressionItemIds.add(i.getMaxIndicatorExpressionItemId());
            }
        });
        mapIndicatorId.put("", "");
        mapIndicatorId.put(null, "");
        List<CaseIndicatorExpressionItemEntity> rowsExpressionItem = caseIndicatorExpressionDao.getSubByLeadIds(expressionIds);
        if(minMaxExpressionItemIds.size()>0){
            rowsExpressionItem.addAll(caseIndicatorExpressionDao.getSubBySubIds(minMaxExpressionItemIds));
        }
        rowsExpressionItem.forEach(i->{
            i.setConditionValList(castExpresionVals(i.getConditionValList(), mapIndicatorId,true));
            i.setResultValList(castExpresionVals(i.getResultValList(), mapIndicatorId,true));
        });


        Map<String, String> mapEventIds = new HashMap<>();
        Map<String, String> mapExpressionId = new HashMap<>();
        Map<String, String> mapExpresionItemId = new HashMap<>();
        mapExpresionItemId.put("", "");
        mapExpresionItemId.put(null, "");
        List<CaseEventEntity> rowsCaseEvent = ShareUtil.XCollection.map(rowsEvent, i ->
                CopyWrapper.create(CaseEventEntity::new)
                        .endFrom(i)
                        .setDt(new Date())
                        .setCaseInstanceId(null!=caseInstanceId?caseInstanceId:i.getCaseInstanceId())
                        .setPersonId(dstAccountId)
                        .setPersonName(personName)
                        .setCreateAccountId(voLogin.getAccountId())
                        .setCreateAccountName(voLogin.getAccountName())
                        .setState(EnumStatus.ENABLE.getCode())
                        .setCaseEventId(mapEventIds.computeIfAbsent(i.getCaseEventId(), v -> idGenerator.nextIdStr()))
                        .setId(null)
        );
        Map<String,String> mapReasonId=new HashMap<>(mapEventIds);
        List<CaseEventActionEntity> rowsCaseAction = ShareUtil.XCollection.map(rowsAction, i ->
                CopyWrapper.create(CaseEventActionEntity::new)
                        .endFrom(i)
                        .setCaseEventId(mapEventIds.get(i.getCaseEventId()))
                        .setCaseEventActionId(mapReasonId.computeIfAbsent(i.getCaseEventActionId(), k-> idGenerator.nextIdStr()))
                        .setId(null)
        );
        List<CaseIndicatorExpressionEntity> rowsCaseExpression = ShareUtil.XCollection.map(rowsExpression, i ->
                CopyWrapper.create(CaseIndicatorExpressionEntity::new)
                        .endFrom(i)
                        .setCasePrincipalId(mapIndicatorId.get(i.getPrincipalId()))
                        .setMaxIndicatorExpressionItemId(mapExpresionItemId.computeIfAbsent(i.getMaxIndicatorExpressionItemId(), v -> idGenerator.nextIdStr()))
                        .setMinIndicatorExpressionItemId(mapExpresionItemId.computeIfAbsent(i.getMinIndicatorExpressionItemId(), v -> idGenerator.nextIdStr()))
                        .setCaseIndicatorExpressionId(mapExpressionId.computeIfAbsent(i.getCaseIndicatorExpressionId(), v -> idGenerator.nextIdStr()))
                        .setId(null)
        );
        List<CaseIndicatorExpressionItemEntity> rowsCaseExpressionItem = ShareUtil.XCollection.map(rowsExpressionItem, i ->
                CopyWrapper.create(CaseIndicatorExpressionItemEntity::new)
                        .endFrom(i)
                        .setIndicatorExpressionId(mapExpressionId.get(i.getIndicatorExpressionId()))
                        .setCaseIndicatorExpressionItemId(mapExpresionItemId.computeIfAbsent(i.getCaseIndicatorExpressionItemId(), v -> idGenerator.nextIdStr()))
                        .setId(null)
        );
        List<CaseIndicatorExpressionRefEntity> rowsCaseExpressionRef = ShareUtil.XCollection.map(rowsExpressionRef, i ->
                CopyWrapper.create(CaseIndicatorExpressionRefEntity::new)
                        .endFrom(i)
                        .setIndicatorExpressionRefId(i.getIndicatorExpressionRefId())
                        .setCaseIndicatorExpressionRefId(idGenerator.nextIdStr())
                        .setReasonId(mapReasonId.get(i.getReasonId()))
                        .setIndicatorExpressionId(mapExpressionId.get(i.getIndicatorExpressionId()))
                        .setId(null)
        );

        mapReasonId.clear();
        mapIndicatorId.clear();
        mapEventIds.clear();
        mapExpressionId.clear();
        mapExpresionItemId.clear();
        return caseEventDao.tranSaveBatch(rowsCaseEvent, rowsCaseAction, rowsCaseExpressionRef, rowsCaseExpression,rowsCaseExpressionItem);
    }

    /**
     * 批量添加事件
     * @param saveCaseEvent
     * @param request
     * @return
     */
    public Boolean copyCaseEvent(CopyCaseEventRequest saveCaseEvent , HttpServletRequest request) {
        LoginContextVO voLogin = ShareBiz.getLoginUser(request);
        final String appId = saveCaseEvent.getAppId();
        final List<String> eventIds = saveCaseEvent.getIds();
        final String personId = saveCaseEvent.getPersonId();
        final String personName = saveCaseEvent.getPersonName();
        final String caseInstanceId = saveCaseEvent.getCaseInstanceId();
        List<EventEntity> rowsEvent = eventDao.getByIds(eventIds);
        AssertUtil.trueThenThrow(ShareUtil.XCollection.isEmpty(rowsEvent))
                .throwMessage("数据库事件不存在或已删除");
        List<EventActionEntity> rowsAction = eventActionDao.getByEventIds(eventIds);
        final List<String> reasonIds=new ArrayList<>(eventIds);
        rowsAction.forEach(i->reasonIds.add(i.getEventActionId()));
        List<IndicatorExpressionRefEntity> rowsExpressionRef = indicatorExpressionRefDao.getByReasonId(appId, reasonIds);
        List<String> expressionIds = ShareUtil.XCollection.map(rowsExpressionRef, IndicatorExpressionRefEntity::getIndicatorExpressionId);
        List<IndicatorExpressionEntity> rowsExpression = indicatorExpressionDao.getByIds(expressionIds);
        Map<String, String> mapIndicatorId = ShareUtil.XCollection.toMap(caseIndicatorInstanceDao.getByPersonId(appId, personId,
                        CaseIndicatorInstanceEntity::getIndicatorInstanceId,
                        CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId),
                CaseIndicatorInstanceEntity::getIndicatorInstanceId, CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId);
        List<String> minMaxExpressionItemIds=new ArrayList<>();
        rowsExpression.forEach(i->{
            if(ShareUtil.XObject.notEmpty(i.getPrincipalId())){
                String caseIndicatorId=mapIndicatorId.get(i.getPrincipalId());
                if(ShareUtil.XObject.isEmpty(caseIndicatorId)){
                    assertNotExistsIndicatorId(i.getPrincipalId());
                }
            }
            if(ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())){
                minMaxExpressionItemIds.add(i.getMinIndicatorExpressionItemId());
            }
            if(ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())){
                minMaxExpressionItemIds.add(i.getMaxIndicatorExpressionItemId());
            }
        });
        mapIndicatorId.put("", "");
        mapIndicatorId.put(null, "");
        List<IndicatorExpressionItemEntity> rowsExpressionItem = indicatorExpressionDao.getSubByLeadIds(expressionIds);
        if(minMaxExpressionItemIds.size()>0){
            rowsExpressionItem.addAll(indicatorExpressionDao.getSubBySubIds(minMaxExpressionItemIds));
        }
        rowsExpressionItem.forEach(i->{
            i.setConditionValList(castExpresionVals(i.getConditionValList(), mapIndicatorId,false));
            i.setResultValList(castExpresionVals(i.getResultValList(), mapIndicatorId,false));
        });

        Map<String, String> mapEventIds = new HashMap<>();
        Map<String, String> mapExpressionId = new HashMap<>();
        Map<String, String> mapExpresionItemId = new HashMap<>();
        mapExpresionItemId.put("", "");
        mapExpresionItemId.put(null, "");
        List<CaseEventEntity> rowsCaseEvent = ShareUtil.XCollection.map(rowsEvent, i ->
                CopyWrapper.create(CaseEventEntity::new)
                        .endFrom(i)
                        .setDt(new Date())
                        .setCaseInstanceId(caseInstanceId)
                        .setPersonId(personId)
                        .setPersonName(personName)
                        .setCreateAccountId(voLogin.getAccountId())
                        .setCreateAccountName(voLogin.getAccountName())
                        .setState(EnumStatus.ENABLE.getCode())
                        .setCaseEventName(i.getEventName())
                        .setCaseEventId(mapEventIds.computeIfAbsent(i.getEventId(), k -> idGenerator.nextIdStr()))
                        .setId(null)
        );
        Map<String,String> mapReasonId=new HashMap<>(mapEventIds);
        List<CaseEventActionEntity> rowsCaseAction = ShareUtil.XCollection.map(rowsAction, i ->
                CopyWrapper.create(CaseEventActionEntity::new)
                        .endFrom(i)
                        .setCaseEventId(mapEventIds.get(i.getEventId()))
                        .setCaseEventActionId(mapReasonId.computeIfAbsent(i.getEventActionId(), k-> idGenerator.nextIdStr()))
                        .setId(null)
        );
        List<CaseIndicatorExpressionEntity> rowsCaseExpression = ShareUtil.XCollection.map(rowsExpression, i ->
                CopyWrapper.create(CaseIndicatorExpressionEntity::new)
                        .endFrom(i)
                        .setCasePrincipalId(mapIndicatorId.get(i.getPrincipalId()))
                        .setMaxIndicatorExpressionItemId(mapExpresionItemId.computeIfAbsent(i.getMaxIndicatorExpressionItemId(), v -> idGenerator.nextIdStr()))
                        .setMinIndicatorExpressionItemId(mapExpresionItemId.computeIfAbsent(i.getMinIndicatorExpressionItemId(), v -> idGenerator.nextIdStr()))
                        .setCaseIndicatorExpressionId(mapExpressionId.computeIfAbsent(i.getIndicatorExpressionId(), v -> idGenerator.nextIdStr()))
                        .setId(null)
        );
        List<CaseIndicatorExpressionItemEntity> rowsCaseExpressionItem = ShareUtil.XCollection.map(rowsExpressionItem, i ->
                CopyWrapper.create(CaseIndicatorExpressionItemEntity::new)
                        .endFrom(i)
                        .setIndicatorExpressionId(mapExpressionId.get(i.getIndicatorExpressionId()))
                        .setCaseIndicatorExpressionItemId(mapExpresionItemId.computeIfAbsent(i.getIndicatorExpressionItemId(), v -> idGenerator.nextIdStr()))
                        .setId(null)
        );
        List<CaseIndicatorExpressionRefEntity> rowsCaseExpressionRef = ShareUtil.XCollection.map(rowsExpressionRef, i ->
                CopyWrapper.create(CaseIndicatorExpressionRefEntity::new)
                        .endFrom(i)
                        .setIndicatorExpressionRefId(i.getIndicatorExpressionRefId())
                        .setCaseIndicatorExpressionRefId(idGenerator.nextIdStr())
                        .setReasonId(mapReasonId.get(i.getReasonId()))
                        .setIndicatorExpressionId(mapExpressionId.get(i.getIndicatorExpressionId()))
                        .setId(null)
        );

        mapReasonId.clear();
        mapIndicatorId.clear();
        mapEventIds.clear();
        mapExpressionId.clear();
        mapExpresionItemId.clear();
        return caseEventDao.tranSaveBatch(rowsCaseEvent, rowsCaseAction, rowsCaseExpressionRef, rowsCaseExpression,rowsCaseExpressionItem);
    }
    private String castExpresionVals(String expressionVals,Map<String,String> mapIndicatorId,boolean isFromCase ){
        if(ShareUtil.XObject.isEmpty(expressionVals)){
            return expressionVals;
        }
        String[] spilts=expressionVals.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        String caseIndicatorId;
        for(String indicatorId:spilts){
            caseIndicatorId=mapIndicatorId.get(indicatorId);
            if(ShareUtil.XObject.isEmpty(caseIndicatorId)){
                if(isFromCase) {
                    assertNotExistsCaseIndicatorId(indicatorId);
                } else{
                    assertNotExistsIndicatorId(indicatorId);
                }
                continue;
            }
            expressionVals=expressionVals.replace(indicatorId, caseIndicatorId);
        }
        return expressionVals;
    }
    private void assertNotExistsCaseIndicatorId(String caseIndicatorId){
        String notExistsIndicator = caseIndicatorInstanceDao.getById(caseIndicatorId, CaseIndicatorInstanceEntity::getIndicatorName)
                .map(CaseIndicatorInstanceEntity::getIndicatorName).orElse("id:".concat(caseIndicatorId));
        AssertUtil.justThrow(String.format("人物找不到关联指标[%s]", notExistsIndicator));
    }
    private void assertNotExistsIndicatorId(String indicatorId){
        String notExistsIndicator = indicatorInstanceDao.getById(indicatorId, IndicatorInstanceEntity::getIndicatorName)
                .map(IndicatorInstanceEntity::getIndicatorName).orElse("id:".concat(indicatorId));
        AssertUtil.justThrow(String.format("人物未定义关联指标[%s]", notExistsIndicator));
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
        return caseIndicatorExpressionRefDao.tranDeleteByExpressionId(delRefItemRequest.getIds());
    }

    /**
     * 删除处理措施
     * @param delRefItemRequest
     * @return
     */
    public Boolean delRefAction(DelRefItemRequest delRefItemRequest){
        caseEventActionDao.tranDelete(delRefItemRequest.getIds());
        return caseIndicatorExpressionRefDao.tranDeleteByReasonId(delRefItemRequest.getIds());
    }

    /**
     * 删除事件影响指标
     * @param delRefItemRequest
     * @return
     */

    public Boolean delRefEventIndicator(DelRefItemRequest delRefItemRequest){
        return caseIndicatorExpressionRefDao.tranDeleteByExpressionId(delRefItemRequest.getIds());
    }

    /**
     * 删除处理措施影响指标
     * @param delRefItemRequest
     * @return
     */

    public Boolean delRefActionIndicator(DelRefItemRequest delRefItemRequest){
        return caseIndicatorExpressionRefDao.tranDeleteByExpressionId(delRefItemRequest.getIds());
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
        CategVO cacheItem = getCategCache().getById(src.getAppId(), src.getEventCategId());
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