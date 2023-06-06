package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.OperateOrgFuncEntity;
import org.dows.hep.entity.OperateOrgFuncSnapEntity;
import org.dows.hep.service.OperateOrgFuncService;
import org.dows.hep.service.OperateOrgFuncSnapService;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/6/4 9:19
 */
@Component
public class OperateOrgFuncDao extends BaseSubDao<OperateOrgFuncService, OperateOrgFuncEntity, OperateOrgFuncSnapService, OperateOrgFuncSnapEntity>
    implements IPageDao<OperateOrgFuncEntity, ExptOrgFuncRequest>{

    public OperateOrgFuncDao(){
        super("未找到操作记录，请刷新");
    }

    @Override
    protected SFunction<OperateOrgFuncEntity, String> getColCateg() {
        return null;
    }

    @Override
    protected SFunction<OperateOrgFuncEntity, String> getColId() {
        return OperateOrgFuncEntity::getOperateOrgFuncId;
    }

    @Override
    protected SFunction<String, ?> setColId(OperateOrgFuncEntity item) {
        return item::setOperateOrgFuncId;
    }

    @Override
    protected SFunction<OperateOrgFuncEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(OperateOrgFuncEntity item) {
        return null;
    }

    @Override
    protected SFunction<OperateOrgFuncSnapEntity, String> getColLeadId() {
        return OperateOrgFuncSnapEntity::getOperateOrgFuncId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(OperateOrgFuncSnapEntity item) {
        return item::setOperateOrgFuncId;
    }

    @Override
    protected SFunction<OperateOrgFuncSnapEntity, String> getColSubId() {
        return OperateOrgFuncSnapEntity::getOperateOrgFuncSnapId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(OperateOrgFuncSnapEntity item) {
        return item::setOperateOrgFuncSnapId;
    }

    @Override
    public IPage<OperateOrgFuncEntity> pageByCondition(ExptOrgFuncRequest req, SFunction<OperateOrgFuncEntity, ?>... cols) {
        return null;
    }

    /**
     * 获取人物最新机构操作记录
     * @param experimentPersonId
     * @param experimentOrgId
     * @param indicatorFuncId
     * @param periods
     * @param cols
     * @return
     */
    public Optional<OperateOrgFuncEntity> getCurrentOrgFuncRecord(String experimentPersonId, String experimentOrgId, String indicatorFuncId,
                                                               Integer periods,SFunction<OperateOrgFuncEntity,?>... cols) {
        return service.lambdaQuery()
                .eq(OperateOrgFuncEntity::getExperimentPersonId,experimentPersonId)
                .eq(OperateOrgFuncEntity::getExperimentOrgId,experimentOrgId)
                .eq(ShareUtil.XObject.notEmpty(indicatorFuncId),OperateOrgFuncEntity::getIndicatorFuncId,indicatorFuncId)
                .eq(ShareUtil.XObject.notEmpty(periods, true),OperateOrgFuncEntity::getPeriods,periods)
                .orderByDesc(OperateOrgFuncEntity::getId)
                .select(cols)
                .last("limit 1")
                .oneOpt();

    }
}
