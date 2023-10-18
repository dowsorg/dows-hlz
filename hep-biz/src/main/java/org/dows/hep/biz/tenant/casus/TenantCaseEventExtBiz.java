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

import static org.dows.hep.biz.base.indicator.CaseIndicatorInstanceExtBiz.checkNullNewId;

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
    public Map<String, String> duplicateCaseEventForPerson(String appId, String oldAccountId, String newAccountId, String personName) throws ExecutionException, InterruptedException {
        List<CaseEventEntity> caseEventList = caseEventDao.getCaseEventsByPersonId(appId, oldAccountId);
        Map<String, String> kOldIdVNewIdMap = new HashMap<>();

        if (CollectionUtils.isEmpty(caseEventList)) {
            return kOldIdVNewIdMap;
        }
        Set<String> reasonId = new HashSet<>();
        Set<String> caseEventIdSet = caseEventList.stream().map(CaseEventEntity::getCaseEventId).collect(Collectors.toSet());

        List<CaseEventActionEntity> caseEventActionEntityList = caseEventActionDao.getByEventIds(List.copyOf(caseEventIdSet));

        Set<String> caseEventActionIdSet = caseEventActionEntityList.stream().map(CaseEventActionEntity::getCaseEventActionId).collect(Collectors.toSet());
        reasonId.addAll(caseEventIdSet);
        reasonId.addAll(caseEventActionIdSet);
        //生成新的id

        CompletableFuture<Void> cfPopulateKOldIdVNewIdMap = CompletableFuture.runAsync(() -> {
            rsUtilBiz.populateKOldIdVNewIdMap(kOldIdVNewIdMap, reasonId);
        });
        cfPopulateKOldIdVNewIdMap.get();

        CompletableFuture<Void> copyCaseEvent = CompletableFuture.runAsync(() -> {
            caseEventList.forEach(caseEventEntity -> {
                caseEventEntity.setCaseEventId(checkNullNewId(caseEventEntity.getCaseEventId(), kOldIdVNewIdMap));
                caseEventEntity.setPersonId(newAccountId);
                caseEventEntity.setPersonName(personName);
                caseEventEntity.setId(null);
                caseEventEntity.setDt(new Date());
            });
        });

        CompletableFuture<Void> copyCaseEventAction = CompletableFuture.runAsync(() -> {
            caseEventActionEntityList.forEach(caseEventAction -> {
                caseEventAction.setCaseEventActionId(checkNullNewId(caseEventAction.getCaseEventActionId(), kOldIdVNewIdMap));
                caseEventAction.setCaseEventId(checkNullNewId(caseEventAction.getCaseEventId(), kOldIdVNewIdMap));
                caseEventAction.setId(null);
                caseEventAction.setDt(new Date());
            });
        });
        CompletableFuture.allOf(copyCaseEvent, copyCaseEventAction).get();

        caseEventDao.tranSaveBatch(caseEventList, caseEventActionEntityList, null, null, null);
        return kOldIdVNewIdMap;
    }


}
