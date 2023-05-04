package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.RequiredArgsConstructor;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.service.IndicatorFuncService;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/4/26 10:12
 */
@Component
@RequiredArgsConstructor
public class IndicatorFuncDao {

    private final IndicatorFuncService indicatorFuncService;
    private final static SFunction<IndicatorFuncEntity,?> COLLogicKey=IndicatorFuncEntity::getIndicatorFuncId;

    public Optional<IndicatorFuncEntity> getById(String indicatorFuncId, SFunction<IndicatorFuncEntity,?>... cols){
        return indicatorFuncService.lambdaQuery()
                .eq(COLLogicKey,indicatorFuncId)
                .select(cols)
                .oneOpt();
    }
}
