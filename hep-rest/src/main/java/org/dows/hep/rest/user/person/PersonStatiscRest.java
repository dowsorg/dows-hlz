package org.dows.hep.rest.user.person;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.account.response.AccountOrgResponse;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.biz.user.person.PersonStatiscBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
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
    @PostMapping("v1/basePerson/personManage/countCasePersons")
    public Integer countCasePersons(@RequestParam @Validated String caseInstanceId) {
        return personStatiscBiz.countCasePersons(caseInstanceId);
    }

    /**
     * @param
     * @return
     * @说明: 获取案例机构
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 下午15:40:34
     */
    @Operation(summary = "获取案例机构")
    @PostMapping("v1/basePerson/personManage/countCaseOrgs")
    public List<AccountOrgResponse> countCaseOrgs(@RequestParam @Validated String caseInstanceId) {
        return personStatiscBiz.countCaseOrgs(caseInstanceId);
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
}
