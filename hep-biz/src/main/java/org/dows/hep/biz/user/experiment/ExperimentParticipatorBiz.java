package org.dows.hep.biz.user.experiment;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.model.PageInfo;
import org.dows.hep.api.tenant.experiment.request.PageExperimentRequest;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExperimentParticipatorBiz {
    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验小组
    private final ExperimentGroupService experimentGroupService;

    /**
     * @param
     * @return
     * @说明: 学生端分页实验列表
     * @关联表: ExperimentInstance
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public PageInfo<ExperimentListResponse> page(PageExperimentRequest pageExperimentRequest) {
        Page page = new Page<ExperimentParticipatorEntity>();
        page.setSize(pageExperimentRequest.getPageSize());
        page.setCurrent(pageExperimentRequest.getPageNo());


        if (StrUtil.isBlank(pageExperimentRequest.getOrderBy())) {
            page.addOrder(pageExperimentRequest.isDesc() ?
                    OrderItem.desc(pageExperimentRequest.getOrderBy()) : OrderItem.asc(pageExperimentRequest.getOrderBy()));
        }

        if (!StrUtil.isBlank(pageExperimentRequest.getAccountId())) {
            page = experimentParticipatorService.page(page, experimentParticipatorService.lambdaQuery()
                    .eq(ExperimentParticipatorEntity::getAccountId, pageExperimentRequest.getAccountId())
                    .ne(ExperimentParticipatorEntity::getParticipatorType, 0)
                    .getWrapper());
        } else {
            page = page.setTotal(0).setCurrent(0).setSize(0);
        }
        PageInfo pageInfo = experimentParticipatorService.getPageInfo(page, ExperimentListResponse.class);
        return pageInfo;
    }
}
