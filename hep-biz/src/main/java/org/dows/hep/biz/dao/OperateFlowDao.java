package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.user.experiment.request.FindOrgReportRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.OperateFlowEntity;
import org.dows.hep.entity.OperateFlowSnapEntity;
import org.dows.hep.service.OperateFlowService;
import org.dows.hep.service.OperateFlowSnapService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/6/4 9:32
 */
@Component
public class OperateFlowDao extends BaseSubDao<OperateFlowService, OperateFlowEntity, OperateFlowSnapService, OperateFlowSnapEntity>
        implements IPageDao<OperateFlowEntity, FindOrgReportRequest>{

    public OperateFlowDao(){
        super("未找到挂号记录，请刷新");
    }

    @Override
    protected SFunction<OperateFlowEntity, String> getColCateg() {
        return null;
    }

    @Override
    protected SFunction<OperateFlowEntity, String> getColId() {
        return OperateFlowEntity::getOperateFlowId;
    }

    @Override
    protected SFunction<String, ?> setColId(OperateFlowEntity item) {
        return item::setOperateFlowId;
    }

    @Override
    protected SFunction<OperateFlowEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(OperateFlowEntity item) {
        return null;
    }

    @Override
    protected SFunction<OperateFlowSnapEntity, String> getColLeadId() {
        return OperateFlowSnapEntity::getOperateFlowId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(OperateFlowSnapEntity item) {
        return item::setOperateFlowId;
    }

    @Override
    protected SFunction<OperateFlowSnapEntity, String> getColSubId() {
        return OperateFlowSnapEntity::getOperateFlowSnapId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(OperateFlowSnapEntity item) {
        return item::setOperateFlowSnapId;
    }

    @Override
    public IPage<OperateFlowEntity> pageByCondition(FindOrgReportRequest req, SFunction<OperateFlowEntity, ?>... cols) {
        Page<OperateFlowEntity> page = Page.of(req.getPageNo(), req.getPageSize());
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(req.getAppId()), OperateFlowEntity::getAppId,req.getAppId())
                .eq(OperateFlowEntity::getExperimentOrgId, req.getExperimentOrgId())
                .eq(ShareUtil.XObject.notEmpty(req.getExperimentPersonId()), OperateFlowEntity::getExperimentPersonId,req.getExperimentPersonId())
                .ge(OperateFlowEntity::getReportFlag,1)
                .eq(ShareUtil.XObject.notEmpty(req.getPeriods(),true),OperateFlowEntity::getPeriods,req.getPeriods())
                .orderByDesc(OperateFlowEntity::getId)
                .select(cols)
                .page(page);
    }

    /**
     * 获取人物最新挂号记录
     * @param experimentPersonId
     * @param experimentOrgId
     * @param periods
     * @param cols
     * @return
     */
    public Optional<OperateFlowEntity> getCurrrentFlow(String experimentPersonId, String experimentOrgId, Integer periods,
                                                       SFunction<OperateFlowEntity,?>... cols){
        return service.lambdaQuery()
                .eq(OperateFlowEntity::getExperimentPersonId,experimentPersonId)
                .eq(OperateFlowEntity::getExperimentOrgId,experimentOrgId)
                .eq(ShareUtil.XObject.notEmpty(periods, true),OperateFlowEntity::getPeriods,periods)
                .le(OperateFlowEntity::getReportFlag,1)
                .orderByDesc(OperateFlowEntity::getPeriods, OperateFlowEntity::getId)
                .select(cols)
                .last(" limit 1")
                .oneOpt();
    }

    /**
     * 获取机构挂号报告
     * @param operateFlowId
     * @param cols
     * @return
     */

    public Optional<OperateFlowSnapEntity> getReportSnapByFlowId(String operateFlowId,SFunction<OperateFlowSnapEntity,?>... cols){
        return subService.lambdaQuery()
                .eq(OperateFlowSnapEntity::getOperateFlowId, operateFlowId)
                .orderByDesc(OperateFlowSnapEntity::getId)
                .select(cols)
                .last(" limit 1")
                .oneOpt();
    }

    /**
     * 按人物列表获取挂号状态
     * @param experimentOrgId
     * @param experimentPersonIds
     * @param periods
     * @param cols
     * @return
     */

    public List<OperateFlowEntity> getCurrentFlowList(String experimentOrgId,List<String> experimentPersonIds,Integer periods,
                                                      SFunction<OperateFlowEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(experimentPersonIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=experimentPersonIds.size()==1;
        return service.lambdaQuery()
                .select(cols)
                .eq(OperateFlowEntity::getExperimentOrgId,experimentOrgId)
                .eq(oneFlag, OperateFlowEntity::getExperimentPersonId,experimentPersonIds.iterator().next())
                .in(!oneFlag, OperateFlowEntity::getExperimentPersonId,experimentPersonIds)
                .ge(ShareUtil.XObject.notEmpty(periods, true),OperateFlowEntity::getPeriods,periods)
                .le(OperateFlowEntity::getReportFlag,1)
                .isNull(OperateFlowEntity::getEndTime)
                .orderByDesc(OperateFlowEntity::getPeriods, OperateFlowEntity::getId)
                .select(cols)
                .list();
    }


}
