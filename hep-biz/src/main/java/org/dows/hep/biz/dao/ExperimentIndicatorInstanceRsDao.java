package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.service.ExperimentIndicatorInstanceRsService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/20 16:45
 */
@Component
public class ExperimentIndicatorInstanceRsDao extends BaseDao<ExperimentIndicatorInstanceRsService, ExperimentIndicatorInstanceRsEntity>{
    public ExperimentIndicatorInstanceRsDao() {
        super("实验人物指标不存在");
    }

    @Override
    protected SFunction<ExperimentIndicatorInstanceRsEntity, String> getColId() {
        return ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentIndicatorInstanceRsEntity item) {
        return item::setExperimentIndicatorInstanceId;
    }

    @Override
    protected SFunction<ExperimentIndicatorInstanceRsEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentIndicatorInstanceRsEntity item) {
        return null;
    }

    public List<ExperimentIndicatorInstanceRsEntity> getByExperimentPersonId(String experimentPersonId,
                                                                               SFunction<ExperimentIndicatorInstanceRsEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(experimentPersonId)){
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId,experimentPersonId)
                .select(cols)
                .list();

    }

    public boolean updateIndicatorChange(String experimentIndicatorId, Double val) {
        if(ShareUtil.XObject.isEmpty(experimentIndicatorId)){
            return true;
        }
        return service.lambdaUpdate()
                .set(ExperimentIndicatorInstanceRsEntity::getChangeVal,val)
                .eq(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, experimentIndicatorId)
                .update();
    }
    public boolean updateIndicatorChange(List<ExperimentIndicatorInstanceRsEntity> rows){
        if(ShareUtil.XObject.isEmpty(rows)){
            return true;
        }
        boolean rst=true;
        for(ExperimentIndicatorInstanceRsEntity item: rows) {
            rst &= updateIndicatorChange(item.getExperimentIndicatorInstanceId(), item.getChangeVal());
        }
        return rst;
    }
}
