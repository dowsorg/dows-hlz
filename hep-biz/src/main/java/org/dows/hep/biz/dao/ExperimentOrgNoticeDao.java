package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.core.BaseExptRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentOrgNoticeEntity;
import org.dows.hep.service.ExperimentOrgNoticeService;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/7/7 19:18
 */
@Component
public class ExperimentOrgNoticeDao extends BaseDao<ExperimentOrgNoticeService,ExperimentOrgNoticeEntity>
        implements IPageDao<ExperimentOrgNoticeEntity, BaseExptRequest> {
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
    public IPage<ExperimentOrgNoticeEntity> pageByCondition(BaseExptRequest req, SFunction<ExperimentOrgNoticeEntity, ?>... cols) {
        Page<ExperimentOrgNoticeEntity> page = Page.of(req.getPageNo(), req.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(req.getAppId()), ExperimentOrgNoticeEntity::getAppId, req.getAppId())
                .eq(ExperimentOrgNoticeEntity::getExperimentOrgId, req.getExperimentOrgId())
                .eq(ExperimentOrgNoticeEntity::getExperimentGroupId, req.getExperimentGroupId())
                .eq(ShareUtil.XObject.notEmpty(req.getExperimentPersonId()),ExperimentOrgNoticeEntity::getExperimentPersonId,req.getExperimentPersonId())
                .select(cols)
                .page(page);
    }
}
