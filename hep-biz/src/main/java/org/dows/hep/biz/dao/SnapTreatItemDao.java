package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapTreatItemEntity;
import org.dows.hep.service.snapshot.SnapTreatItemService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/19 0:30
 */
@Component
public class SnapTreatItemDao extends BaseDao<SnapTreatItemService, SnapTreatItemEntity> {
    public SnapTreatItemDao() {
        super("治疗方案不存在");
    }

    @Override
    protected SFunction<SnapTreatItemEntity, String> getColId() {
        return SnapTreatItemEntity::getTreatItemId;
    }

    @Override
    protected SFunction<String, ?> setColId(SnapTreatItemEntity item) {
        return item::setTreatItemId;
    }

    @Override
    protected SFunction<SnapTreatItemEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SnapTreatItemEntity item) {
        return null;
    }

    public List<SnapTreatItemEntity> getByExperimentId(String experimentId,
                                                       SFunction<SnapTreatItemEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(experimentId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(SnapTreatItemEntity::getExperimentInstanceId, experimentId)
                .orderByAsc(SnapTreatItemEntity::getId)
                .select(cols)
                .list();
    }
}
