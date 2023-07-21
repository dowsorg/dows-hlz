package org.dows.hep.rest.report.pdf;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.constant.RedisKeyConst;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.vo.report.ExptGroupReportVO;
import org.dows.hep.vo.report.ExptReportVO;
import org.dows.hep.api.user.experiment.ExptSettingModeEnum;
import org.dows.hep.biz.report.pdf.ExptOverviewReportBiz;
import org.dows.hep.biz.report.pdf.ExptSandReportBiz;
import org.dows.hep.biz.report.pdf.ExptSchemeReportBiz;
import org.dows.hep.biz.report.pdf.ReportZipHelper;
import org.dows.hep.biz.user.experiment.ExperimentSettingBiz;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author fhb
 * @version 1.0
 * @folder report/pdf
 * @description 报告管理-pdf形式
 * @date 2023/7/6 16:38
 **/
@RequiredArgsConstructor
@RestController
@Tag(name = "pdf报告管理", description = "pdf报告管理")
public class ExptReportPdfRest {
    private final RedissonClient redissonClient;
    private final ExperimentInstanceService experimentInstanceService;
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExptSchemeReportBiz exptSchemeReportBiz;
    private final ExptSandReportBiz exptSandReportBiz;
    private final ExptOverviewReportBiz exptOverviewReportBiz;
    private final ReportZipHelper reportZipHelper;

    /**
     * 导出小组实验pdf报告
     * @param experimentInstanceId - 实验实例ID
     * @param experimentGroupId - 实验小组ID
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 导出小组实验pdf报告
     * @date 2023/7/18 10:15
     */
    @Operation(summary = "导出小组实验pdf报告")
    @GetMapping(value = "/v1/report/pdf/exportGroupReport")
    public ExptReportVO exportGroupReport(@RequestParam String experimentInstanceId, @RequestParam String experimentGroupId) {
        // 获取实验信息
        ExperimentInstanceEntity exptEntity = checkExpt(experimentInstanceId);
        String fileName = exptEntity.getId() + SystemConstant.SPLIT_UNDER_LINE + exptEntity.getExperimentName() + SystemConstant.SPLIT_UNDER_LINE + experimentGroupId + SystemConstant.SUFFIX_ZIP;

        /*todo 分布式锁*/
        RLock lock = redissonClient.getLock(RedisKeyConst.HM_LOCK_REPORT + experimentGroupId);
        try {
            if (lock.tryLock(-1, 10, TimeUnit.SECONDS)) {
                ExptReportVO exptReportVO = generatePdf(experimentInstanceId, experimentGroupId);
                exptReportVO.setZipName(fileName);
                reportZipHelper.upload(exptReportVO);
                return exptReportVO;
            } else {
                throw new BizException("报告生成中");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return ExptReportVO.emptyVO();
    }

    /**
     * 导出实验pdf报告
     * @param experimentInstanceId - 实验实例ID
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 导出实验pdf报告
     * @date 2023/7/18 10:16
     */
    @Operation(summary = "导出实验pdf报告")
    @GetMapping(value = "/v1/report/pdf/exportExptReport")
    public ExptReportVO exportExptReport(@RequestParam String experimentInstanceId) throws InterruptedException {
        // check
        ExperimentInstanceEntity exptEntity = checkExpt(experimentInstanceId);
        String fileName = exptEntity.getId() + SystemConstant.SPLIT_UNDER_LINE + exptEntity.getExperimentName() + SystemConstant.SUFFIX_ZIP;

        /*todo 分布式锁*/
        RLock lock = redissonClient.getLock(RedisKeyConst.HM_LOCK_REPORT + experimentInstanceId);
        try {
            if (lock.tryLock(-1, 30, TimeUnit.SECONDS)) {
                ExptReportVO exptReportVO = generatePdf(experimentInstanceId, null);
//                exptReportVO.setZipPath(zipPath);
                exptReportVO.setZipName(fileName);
                reportZipHelper.upload(exptReportVO);
                return exptReportVO;
            } else {
                throw new BizException("报告生成中");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return ExptReportVO.emptyVO();
    }

    private ExperimentInstanceEntity checkExpt(String experimentInstanceId) {
        ExperimentInstanceEntity exptInstance = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .oneOpt()
                .orElseThrow(() -> new BizException("实验不存在"));
        Integer state = exptInstance.getState();
        // TODO 测试结束后，放开注释
//        if (state < EnumExperimentState.FINISH.getState()) {
//            throw new BizException("实验还未结束，请等待");
//        }
        return exptInstance;
    }

    // 以小组为单位导出PDF
    private ExptReportVO generatePdf(String experimentInstanceId, String experimentGroupId) {
        List<ExptGroupReportVO> exptGroupReportVOS = new ArrayList<>();
        ExptReportVO result = ExptReportVO.builder()
                .groupReportList(exptGroupReportVOS)
                .build();

        ExptSettingModeEnum exptSettingMode = experimentSettingBiz.getExptSettingMode(experimentInstanceId);
        switch (exptSettingMode) {
            case SCHEME -> {
                ExptReportVO schemeReportVO = exptSchemeReportBiz.generatePdfReport(experimentInstanceId, experimentGroupId);
                exptGroupReportVOS.addAll(schemeReportVO.getGroupReportList());
            }
            case SAND -> {
                ExptReportVO sandReportVO = exptSandReportBiz.generatePdfReport(experimentInstanceId, experimentGroupId);
                exptGroupReportVOS.addAll(sandReportVO.getGroupReportList());
            }
            case SAND_SCHEME -> {
                ExptReportVO schemeReportVO = exptSchemeReportBiz.generatePdfReport(experimentInstanceId, experimentGroupId);
                ExptReportVO sandReportVO = exptSandReportBiz.generatePdfReport(experimentInstanceId, experimentGroupId);
                exptGroupReportVOS.addAll(schemeReportVO.getGroupReportList());
                exptGroupReportVOS.addAll(sandReportVO.getGroupReportList());
            }
        }
        // 实验总报告
        ExptReportVO overviewReportVO = exptOverviewReportBiz.generatePdfReport(experimentInstanceId, experimentGroupId);
        exptGroupReportVOS.addAll(overviewReportVO.getGroupReportList());

        return result;
    }
}
