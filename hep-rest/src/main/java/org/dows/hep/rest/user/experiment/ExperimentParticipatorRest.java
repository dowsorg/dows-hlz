package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.model.PageInfo;
import org.dows.hep.api.tenant.experiment.request.PageExperimentRequest;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.api.user.experiment.request.GetExperimentGroupCaptainRequest;
import org.dows.hep.api.user.experiment.response.GetExperimentGroupCaptainResponse;
import org.dows.hep.biz.user.experiment.ExperimentParticipatorBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * 分页获取实验参与者列表
     *
     * @param
     * @return
     */
    @Operation(summary = "分页获取实验参与者列表")
    @PostMapping("v1/user/experimentParticipator/page")
    public PageInfo<ExperimentListResponse> page(@RequestBody @Validated PageExperimentRequest pageExperimentRequest) {
        return experimentParticipatorBiz.page(pageExperimentRequest);
    }


    /**
     * 获取实验列表
     *
     * @param
     * @return
     */
    @Operation(summary = "获取实验队长信息")
    @GetMapping("v1/user/experimentParticipator/getCaptain")
    public GetExperimentGroupCaptainResponse getCaptain(GetExperimentGroupCaptainRequest getExperimentGroupCaptainRequest) {
        return experimentParticipatorBiz.getExperimentGroupCaptain(getExperimentGroupCaptainRequest);
    }
}
