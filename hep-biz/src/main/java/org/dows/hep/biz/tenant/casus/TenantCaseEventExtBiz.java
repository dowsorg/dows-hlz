package org.dows.hep.biz.tenant.casus;

import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.RsUtilBiz;
import org.dows.hep.biz.dao.CaseEventActionDao;
import org.dows.hep.biz.dao.CaseEventDao;
import org.dows.hep.entity.CaseEventActionEntity;
import org.dows.hep.entity.CaseEventEntity;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 人物突发事件复制
 *
 * @description: lifel 2023/10/11
 */
@Service
@RequiredArgsConstructor
public class TenantCaseEventExtBiz {

    private final CaseEventDao caseEventDao;
    private final CaseEventActionDao caseEventActionDao;
    private final RsUtilBiz rsUtilBiz;
    private final IdGenerator idGenerator;
    @Transactional(rollbackFor = Exception.class)
    public void copyCaseEventForPerson(String appId, String oldAccountId, String newAccountId, String personName) throws ExecutionException, InterruptedException {
        List<CaseEventEntity> caseEventList = caseEventDao.getCaseEventsByPersonId(appId, oldAccountId);
        if (CollectionUtils.isEmpty(caseEventList)){
            return;
        }

        Set<String> caseEventId = caseEventList.stream().map(CaseEventEntity::getCaseEventId).collect(Collectors.toSet());
        List<CaseEventActionEntity> caseEventActionEntityList = caseEventActionDao.getByEventIds(List.copyOf(caseEventId));

        //生成新的id
        Map<String, String> kOldIdVNewIdMap = new HashMap<>();
        CompletableFuture<Void> cfPopulateKOldIdVNewIdMap = CompletableFuture.runAsync(() -> {
            rsUtilBiz.populateKOldIdVNewIdMap(kOldIdVNewIdMap, caseEventId);
        });
        cfPopulateKOldIdVNewIdMap.get();

        List<CaseEventEntity> newCaseEventList = new ArrayList<>();
        CompletableFuture<Void> copyCaseEvent = CompletableFuture.runAsync(() -> {
            caseEventList.forEach(caseEventEntity -> {
                caseEventEntity.setCaseEventId(kOldIdVNewIdMap.get(caseEventEntity.getCaseEventId()));
                caseEventEntity.setPersonId(newAccountId);
                caseEventEntity.setPersonName(personName);
                caseEventEntity.setId(null);
                newCaseEventList.add(caseEventEntity);
            });
        });
        copyCaseEvent.get();

        List<CaseEventActionEntity> newCaseEventActionEntityList = new ArrayList<>();
        CompletableFuture<Void> copyCaseEventAction = CompletableFuture.runAsync(() -> {
            caseEventActionEntityList.forEach(caseEventAction -> {
                caseEventAction.setCaseEventActionId(idGenerator.nextIdStr());
                caseEventAction.setCaseEventId(kOldIdVNewIdMap.get(caseEventAction.getCaseEventId()));
                caseEventAction.setId(null);
                newCaseEventActionEntityList.add(caseEventAction);
            });
        });
        copyCaseEventAction.get();

        caseEventDao.tranSaveBatch(newCaseEventList,newCaseEventActionEntityList,null,null,null);

    }



}
