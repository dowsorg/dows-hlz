package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.service.ExperimentSettingService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:56
 */

@Component
public class ExperimentSettingDao extends BaseDao<ExperimentSettingService, ExperimentSettingEntity> {
    public ExperimentSettingDao(){
        super("实验时间设置不存在");
    }

    @Override
    protected SFunction<ExperimentSettingEntity, String> getColAppId() {
        return ExperimentSettingEntity::getAppId;
    }

    @Override
    protected SFunction<ExperimentSettingEntity, String> getColId() {
        return null;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentSettingEntity item) {
        return null;
    }

    @Override
    protected SFunction<ExperimentSettingEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentSettingEntity item) {
        return null;
    }

    public List<ExperimentSettingEntity> getByExperimentId(String appId,String experimentId,String configKey,
                                                           SFunction<ExperimentSettingEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId), getColAppId(),appId)
                .eq(ExperimentSettingEntity::getExperimentInstanceId,experimentId)
                .eq(ShareUtil.XObject.notEmpty(configKey), ExperimentSettingEntity::getConfigKey,configKey)
                .select(cols)
                .list();
    }
}
