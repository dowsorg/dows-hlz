package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.CaseOrgFeeEntity;
import org.dows.hep.service.CaseOrgFeeService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/6/4 12:26
 */
@Component
public class CaseOrgFeeDao extends BaseDao<CaseOrgFeeService, CaseOrgFeeEntity>{
    protected CaseOrgFeeDao(){
        super("未找到机构费用设置");
    }

    @Override
    protected SFunction<CaseOrgFeeEntity, String> getColId() {
        return CaseOrgFeeEntity::getCaseOrgFeeId;
    }

    @Override
    protected SFunction<String, ?> setColId(CaseOrgFeeEntity item) {
        return item::setCaseOrgFeeId;
    }

    @Override
    protected SFunction<CaseOrgFeeEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(CaseOrgFeeEntity item) {
        return null;
    }

    /**
     * 获取机构费用
     * @param orgId
     * @param feeCode
     * @param cols
     * @return
     */
    public Optional<CaseOrgFeeEntity> getFee(String orgId,String feeCode,SFunction<CaseOrgFeeEntity,?>... cols){

        return service.lambdaQuery()
                .eq(CaseOrgFeeEntity::getCaseOrgId,orgId)
                .eq(CaseOrgFeeEntity::getFeeCode,feeCode)
                .select(cols)
                .last("limit 1")
                .oneOpt();
    }

    /**
     * 获取机构费用列表
     * @param orgId
     * @param cols
     * @return
     */
    public List<CaseOrgFeeEntity> getFeeList(String orgId,SFunction<CaseOrgFeeEntity,?>... cols){

        return service.lambdaQuery()
                .eq(CaseOrgFeeEntity::getCaseOrgId,orgId)
                .select(cols)
                .list();
    }


}
