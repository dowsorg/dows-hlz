package org.dows.hep.biz.operate;


import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.enums.EnumOrgFeeType;
import org.dows.hep.entity.OperateCostEntity;
import org.dows.hep.service.OperateCostService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * @param
     * @return
     * @说明: 获取实验人物某期的花费,但不包括保险费
     * @关联表: operate_cost
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年7月25日 下午14:40:34
     */
    public List<OperateCostEntity> getPeriodsCostNotInsurance(CostRequest request) {
        List<OperateCostEntity> costEntityList = operateCostService.lambdaQuery()
                .eq(OperateCostEntity::getPatientId,request.getPatientId())
                .ne(OperateCostEntity::getFeeCode, EnumOrgFeeType.BXF.getCode())
                .eq(OperateCostEntity::getPeriod,request.getPeriod())
                .eq(OperateCostEntity::getDeleted,false)
                .list();
        return costEntityList;
    }
}
