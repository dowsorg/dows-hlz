package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.enums.EnumExperimentOrgNoticeType;
import org.dows.hep.api.user.experiment.request.FindOrgNoticeRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentOrgNoticeEntity;
import org.dows.hep.service.ExperimentOrgNoticeService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/7 19:18
 */
@Component
public class ExperimentOrgNoticeDao extends BaseDao<ExperimentOrgNoticeService,ExperimentOrgNoticeEntity>
        implements IPageDao<ExperimentOrgNoticeEntity, FindOrgNoticeRequest> {
    public ExperimentOrgNoticeDao() {
        super("机构通知不存在");
    }

    @Override
    protected SFunction<ExperimentOrgNoticeEntity, String> getColId() {
        return ExperimentOrgNoticeEntity::getExperimentOrgNoticeId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentOrgNoticeEntity item) {
        return item::setExperimentOrgNoticeId;
    }

    @Override
    protected SFunction<ExperimentOrgNoticeEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentOrgNoticeEntity item) {
        return null;
    }


    @Override
    public IPage<ExperimentOrgNoticeEntity> pageByCondition(FindOrgNoticeRequest req, SFunction<ExperimentOrgNoticeEntity, ?>... cols) {
        Page<ExperimentOrgNoticeEntity> page = Page.of(req.getPageNo(), req.getPageSize());
        if (ShareUtil.XObject.isEmpty(req.getExperimentPersonIds())) {
            return page;
        }
        final boolean oneFlag = req.getExperimentPersonIds().size() == 1;
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(req.getAppId()), ExperimentOrgNoticeEntity::getAppId, req.getAppId())
                //.eq(ExperimentOrgNoticeEntity::getExperimentOrgId, req.getExperimentOrgId())
                //.eq(ExperimentOrgNoticeEntity::getExperimentGroupId, req.getExperimentGroupId())
                .eq(oneFlag, ExperimentOrgNoticeEntity::getExperimentPersonId, req.getExperimentPersonIds().get(0))
                .in(!oneFlag, ExperimentOrgNoticeEntity::getExperimentPersonId, req.getExperimentPersonIds())
                .and(i -> i.eq(ExperimentOrgNoticeEntity::getNoticeSrcType, EnumExperimentOrgNoticeType.EVENTTriggered.getCode())
                        .or()
                        .in(ShareUtil.XObject.notEmpty(req.getFollowUpNoticeIds()), ExperimentOrgNoticeEntity::getExperimentOrgNoticeId, req.getFollowUpNoticeIds())
                )
                .orderByDesc(ExperimentOrgNoticeEntity::getNoticeTime)
                .select(cols)
                .page(page);
    }

    public List<ExperimentOrgNoticeEntity> getTopFollowUpNoticeIds(List<String> experimentPersonIds){
        if(ShareUtil.XObject.isEmpty(experimentPersonIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=experimentPersonIds.size()==1;
        return service.query()
                .select("experiment_person_id, max(experiment_org_notice_id) experiment_org_notice_id")
                .groupBy("experiment_person_id")
                .list();
    }

    public ExperimentOrgNoticeEntity getTopFollowupNotice(String experimentPersonId
            ,SFunction<ExperimentOrgNoticeEntity,?>...cols){
        return service.lambdaQuery()
                .eq(ExperimentOrgNoticeEntity::getExperimentPersonId,experimentPersonId)
                .orderByDesc(ExperimentOrgNoticeEntity::getId)
                .select(cols)
                .last("limit 1")
                .one();
    }

    public boolean setTopFollowupNoticeAction(String noticeId,Integer actionState){
        return service.lambdaUpdate()
                .eq(ExperimentOrgNoticeEntity::getExperimentOrgNoticeId,noticeId)
                .set(ExperimentOrgNoticeEntity::getActionState,actionState)
                .update();
    }
}
