package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEventEntity;
import org.dows.hep.service.ExperimentEventService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author : wuzl
 * @date : 2023/6/18 18:28
 */
@Component
public class ExperimentEventDao extends BaseDao<ExperimentEventService, ExperimentEventEntity> {
    public ExperimentEventDao(){
        super("实验事件不存在");
    }

    @Override
    protected SFunction<ExperimentEventEntity, String> getColAppId() {
        return ExperimentEventEntity::getAppId;
    }

    @Override
    protected SFunction<ExperimentEventEntity, String> getColId() {
        return ExperimentEventEntity::getExperimentEventId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentEventEntity item) {
        return item::setExperimentEventId;
    }

    @Override
    protected SFunction<ExperimentEventEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentEventEntity item) {
        return null;
    }

    //region retrieve
    public List<ExperimentEventEntity> getByExperimentId(String appId,String experimentId,String experimentPersonId,Integer state,
                                                         SFunction<ExperimentEventEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ExperimentEventEntity::getAppId, appId)
                .eq(ExperimentEventEntity::getExperimentInstanceId,experimentId)
                .eq(ShareUtil.XObject.notEmpty(experimentPersonId),ExperimentEventEntity::getExperimentPersonId,experimentPersonId)
                .eq(ShareUtil.XObject.notEmpty(state),ExperimentEventEntity::getState,state)
                .select(cols)
                .list();
    }

    public List<ExperimentEventEntity> getTimeEventByExperimentId(String appId,String experimentId,String experimentPersonId,Integer state,
                                                         SFunction<ExperimentEventEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ExperimentEventEntity::getAppId, appId)
                .eq(ExperimentEventEntity::getExperimentInstanceId,experimentId)
                .eq(ShareUtil.XObject.notEmpty(experimentPersonId),ExperimentEventEntity::getExperimentPersonId,experimentPersonId)
                .eq(ShareUtil.XObject.notEmpty(state),ExperimentEventEntity::getState,state)
                .gt(ExperimentEventEntity::getTriggerType, 0)
                .orderByAsc(ExperimentEventEntity::getCasePersonId,ExperimentEventEntity::getCaseEventId)
                .select(cols)
                .list();
    }

    public List<ExperimentEventEntity> getConditionEventByExperimentId(String appId,String experimentId,String experimentPersonId,Integer state,
                                                                  SFunction<ExperimentEventEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ExperimentEventEntity::getAppId, appId)
                .eq(ExperimentEventEntity::getExperimentInstanceId,experimentId)
                .eq(ShareUtil.XObject.notEmpty(experimentPersonId),ExperimentEventEntity::getExperimentPersonId,experimentPersonId)
                .eq(ShareUtil.XObject.notEmpty(state),ExperimentEventEntity::getState,state)
                .eq(ExperimentEventEntity::getTriggerType, 0)
                .orderByAsc(ExperimentEventEntity::getCasePersonId, ExperimentEventEntity::getExperimentPersonId)
                .select(cols)
                .list();
    }
    //endregion

    //region save
    @Transactional(rollbackFor = Exception.class)
    public boolean tranSaveSnapshot(String experimentInstanceId,Collection<ExperimentEventEntity> items){
        if(ShareUtil.XObject.isEmpty(items)){
            return true;
        }
        service.lambdaUpdate()
                .eq(ExperimentEventEntity::getExperimentInstanceId, experimentInstanceId)
                .remove();
        return service.saveBatch(items);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranUpdateTriggered(Collection<ExperimentEventEntity> items, Supplier<Boolean> saveOthers) {
        if(ShareUtil.XObject.isEmpty(items)){
            return true;
        }
        AssertUtil.falseThenThrow(saveOrUpdateBatch(items,true,true))
                .throwMessage(failedSaveMessage);
        if(null!=saveOthers && !saveOthers.get()) {
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranUpdateAcction(ExperimentEventEntity item, Supplier<Boolean> saveOthers) {
        if(ShareUtil.XObject.isEmpty(item)){
            return true;
        }
        AssertUtil.falseThenThrow(saveOrUpdate(item,true))
                .throwMessage(failedSaveMessage);
        if(null!=saveOthers && !saveOthers.get()) {
            return false;
        }
        return true;
    }



    //endregion

}
