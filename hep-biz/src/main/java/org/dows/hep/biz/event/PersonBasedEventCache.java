package org.dows.hep.biz.event;

import org.dows.hep.api.enums.EnumExperimentEventState;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentEventDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.PersonBasedEventCollection;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEventEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:30
 */
@Component
public class PersonBasedEventCache extends BaseLoadingCache<ExperimentCacheKey, PersonBasedEventCollection> {
    private static volatile PersonBasedEventCache s_instance;

    public static PersonBasedEventCache Instance(){
        return s_instance;
    }
    protected final static int CACHEInitCapacity=2;
    protected final static int CACHEMaxSize=10;
    protected final static int CACHEExpireSeconds=60*60*12;

    @Autowired
    private ExperimentEventDao experimentEventDao;

    private PersonBasedEventCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }

    @Override
    protected PersonBasedEventCollection load(ExperimentCacheKey key) {
        PersonBasedEventCollection rst = new PersonBasedEventCollection()
                .setExperimentInstanceId(key.getExperimentInstanceId());
        List<ExperimentEventEntity> rowsEvent = experimentEventDao.getConditionEventByExperimentId(key.getAppId(), key.getExperimentInstanceId(),
                null, EnumExperimentEventState.INIT.getCode(),
                ExperimentEventEntity::getId,
                ExperimentEventEntity::getAppId,
                ExperimentEventEntity::getExperimentEventId,
                ExperimentEventEntity::getExperimentInstanceId,
                ExperimentEventEntity::getExperimentGroupId,
                ExperimentEventEntity::getExperimentOrgId,
                ExperimentEventEntity::getExperimentPersonId,
                ExperimentEventEntity::getAccountId,
                ExperimentEventEntity::getPersonName,
                ExperimentEventEntity::getPeriods,
                ExperimentEventEntity::getCasePersonId,
                ExperimentEventEntity::getCaseEventId,
                ExperimentEventEntity::getTriggerType,
                ExperimentEventEntity::getTriggerTime,
                ExperimentEventEntity::getTriggerGameDay,
                ExperimentEventEntity::getState
        );
        if (ShareUtil.XCollection.isEmpty(rowsEvent)) {
            return rst;
        }
        Map<String, List<ExperimentEventEntity>> mapEvents = ShareUtil.XCollection.groupBy(rowsEvent, ExperimentEventEntity::getExperimentPersonId);
        List<PersonBasedEventCollection.PersonBasedEventGroup> groups = new ArrayList<>();
        mapEvents.forEach((k, v) -> groups.add(PersonBasedEventCollection.PersonBasedEventGroup.builder()
                .experimentPersonId(k)
                .eventItems(v)
                .build()));
        rst.setEventGroups(groups);
        mapEvents.clear();
        rowsEvent.clear();
        return rst;
    }
}
