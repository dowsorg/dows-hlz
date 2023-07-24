package org.dows.hep.biz.operate;


import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.dows.hep.entity.OperateCostEntity;
import org.dows.hep.service.OperateCostService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OperateCostBiz {
    private final OperateCostService operateCostService;
    private final IdGenerator idGenerator;

    public String saveCost(CostRequest costRequest) {

        OperateCostEntity operateCostEntity = BeanUtil.copyProperties(costRequest, OperateCostEntity.class);
        operateCostEntity.setOperateCostId(idGenerator.nextIdStr());
        operateCostService.save(operateCostEntity);
        return operateCostEntity.getOperateCostId();

    }


}
