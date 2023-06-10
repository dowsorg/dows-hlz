package org.dows.hep.rest.user.experiment;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.model.PageInfo;
import org.dows.hep.api.tenant.experiment.request.PageExperimentRequest;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.biz.user.experiment.ExperimentParticipatorBiz;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "实验参与者", description = "实验参与者")
public class ExperimentParticipatorRest {
    // 实验参与者
    private final ExperimentParticipatorBiz experimentParticipatorBiz;

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

    /**
     * 获取实验列表
     *
     * @param
     * @return
     */
    @Operation(summary = "分页获取实验参与者列表")
    @GetMapping("v1/tenantExperiment/experimentParticipator/page")
    public PageInfo<ExperimentListResponse> page(PageExperimentRequest pageExperimentRequest) {
        return experimentParticipatorBiz.page(pageExperimentRequest);
    }
}
