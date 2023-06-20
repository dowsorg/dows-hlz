package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.dto.ExptCaseNoticeDTO;
import org.dows.hep.biz.user.experiment.ExperimentCaseInfoBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description project descr:实验:实验社区基本信息
 * @folder user-hep/实验社区基本信息
 * @author lait.zhang
 * @date 2023年4月23日 上午9:44:34
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "实验社区基本信息", description = "实验社区基本信息")
public class ExperimentCaseInfoRest {
    private final ExperimentCaseInfoBiz experimentCaseInfoBiz;

    /**
     * 获取社区背景
     * @param
     * @return
     */
    @Operation(summary = "获取社区背景")
    @GetMapping("v1/userExperiment/experimentCaseInfo/getDescr")
    public String getDescr(@RequestParam String experimentInstanceId) {
       return experimentCaseInfoBiz.getDescr(experimentInstanceId);
    }

    /**
     * 获取社区帮助
     * @param
     * @return
     */
    @Operation(summary = "获取社区帮助")
    @GetMapping("v1/userExperiment/experimentCaseInfo/getGuide")
    public String getGuide(@RequestParam String experimentInstanceId) {
        return experimentCaseInfoBiz.getGuide(experimentInstanceId);
    }

    /**
     * 获取社区公告
     * @param
     * @return
     */
    @Operation(summary = "获取社区公告")
    @GetMapping("v1/userExperiment/experimentCaseInfo/getNotice")
    public ExptCaseNoticeDTO getNotice(@RequestParam String experimentInstanceId) {
        return experimentCaseInfoBiz.getNotice(experimentInstanceId);
    }

}
