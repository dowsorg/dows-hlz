package org.dows.hep.biz.dao;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSnapshotRefEntity;
import org.dows.hep.service.ExperimentSnapshotRefService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author : wuzl
 * @date : 2023/6/28 16:18
 */
@Component
public class ExperimentSnapshotRefDao extends BaseDao<ExperimentSnapshotRefService, ExperimentSnapshotRefEntity>{

    public ExperimentSnapshotRefDao(){
        super("实验快照关联不存在");
    }

    @Override
    protected SFunction<ExperimentSnapshotRefEntity, String> getColAppId() {
        return ExperimentSnapshotRefEntity::getAppId;
    }


    @Override
    protected SFunction<ExperimentSnapshotRefEntity, String> getColId() {
        return ExperimentSnapshotRefEntity::getExperimentSnapshotRefId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentSnapshotRefEntity item) {
        return item::setExperimentSnapshotRefId;
    }

    @Override
    protected SFunction<ExperimentSnapshotRefEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentSnapshotRefEntity item) {
        return null;
    }


    public Optional<ExperimentSnapshotRefEntity> getByMd5(String appId,String snapShotType,String md5,
                                                          SFunction<ExperimentSnapshotRefEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId),ExperimentSnapshotRefEntity::getAppId,appId)
                .eq(ExperimentSnapshotRefEntity::getSnapshotType,snapShotType)
                .eq(ExperimentSnapshotRefEntity::getMd5, md5)
                .isNull(ExperimentSnapshotRefEntity::getRefExperimentInstanceId)
                .select(cols)
                .orderByDesc(ExperimentSnapshotRefEntity::getId)
                .last("limit 1")
                .oneOpt();
    }
    public Optional<ExperimentSnapshotRefEntity> getByExperimentId(String appId,String snapShotType, String experimentId,
                                                                   SFunction<ExperimentSnapshotRefEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId),ExperimentSnapshotRefEntity::getAppId,appId)
                .eq(ExperimentSnapshotRefEntity::getSnapshotType,snapShotType)
                .eq(ExperimentSnapshotRefEntity::getExperimentInstanceId,experimentId)
                .select(cols)
                .orderByDesc(ExperimentSnapshotRefEntity::getId)
                .last("limit 1")
                .oneOpt();
    }
    @DSTransactional
    public boolean tranSave(ExperimentSnapshotRefEntity item, Supplier<Boolean> saveOthers){
        if(!saveOthers.get()){
            return false;
        }
        AssertUtil.falseThenThrow(service.save(item))
                .throwMessage("快照关联数据保存失败");
        return true;
    }



}
