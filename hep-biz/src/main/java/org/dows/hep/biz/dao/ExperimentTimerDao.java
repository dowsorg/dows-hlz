package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/6/18 18:15
 */
@Component
public class ExperimentTimerDao extends BaseDao<ExperimentTimerService, ExperimentTimerEntity>{
    public ExperimentTimerDao(){
        super("实验计时器不存在");
    }

    @Override
    protected SFunction<ExperimentTimerEntity, String> getColAppId() {
        return ExperimentTimerEntity::getAppId;
    }

    @Override
    protected SFunction<ExperimentTimerEntity, String> getColId() {
        return null;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentTimerEntity item) {
        return null;
    }

    @Override
    protected SFunction<ExperimentTimerEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentTimerEntity item) {
        return null;
    }

    /**
     * 获取实验timer
     * @param appId
     * @param experimentId
     * @param period
     * @param cols
     * @return
     */
    public List<ExperimentTimerEntity> getByExperimentId(String appId,String experimentId,Integer period,
                                                         SFunction<ExperimentTimerEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId), ExperimentTimerEntity::getAppId,appId)
                .eq(ExperimentTimerEntity::getExperimentInstanceId,experimentId)
                .eq(ShareUtil.XObject.notEmpty(period),ExperimentTimerEntity::getPeriod,period)
                .orderByDesc(ExperimentTimerEntity::getStartTime, ExperimentTimerEntity::getPauseCount)
                .select(cols)
                .list();

    }

    /**
     * 获取实验当前timer
     * @param appId
     * @param experimentId
     * @param timeStamp
     * @param cols
     * @return
     */

    public Optional<ExperimentTimerEntity> getCurPeriodByExperimentId(String appId, String experimentId, Long timeStamp,
                                                                      SFunction<ExperimentTimerEntity,?>... cols) {
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId), ExperimentTimerEntity::getAppId, appId)
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentId)
                .le(ExperimentTimerEntity::getStartTime, timeStamp)
                .ge(ExperimentTimerEntity::getEndTime, timeStamp)
                .orderByDesc(ExperimentTimerEntity::getStartTime, ExperimentTimerEntity::getPauseCount)
                .select(cols)
                .last("limit 1")
                .oneOpt();

    }
}
