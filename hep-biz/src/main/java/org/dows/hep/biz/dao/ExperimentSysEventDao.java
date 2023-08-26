package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.dows.hep.service.ExperimentSysEventService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/8/23 10:38
 */
@Component
public class ExperimentSysEventDao extends BaseDao<ExperimentSysEventService, ExperimentSysEventEntity> {


    protected ExperimentSysEventDao() {
        super("系统任务不存在", "系统任务保存失败");
    }

    @Override
    protected SFunction<ExperimentSysEventEntity, String> getColAppId() {
        return ExperimentSysEventEntity::getAppId;
    }

    @Override
    protected SFunction<ExperimentSysEventEntity, String> getColId() {
        return null;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentSysEventEntity item) {
        return null;
    }

    @Override
    protected SFunction<ExperimentSysEventEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentSysEventEntity item) {
        return null;
    }

    public List<ExperimentSysEventEntity> getByExperimentId(String appId,String experimentId,Integer state,
                                                            SFunction<ExperimentSysEventEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId),ExperimentSysEventEntity::getAppId, appId)
                .eq(ExperimentSysEventEntity::getExperimentInstanceId,experimentId)
                .eq(ShareUtil.XObject.notEmpty(state),ExperimentSysEventEntity::getState,state)
                .orderByAsc(ExperimentSysEventEntity::getPeriods,ExperimentSysEventEntity::getDealSeq)
                .select(cols)
                .list();
    }

}
