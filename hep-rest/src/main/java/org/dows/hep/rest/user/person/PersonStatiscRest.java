package org.dows.hep.rest.user.person;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.ExperimentPersonRequest;
import org.dows.hep.api.user.experiment.response.ExperimentOrgResponse;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.biz.user.person.PersonStatiscBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @folder user
 * @author jx
 * @date 2023/5/8 13:31
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "案例人物数据统计", description = "案例人物数据统计")
public class PersonStatiscRest {
    private final PersonStatiscBiz personStatiscBiz;

    /**
     * @param
     * @return
     * @说明: 获取社区人数(某案例中的所有开启的机构的所有开启的案例人物)
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 上午13:57:34
     */
    @Operation(summary = "获取社区人数")
    @PostMapping("v1/basePerson/personManage/countExperimentPersons")
    public Integer countExperimentPersons(@RequestParam @Validated String experimentInstanceId) {
        return personStatiscBiz.countExperimentPersons(experimentInstanceId);
    }

    /**
     * @param
     * @return
     * @说明: 获取实验机构
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 下午15:40:34
     */
    @Operation(summary = "获取实验机构")
    @PostMapping("v1/basePerson/personManage/listExperimentOrgs")
    public List<ExperimentOrgResponse> listExperimentOrgs(@RequestParam @Validated String experimentInstanceId,
                                                          @RequestParam @Validated String experimentGroupId) {
        return personStatiscBiz.listExperimentOrgs(experimentInstanceId,experimentGroupId);
    }

    /**
     * @param
     * @return
     * @说明: 获取参与者信息
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 下午16:33:34
     */
    @Operation(summary = "获取参与者信息")
    @GetMapping("v1/basePerson/personManage/getParticipatorInfo/{experimentParticipatorId}")
    public ExperimentParticipatorResponse getParticipatorInfo(@PathVariable @Validated String experimentParticipatorId) {
        return personStatiscBiz.getParticipatorInfo(experimentParticipatorId);
    }

    /**
     * @param
     * @return
     * @说明: 每期结束后，每个人物如果有购买保险的话，就返回报销比例
     * @关联表:
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年7月25日 下午14:07:34
     */
    @Operation(summary = "每期结束后，每个人物如果有购买保险的话，就返回报销比例")
    @PostMapping("v1/basePerson/personManage/refundFunds")
    public void refundFunds(@RequestBody @Validated ExperimentPersonRequest request) {
        personStatiscBiz.refundFunds(request);
    }
}
